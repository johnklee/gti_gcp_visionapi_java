## gti_gcp_visionapi_java
This repo is used as exploration of [GCP Vision Java Client API](https://cloud.google.com/vision/docs/libraries)

## Initialization

### Setting up authorization
To run the client library, you must first set up authentication by creating a service account and setting an environment variable. Complete the following steps to set up authentication. For other ways to authenticate, see the [GCP authentication documentation](https://cloud.google.com/docs/authentication/production).

Provide authentication credentials to your application code by setting the environment variable `GOOGLE_APPLICATION_CREDENTIALS`. Replace `**[PATH]**` with the file path of the JSON file that contains your service account key. This variable only applies to your current shell session, so if you open a new session, set the variable again:
```bash
export GOOGLE_APPLICATION_CREDENTIALS="[PATH]"
```
For example:
```console
# export GOOGLE_APPLICATION_CREDENTIALS="`pwd`/just4fun-164308-6dd7cc07d90
# env | grep GOOGLE
GOOGLE_APPLICATION_CREDENTIALS=/root/Github/gti_gcp_visionapi_java/just4fun-164308-6dd7cc07d900.json
```

## Usage

### Build Process
To build this repo, please execute below command:
```console
# make build
...
BUILD SUCCESSFUL

Total time: 0.457 secs
```

### Demo API
To show demonstration of GCP Vision API (`image file -> (Vision API) -> label(s)`), please execute below command:
```console
# make demo
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
which means we obtain 10 labels from given image file `datas/food_ingredients/13.PNG`. If you want to annotate/label your own image file `datas/food_ingredients/15.PNG`, try command below:
```console
# java -cp build/libs/*:build/ext_libs/* gti.ingredientsgenius.APITest datas/food_ingredients/15.PNG
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

## Reference
* [Google API Client Libraries - Easily access Google APIs from Java](https://developers.google.com/api-client-library/java)
* [Google Cloud Vision Client for Java Repo](https://github.com/googleapis/java-vision)
* [Gradle – Create a Jar file with dependencies](https://mkyong.com/gradle/gradle-create-a-jar-file-with-dependencies/)
* [FAQ - Gradle: Saving Gradle Dependencies to a Directory](https://stackoverflow.com/questions/46628910/saving-gradle-dependencies-to-a-directory)
