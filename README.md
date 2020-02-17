# PerformNetverifyBatch

Command-line client that can be used by prospect merchants to test Netverify services.

## Getting Started

### Install Java JDK

Java JDK is needed to compile the program. Please download [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

Please make sure the JAVA_HOME environment variable is set to JDK.

### Install Maven

Maven can be downloaded and installed directly [here](http://maven.apache.org/download.html).

On PC, please follow instruction [here](https://maven.apache.org/install.html).

On Mac, Maven can also be installed through Homebrew. Please check [here](https://brew.sh/) for Homebrew installation.

```
$ brew install maven
```
### Download or clone the project directory

Download the zip file below and extract the content.

```
https://github.com/JumioServices/PerformNetverifyBatch-Java/archive/master.zip
```

Or use below command to clone in terminal.

```
$ git clone https://github.com/JumioServices/PerformNetverifyBatch-Java.git
```

### Clean the project directory

Use below command to make sure the environment is clean. Open the command prompt or terminal and enter below.

```
$ mvn clean
```

### Build the program

At the project's root directory, use Maven to compile the program.

```
$ mvn compile assembly:single
```

After compilation, 'PerformNetverify-1.0-SNAPSHOT-jar-with-dependencies.jar' should appear in the 'target' directory. This file is the compiled Java program.

## Running the program

### Edit config.properties

Below are the parameters that can be customized. When parameter value is specified both in config file and the command line, the command line value takes precedence.

Name|Command Line Arg|Example
---|---|---
serverUrl                   |no     |https://netverify.com/api/netverify/v2/performNetverify
token                       |yes    |
secret                      |yes    |
userAgent                   |no     |Jumio performNetverifyBatch/1.0
merchantIdScanReference     |no     |Jumio NV Test Tool
merchantReportingCriteria   |no     |Batch
country                     |yes    |USA
idType                      |yes    |DRIVING_LICENSE
pathToImageFolder           |yes    |images
numberToSubmit              |yes    |1000
frontSuffix                 |no     |front
backSuffix                  |no     |back
faceSuffix                  |no     |face
enabledFields               |no     |idNumber,idFirstName,idLastName,idDob,idExpiry,idUsState,idPersonalNumber,idAddress,idFaceMatch
faceImageRequired           |no     |true
backImageRequired           |no     |true

### Set API token/secret in environment variables (optional)

Netverify requires authentication through API token and secret. They can be stored in environment variables for easy access.

```
$ export API_TOKEN=********
$ export API_SECRET=********
```

### Execution

1. Name the front side of the ID with 'frontSuffix'. For example, 'User12345_front.jpg'.

2. Name the back side of the ID with 'backSuffix'. For example, 'User12345_back.jpg'.

3. Name the selfie with 'faceSuffix'. For example, 'User12345_face.jpg'.

4. Move the images to a new folder named 'images' in the root folder.

5. Use below command to run the program.

```
$ java -jar target/PerformNetverify-1.0-SNAPSHOT-jar-with-dependencies.jar token=$API_TOKEN secret=$API_SECRET 
```

6. Exam terminal output and output.csv for the new scan ref.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
