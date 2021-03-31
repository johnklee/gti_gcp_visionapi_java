package gti.foodgenie.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.common.io.Files;
import com.google.protobuf.ByteString;

public class Image2LabelAgent {
	protected ImageAnnotatorClient vision = null;
	protected float minScore = (float) 0.8;
	protected Set<String> ignoredLabels = new HashSet<String>();
	
	public Image2LabelAgent(String credentialsPath) throws Exception{
		if(credentialsPath != null) {
			File cFile = new File(credentialsPath);
			cFile.toPath().toAbsolutePath().toString();
			if(!cFile.exists() || cFile.isDirectory()) {
				throw new Exception(String.format("%s doesn't exist!", cFile.toPath()));
			}		
			InjectEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", cFile.toPath().toString());
		}		
		this.inti_gcp_vision_client();
		this.load_settings();
	}
	
	public List<String> label(String imageFilePath) throws Exception{
		return this.label(new File(imageFilePath));
	}

	public Map<File, List<String>> batch_label(List<File> imageFiles) throws Exception {
		Map<File, List<String>> rstMap = new TreeMap<File, List<String>>();
		
		// Builds the image annotation request
		List<AnnotateImageRequest> requests = new ArrayList<AnnotateImageRequest>();
		for (File imageFile : imageFiles) {
			byte[] data = Files.toByteArray(imageFile);
			ByteString imgBytes = ByteString.copyFrom(data);
			Image img = Image.newBuilder().setContent(imgBytes).build();
			Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
			AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
			requests.add(request);
		}

		// Performs label detection on the image file
		BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
		List<AnnotateImageResponse> responses = response.getResponsesList();
		int fc = 0;
		for (AnnotateImageResponse res : responses) {
			File targetFile = imageFiles.get(fc++);
			if (res.hasError()) {
				System.out.format("Error: %s%n", res.getError().getMessage());
				rstMap.put(targetFile, null);
				continue;
			}
			List<String> labels = new ArrayList<String>();
			for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
				String label = annotation.getDescription().toLowerCase();
				float score = annotation.getScore();
				if (!ignoredLabels.contains(label) && score > this.minScore) {
					labels.add(label);
				}
			}
			rstMap.put(targetFile, labels);
		}
		return rstMap;
	}
	
	public List<String> label(File imageFile) throws Exception{
		List<String> labels = new ArrayList<String>();
		byte[] data = Files.toByteArray(imageFile);
        ByteString imgBytes = ByteString.copyFrom(data);

        // Builds the image annotation request
        List<AnnotateImageRequest> requests = new ArrayList<AnnotateImageRequest>();
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);
        
        // Performs label detection on the image file
        BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = response.getResponsesList();

        for (AnnotateImageResponse res : responses) {
          if (res.hasError()) {
            System.out.format("Error: %s%n", res.getError().getMessage());
            return null;
          }

          for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
        	  String label = annotation.getDescription().toLowerCase();
        	  float score = annotation.getScore();
        	  if(!ignoredLabels.contains(label) && score > this.minScore) {
        		  labels.add(label);
        	  }
            }
        }
        
		return labels;
	}
	
	protected void load_settings() throws Exception{
		File configFile = new File("setting.properties");
		if(configFile.exists()) {
			Properties appProps = new Properties();
			appProps.load(new FileInputStream(configFile));
			
			// Handle ignored label list
			for(String ignoredLabel:appProps.getProperty("ignored_label_list", "").split(",")) {
				ignoredLabel = ignoredLabel.trim();
				if(ignoredLabel.length() > 0) {
					ignoredLabels.add(ignoredLabel);
				}
			}
		}
	}
	
	protected void inti_gcp_vision_client() throws IOException{
		this.vision = ImageAnnotatorClient.create();
	}
	
	@SuppressWarnings("unchecked")
	private static void InjectEnvironmentVariable(String key, String value)
            throws Exception {

        Class<?> processEnvironment = Class.forName("java.lang.ProcessEnvironment");

        Field unmodifiableMapField = getAccessibleField(processEnvironment, "theUnmodifiableEnvironment");
        Object unmodifiableMap = unmodifiableMapField.get(null);
        injectIntoUnmodifiableMap(key, value, unmodifiableMap);

        Field mapField = getAccessibleField(processEnvironment, "theEnvironment");
        Map<String, String> map = (Map<String, String>) mapField.get(null);
        map.put(key, value);
    }
	
	private static Field getAccessibleField(Class<?> clazz, String fieldName)
            throws NoSuchFieldException {

        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    private static void injectIntoUnmodifiableMap(String key, String value, Object map)
            throws ReflectiveOperationException {

        Class unmodifiableMap = Class.forName("java.util.Collections$UnmodifiableMap");
        Field field = getAccessibleField(unmodifiableMap, "m");
        Object obj = field.get(map);
        ((Map<String, String>) obj).put(key, value);
    }

	public static void main(String[] args) throws Exception{
        // Initialize Image 2 label agent
        Image2LabelAgent vAgent = new Image2LabelAgent(null);

		// Label single image file
        File dataPath = new File("datas/food_ingredients/");
		System.out.printf("===== Label Single Image File ===\n");
		File testImgFile = new File(dataPath, "8.PNG");
		System.out.printf("Labeling %s...\n", testImgFile.toPath().toString());
		for(String label:vAgent.label(testImgFile)) {
			System.out.printf("\t%s\n", label);
		}
		System.out.println();
		
		// Label image files in batch
		System.out.printf("===== Label Image Files In Batch ===\n");
		Map<File, List<String>> rstMap = vAgent.batch_label(Arrays.asList(
				new File(dataPath, "8.PNG"), 
				new File(dataPath, "2.PNG"), 
				new File(dataPath, "5.PNG")
		));
		
		for (Map.Entry<File, List<String>> entry : rstMap.entrySet()) {
	        System.out.printf("Labeling Result of %s:\n", entry.getKey().toPath().toString());
	        for(String label:entry.getValue()) {
	        	System.out.printf("\t%s\n", label);
	        }	        
	    }
		System.out.println();
	}

}
