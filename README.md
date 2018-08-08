# PerformNetverifyBatch

# Summary

Command-line client that can be used by prospect merchants to test NetVerify services. The tool eliminates the need for the merchant to integrate with us before they can test.

Facematch and the back of the id can be enabled by setting the flags in the config.properties file.

The token and secret must be passed in on the command line.

Most of the batch upload projects require minor changes to the Java code.  PerformNetverify.java words for verifications that have really predictable file names like <transaction_id>_front, <transaction_id>_back, and <transaction_id>_face. 

If you are running on Linux, use "nohup" and "tee" to make sure that the job finishes and the output is saved.

# Configuration

Below are the parameters that can be customized. 
When parameter value is specified both in config file and the command line, the command line value takes precedence.

Name|config.properties|command-line arg|default
---|---|---|---
pathToImageFolder |yes	|yes	|nil
serverUrl	|yes	|yes	|https://netverify.com/api/netverify/v2
token |no |yes  |null
secret  |no |yes  |null
enabledFields	|yes	|no	|“idNumber,idFirstName,idLastName,idDob,idExpiry,idUsState,idPersonalNumber,idAddress,idFaceMatch"
merchantReportingCriteria	|yes	|no	|“Jumio NV Test Tool”
faceImageRequired |yes |no  |true
backImageRequired |yes |no  |true
frontSuffix |yes |no |front
backSuffix |yes |no  |back
faceSuffix |yes |no  |face

# Installation

1. Install Maven on Mac
```
brew install maven
```

# Running the tool for the batches

This program only requires a relatively recent version of Java to be installed.  The program is easy to run in IntelliJ (Android Studio) or Elipse but an IDE is not required.

Example:

```
java -cp PerformNetverifyTest.jar PerformNetverifyTest secret=***** token=****** pathToImageFolder=d:/imgFolder 

```

java -jar PerformNetverifyTest.jar PerformNetverifyGuid secret=***** token-****** pathToImageFolder=/Users/johnmc/Desktop/nv-test-tool-master/Imagesrc

''' Linux example
nohup java -cp PerformNetverifyTest.jar PerformNetverifyGuid secret=***** token=****| tee Output.txt


# Running Large Number of Scans

If you are going to post a large number of transactions, you need to be more organized.  You potentially could submit the same images multiple times and double bill the customer. 

Initial Setup Steps:

1) Install Java from https://java.com/en/download/
2) Get the PerformNetVerifyBatch repository
3) Navigate to the PerformNetverifyBatch directory.
4) Create a sub-directory called "images"
5) Create a sub-directory called "completed"
6) Create a sub-directory called "error"
7) Add the secret and token to the *.bat file for Windows or *.sh file for Linux
8) Edit run.bat to execute the right class and add the token and secret.  (I already did this.)

Preparing Images For Processing (Done for every patch):

1) Copy the images that we get from the client into PerformNetverifyBatch/images
2) For each ID image, Add "-id" to the end of "upload" substring of the filename.

              For example,
                         401727-10345741-upload-22cebe50334108573596084acddfe3a13447fd29.jpg  becomes
                         401727-10345741-upload-id-22cebe50334108573596084acddfe3a13447fd29.jpg

3) If there are multiple face images, pick the best image.
4) Move unused face images to the trash folder.   (If all the face images are about the same quality, you can skip this step.  The program will just take the first one.)

Processing the images:

1) Open a command line window by typing "cmd" in the run edit box.
2) Navigate to the PerformVerifyTest directory
3) execute "run.bat"
4) Copy the output with ctrl c and paste in a text file.  Save the txt file.
5) Note any files that were in error and move them to the error folder from the completed folder.
6) Repeat until all the transactions are done.
