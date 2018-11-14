package com.jumio.lab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class PerformNetverify {
	
	private static final String PROPERTIES_FILE = "config.properties";
	
	private static final String PATH_TO_IMAGE_FOLDER_ = "pathToImageFolder=";
	private static final String SERVER_URL_ = "serverUrl=";
	private static final String API_SECRET_ = "secret=";
	private static final String API_TOKEN_ = "token=";

	private static final String USER_AGENT_TXT = "Jumio NV Test Tool/v1.0";
	private static final String PATH_TO_IMAGE_FOLDER = "pathToImageFolder";
	private static final String SERVER_URL = "serverUrl";
	private static final String ENABLED_FIELDS = "enabledFields";
	private static final String MERCHANT_REPORTING_CRITERIA = "merchantReportingCriteria";
	private static final String FRONT_SUFFIX = "frontSuffix";
	private static final String BACK_SUFFIX = "backSuffix";
	private static final String FACE_SUFFIX = "faceSuffix";
	private static final String MERCHANT_ID_SCAN_REFERENCE = "merchantIdScanReference";
	private static final String FRONTSIDE_IMAGE = "frontsideImage";
	private static final String BACK_IMAGE = "backsideImage";
	private static final String FACE_IMAGE = "faceImage";
	private static final String BACK_IMAGE_REQUIRED = "backImageRequired";
	private static final String FACE_IMAGE_REQUIRED = "faceImageRequired";
	private static final String JUMIO_ID_SCAN_REFERENCE = "jumioIdScanReference";
	private static final String NUMBER_TO_SUBMIT = "numberToSubmit";
	
	private static final String SUCCESSFULLY = "successfully";
	private static final String MSG_LIMIT = "WARN: There are more than 100 images in the folder. Only the first 100 will be processed.";
	private static final String BACK_MISSING = "Back image missing for: ";
	private static final String FACE_MISSING = "Face image missing for: ";
	private static final String FILE_NOT_PROCESSED = "! File Not Processed.";
	
	private static final String MSG_API_SECRET = "API secret is empty.";
	private static final String IS_EMPTY = " is empty.";
	private static final String NOT_EXISTING = "not existing.";
	private static final String PERFORM_NETVERIFY_TXT = "/performNetverify";
	private static final String COMPLETED_FOLDER = "completed";
	private static String frontSuffix;
	private static String faceSuffix;
	private static String backSuffix;
	
	
	public static void main(String[] args) {
		try {
					
			//properties file
			FileInputStream inputStream = new FileInputStream(PROPERTIES_FILE);			
			Properties prop = new Properties();
			prop.load(inputStream);
			
			boolean requiresFace = Boolean.parseBoolean(prop.getProperty(FACE_IMAGE_REQUIRED));
			boolean requiresBack = Boolean.parseBoolean(prop.getProperty(BACK_IMAGE_REQUIRED));
			String pathToImageFolder = prop.getProperty(PATH_TO_IMAGE_FOLDER);
			String serverUrl = prop.getProperty(SERVER_URL);
			String token = prop.getProperty(API_TOKEN_);
			String secret = prop.getProperty(API_SECRET_);
			String enabledFields = prop.getProperty(ENABLED_FIELDS);
			String merchantReportingCriteria = prop.getProperty(MERCHANT_REPORTING_CRITERIA);
			frontSuffix = prop.getProperty(FRONT_SUFFIX);
			faceSuffix = prop.getProperty(FACE_SUFFIX);
			backSuffix = prop.getProperty(BACK_SUFFIX);
			int numberToSubmit = Integer.parseInt(prop.getProperty(NUMBER_TO_SUBMIT));
			int counter = 0;


			//arguments from command line
			for(int i = 0; i < args.length; i++) {
				if(args[i].contains(PATH_TO_IMAGE_FOLDER_)) {
					pathToImageFolder = args[i].replace(PATH_TO_IMAGE_FOLDER_, "");
				}
				else if(args[i].contains(SERVER_URL_)) {
					serverUrl = args[i].replace(SERVER_URL_, "");
				}
				else if(args[i].contains(API_SECRET_)) {
					secret = args[i].replace(API_SECRET_, "");
				}
				else if(args[i].contains(API_TOKEN_)) {
					token = args[i].replace(API_TOKEN_, "");
				}
			}
			File imageFolder = new File(pathToImageFolder);

			// Create the completed folder if missing so we can move submitted files
			StringBuffer completedPath = new StringBuffer(pathToImageFolder).append(File.separator).append(COMPLETED_FOLDER);
			File completedFolder = new File(completedPath.toString());
			if (!completedFolder.exists()) {
				completedFolder.mkdir();
			}

			if(secret == null || secret.equals("")) {
				System.out.println(MSG_API_SECRET);
				return;
			}
			ArrayList<String> imagesArray = getAllIdImagesFromDirectory(imageFolder);
			if (imagesArray == null && imagesArray.size() == 0) {
				System.out.println("\n\nNo Images to submit");
				return;
			}
			try {
				URL url = new URL(serverUrl + PERFORM_NETVERIFY_TXT);
				String auth = token + ":" + secret;
				auth = Base64.getEncoder().encodeToString(auth.getBytes());

				String idFilename = null, backFilename = null, imgFilename = null;
				HttpURLConnection conn;
				OutputStreamWriter wr = null;
				Path idPath = null, imgPath = null, backPath = null;

				for (String str : imagesArray) {
					if (counter >= numberToSubmit) {
						System.out.println("\n\nFinal Total Submitted: " + counter);
						break;
					}

					idPath = Paths.get(str);

					conn = (HttpURLConnection) url.openConnection();
					conn.setDoOutput(true);
					conn.setDoInput(true);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Accept", "application/json");
					conn.setRequestProperty("Content-Type", "application/json");
					conn.setRequestProperty("User-Agent", USER_AGENT_TXT);
					conn.setRequestProperty("Authorization", "Basic " + auth);

					try {
						JsonObject jsonObject = new JsonObject();

						//jsonObject.addProperty(ENABLED_FIELDS, enabledFields);
						jsonObject.addProperty(MERCHANT_REPORTING_CRITERIA, merchantReportingCriteria);
						jsonObject.addProperty(MERCHANT_ID_SCAN_REFERENCE, idPath.getFileName().toString());

						// Add front image
						byte[] data = Files.readAllBytes(idPath);
						String idImg = Base64.getEncoder().encodeToString(data);
						jsonObject.addProperty(FRONTSIDE_IMAGE, idImg);

						// Add back image
						idFilename = idPath.getFileName().toString();
						if (requiresBack) {
							backFilename = idFilename.substring(0, idFilename.lastIndexOf(".") - frontSuffix.length()) + backSuffix + idFilename.substring(idFilename.lastIndexOf("."));
							backPath = Paths.get(idPath.getParent().toString() + idPath.getFileSystem().getSeparator() + backFilename);
							if (!backPath.toFile().exists() && requiresBack) {
								System.out.println(BACK_MISSING + idPath.getFileName().toString() + FILE_NOT_PROCESSED);
								continue;
							}
							String backImg = Base64.getEncoder().encodeToString(Files.readAllBytes(backPath));
							jsonObject.addProperty(BACK_IMAGE, backImg);
						}

						// Add face image
						if (requiresFace) {
							imgFilename = idFilename.substring(0, idFilename.lastIndexOf(".") - frontSuffix.length()) + faceSuffix + idFilename.substring(idFilename.lastIndexOf("."));
							imgPath = Paths.get(idPath.getParent().toString() + idPath.getFileSystem().getSeparator() + imgFilename);
							if (!imgPath.toFile().exists()) {
								System.out.println(FACE_MISSING + idPath.getFileName().toString() + FILE_NOT_PROCESSED);
								continue;
							}
							String faceImg = Base64.getEncoder().encodeToString(Files.readAllBytes(imgPath));
							jsonObject.addProperty(FACE_IMAGE, faceImg);
						}

						// Finished building jsonObject; Send to server
						wr = new OutputStreamWriter(conn.getOutputStream());
						wr.write(jsonObject.toString());
						wr.flush();
						//System.out.println(jsonObject.toString());

						String streamToString = convertStreamToString(conn.getInputStream());
						try {
							JsonParser parser = new JsonParser();
							JsonObject jsonObj = (JsonObject) parser.parse(streamToString);

							// if successfully submitted, move the files to completed.
							if (jsonObj.get(JUMIO_ID_SCAN_REFERENCE) != null) {
								System.out.println(idPath.getFileName().toString() + ": " + SUCCESSFULLY);
								if (idPath != null)
									idPath.toFile().renameTo(new File(completedPath + idPath.getFileSystem().getSeparator() + idFilename));
								if (backPath != null)
									backPath.toFile().renameTo(new File(completedPath + idPath.getFileSystem().getSeparator() + backFilename));
								if (imgPath != null)
									imgPath.toFile().renameTo(new File(completedPath + idPath.getFileSystem().getSeparator() + imgFilename));
							} else {
								System.out.println(idPath.getFileName().toString() + ": " + jsonObj.toString());
							}
						} catch (JsonSyntaxException jsexc) {
							System.out.println(idPath.getFileName().toString() + ": " + streamToString);
						}

					} catch (IOException ioexc) {
						System.out.println(idPath.getFileName().toString() + ": " + ioexc.getMessage());
					}
					idPath = null;
					backPath = null;
					imgPath = null;

					counter++;
					conn.disconnect();
					System.out.println("\n\nTotal Submitted: " + counter);
				}
			}
			catch(MalformedURLException muexc){
				System.out.println(muexc);
			}
			catch(ProtocolException pexc){
				System.out.println(pexc.getMessage());
			}
			catch(IOException ioexc){
				System.out.println(ioexc.getMessage());
			}

		}
		catch(Exception ioexc){
			System.out.println(ioexc.getMessage());
		}

	}

	/*
}

catch(FileNotFoundException fnfexc) {
	System.out.println(fnfexc.getMessage());
}
catch(IOException ioexc) {
	System.out.println(ioexc.getMessage());
}
	}
	*/
	/**
	 * getAllIdImagesFromDirectory creates a list of frontImage id's.  
	 *
	 * TODO: Need to verify that back and face exist if needed.
	 * && (file.getName().toLowerCase().indexOf(faceSuffix, 0) < 0) && file.getName().toLowerCase().indexOf(backSuffix, 0) < 0
	 * 
	 * @param directory - Directory of all images to be verified
	 * @return
	 */
	private static ArrayList<String> getAllIdImagesFromDirectory(File directory) {
        ArrayList<String> resultList = new ArrayList<String>(1);
		FilenameFilter idFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.toLowerCase().contains(frontSuffix) && name.endsWith("pdf") == false) {
					return true;
				} else {
					return false;
				}
			}
		};
		File[] f = directory.listFiles(idFilter);
		if (f != null) {

			for (File file : f) {
				try {
					resultList.add(file.getCanonicalPath());
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		} else {
			System.out.println(directory.getName() + ": " + NOT_EXISTING);
			return null;
		}

		if (resultList.size() > 0) {
			if (resultList.size() > 100) {
				System.out.println(MSG_LIMIT);
        	}
            return resultList;
        }
        else {
        	System.out.println(directory.getName() + IS_EMPTY);
            return null;
        }
    }

	private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        }
        catch (Exception e) {
        	System.out.println(e.getMessage());
        }
        finally {
            try {
                is.close();
            }
            catch (Exception e) {
            	System.out.println(e.getMessage());
            }
        }
        return sb.toString();
    }
}
