package gti.ingredientsgenius;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class APITest{
    public static void main(String args[]) throws Exception{
        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        try{
            System.out.printf("Initializing Vision API client object...\n");
            ImageAnnotatorClient vision = ImageAnnotatorClient.create();
            for(String imagePath:args){
                File f = new File(imagePath);
                if(f.exists() && !f.isDirectory()){
                    System.out.printf("Handling image file=%s...\n", imagePath);
                    // Reads the image file into memory
                    Path path = Paths.get(imagePath);
                    byte[] data = Files.readAllBytes(path);
                    ByteString imgBytes = ByteString.copyFrom(data);
                
                    // Builds the image annotation request
                    List<AnnotateImageRequest> requests = new ArrayList<>();
                    Image img = Image.newBuilder().setContent(imgBytes).build();
                    Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
                    AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
                    requests.add(request);

                    // Performs label detection on the image file
                    BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
                    List<AnnotateImageResponse> responses = response.getResponsesList();

                    // Print out annotation
                    int num_label = 0;
                    System.out.printf("Obtained labeling result:\n");
                    for (AnnotateImageResponse res : responses) {
                        if (res.hasError()) {
                            System.err.format("Error: %s%n", res.getError().getMessage());
                            return;
                        }

                        for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                            System.out.printf("\t%s (%.02f)\n", annotation.getDescription(), annotation.getScore());
                            num_label += 1;
                        }
                    }
                    System.out.printf("Total %d label(s) received!\n\n", num_label);
                } else {
                    System.err.printf("Image file=%s does not exist!\n", imagePath);
                }
            }
        } catch(Exception e){
            System.err.printf("Something went wrong: " + e.getMessage());
        } finally {
        }
    }
}
