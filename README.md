## gti_gcp_visionapi_java
This repo is used as exploration of [GCP Vision Java Client API](https://cloud.google.com/vision/docs/libraries)

## Initialization

### Setting up authorization
To run the client library, you must first set up authentication by creating a service account and setting an environment variable. Complete the following steps to set up authentication. For other ways to authenticate, see the [GCP authentication documentation](https://cloud.google.com/docs/authentication/production).

Provide authentication credentials to your application code by setting the environment variable `GOOGLE_APPLICATION_CREDENTIALS`. Replace **`[PATH]`** with the file path of the JSON file that contains your service account key. This variable only applies to your current shell session, so if you open a new session, set the variable again:
```bash
$ export GOOGLE_APPLICATION_CREDENTIALS="[PATH]"
```
For example:
```console
$ export GOOGLE_APPLICATION_CREDENTIALS="`pwd`/just4fun-164308-6dd7cc07d90
$ env | grep GOOGLE
GOOGLE_APPLICATION_CREDENTIALS=/root/Github/gti_gcp_visionapi_java/just4fun-164308-6dd7cc07d900.json
```

## Usage

### Build Process
To build this repo, please execute below command:
```console
$ make build
...
BUILD SUCCESSFUL

Total time: 0.457 secs
```

### Vision API Demo
To show demonstration of GCP Vision API (`image file -> (Vision API) -> label(s)`), please execute below command:
```console
$ make demo
...
./runAPITest.sh
Initializing Vision API client object...
Handling image file=datas/food_ingredients/13.PNG...
Obtained labeling result:
        Food (0.98)
        Plant (0.95)
        Garlic (0.93)
        Natural foods (0.89)
        Plum tomato (0.89)
        Ingredient (0.88)
        Fruit (0.87)
        Bush tomato (0.82)
        Recipe (0.79)
        Vegetable (0.79)
Total 10 label(s) received!
```
which means we obtain 10 labels from given image file `datas/food_ingredients/13.PNG`. For the source code, please refer to `[APITest.java](src/main/java/gti/foodgenie/APITest.java)`


If you want to annotate/label your own image file `datas/food_ingredients/15.PNG`, try command below:
```console
$ java -cp build/libs/*:build/ext_libs/* gti.ingredientsgenius.APITest datas/food_ingredients/15.PNG
Initializing Vision API client object...
Handling image file=datas/food_ingredients/15.PNG...
Obtained labeling result:
        Food (0.98)
        Fruit (0.92)
        Plum tomato (0.91)
        Dishware (0.91)
        Tableware (0.90)
        Cherry Tomatoes (0.89)
        Ingredient (0.89)
        Wood (0.89)
        Recipe (0.88)
        Plate (0.87)
Total 10 label(s) received!
```
### Wrapper API Demo
In order to get rid of the dependency for the future change from Vision API, we create a class **[Image2LabelAgent](src/main/java/gti/foodgenie/core/Image2LabelAgent.java)** to wrap the Vision API and provide below API signature for our RPC service:
```java
	/**
	 * Label the input File `imageFile`
	 * <p>
	 * This method always returns immediately, whether or not the image exists. When
	 * this applet attempts to draw the image on the screen, the data will be
	 * loaded. The graphics primitives that draw the image will incrementally paint
	 * on the screen.
	 *
	 * @param imageFile  Input image file for labeling.
	 * @return The list of label associated with given input image file.
	 */
	public List<String> label(File imageFile) throws Exception    
```
Below is sample code on how to call this API:
```java
import gti.foodgenie.core.Image2LabelAgent;

...
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
```
You can execute below command to check the output of above sample code:
```console
$ make demo_wrapper
...
./runImage2LabelAgent.sh
===== Label Single Image File ===
Labeling datas/food_ingredients/8.PNG...
        garlic
        nut

===== Label Image Files In Batch ===
Labeling Result of datas/food_ingredients/2.PNG:
        leaf
        houseplant
        terrestrial plant
Labeling Result of datas/food_ingredients/5.PNG:
        garlic
        wood
        elephant garlic
Labeling Result of datas/food_ingredients/8.PNG:
        garlic
        nut
```

## Reference
* [Google API Client Libraries - Easily access Google APIs from Java](https://developers.google.com/api-client-library/java)
* [Google Cloud Vision Client for Java Repo](https://github.com/googleapis/java-vision)
* [Gradle – Create a Jar file with dependencies](https://mkyong.com/gradle/gradle-create-a-jar-file-with-dependencies/)
* [FAQ - Gradle: Saving Gradle Dependencies to a Directory](https://stackoverflow.com/questions/46628910/saving-gradle-dependencies-to-a-directory)
* [How to change environment variables in Java](https://blog.sebastian-daschner.com/entries/changing_env_java)
