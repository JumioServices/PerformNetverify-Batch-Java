# PerformNetverifyBatch

Command-line client that can be used by prospect merchants to test NetVerify services. The tool eliminates the need for the merchant to integrate with us before they can test.

## Getting Started

### Install Java JDK

Java JDK is needed to compile the program. Please download [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

### Install Maven

Maven can be downloaded and installed directly [here](http://maven.apache.org/download.html).

On Mac, Maven can also be installed through Homebrew.

```
$ brew install maven
```

### Build the program

At the project's root directory, use Maven to compile the program.

```
$ mvn compile assembly:single
```

After the compilation, 'PerformNetverify-1.0-SNAPSHOT-jar-with-dependencies.jar' should appear in the 'target' directory. This file is the compiled Java program.

## Running the program

### Edit config.properties

Below are the parameters that can be customized. When parameter value is specified both in config file and the command line, the command line value takes precedence.

Name|Command Line Arg|Default
---|---|---
pathToImageFolder |yes	|nil
serverUrl	|yes	|https://netverify.com/api/netverify/v2
enabledFields	|no	|idNumber,idFirstName,idLastName,idDob,idExpiry,idUsState,idPersonalNumber,idAddress,idFaceMatch
merchantReportingCriteria	|no	|Jumio NV Test Tool
faceImageRequired |no  |true
backImageRequired |no  |true
frontSuffix |no |front
backSuffix |no  |back
faceSuffix |no  |face

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

4. Take 'PerformNetverify-1.0-SNAPSHOT-jar-with-dependencies.jar' and copy to the image folder (optional).

5. Use below command to run the program.

```
$ java -jar PerformNetverify-1.0-SNAPSHOT-jar-with-dependencies.jar token=$API_TOKEN secret=$API_SECRET 
```

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
