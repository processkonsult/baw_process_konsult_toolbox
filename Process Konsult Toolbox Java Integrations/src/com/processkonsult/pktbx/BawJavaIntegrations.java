package com.processkonsult.pktbx;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.wsspi.security.auth.callback.Constants;
import com.ibm.wsspi.security.auth.callback.WSMappingCallbackHandlerFactory;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.*;
import javax.net.ssl.*;
import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;

public class BawJavaIntegrations {

    public static String performLDAPLookupJ2CAlias(String namingFactory, String ldapUrl, String j2cAlias, String searchBase, String searchQuery, String attributeListCsv) throws Throwable {
        JSONObject jsonCredential = getUsernamePasswordFromJ2CAlias(j2cAlias);
        return performLDAPLookup(namingFactory, ldapUrl, jsonCredential.get("username").toString(), jsonCredential.get("password").toString(), searchBase, searchQuery, attributeListCsv);
    }

    public static String performLDAPLookup(String namingFactory, String ldapUrl, String bindUserName, String bindUserPassword, String searchBase, String searchQuery, String attributeListCsv) throws Throwable {
        Properties env = new Properties();
        env.put("java.naming.factory.initial", namingFactory);
        env.put("java.naming.provider.url", ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, bindUserName);
        env.put(Context.SECURITY_CREDENTIALS, bindUserPassword);
        DirContext ctx = new InitialDirContext(env);

        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<?> namingEnum = ctx.search(searchBase, searchQuery, controls);
        JSONArray ldapSearchJsonResults = new JSONArray();
        while (namingEnum.hasMore()) {
            JSONObject jsonObject = new JSONObject();
            SearchResult result = (SearchResult) namingEnum.next();
            Attributes attrs = result.getAttributes();
            String[] attributeList = attributeListCsv.split(",");
            for(String attribute : attributeList) {
                String attributeValue = null;
                if(attrs.get(attribute) != null)
                    attributeValue = (String) attrs.get(attribute).get();
                jsonObject.put(attribute, attributeValue);
            }
            ldapSearchJsonResults.add(jsonObject);
        }
        namingEnum.close();
        return ldapSearchJsonResults.toString();
    }

    public static void setFilePermissions(String filePath, String posixFilePermissions) throws Throwable {
        Set<PosixFilePermission> permissions = PosixFilePermissions.fromString(posixFilePermissions);
        File file = new File(filePath);
        Files.setPosixFilePermissions(file.toPath(), permissions);
    }

    public static String[] listDirectory(String folderPath, Boolean excludeDirectories) {
        File folderFile = new File(folderPath);
        File[] files = folderFile.listFiles();
        Arrays.sort(files);
        ArrayList<String> fileList = new ArrayList<String>();
        for(int i=0; i<files.length; i++) {
            if(excludeDirectories && files[i].isDirectory())
                continue;
            else
                fileList.add(files[i].getAbsolutePath());
        }
        return fileList.toArray(new String[fileList.size()]);
    }

    public static String getFileDetailsFromDirectory(String directoryPath) {
        File folderFile = new File(directoryPath);
        File[] files = folderFile.listFiles();
        Arrays.sort(files);
        JSONArray jsonArray = new JSONArray();
        for(int i=0; i<files.length; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fileName", files[i].getName());
            jsonObject.put("lastModified", files[i].lastModified());
            jsonArray.add(jsonObject);
        }
        return jsonArray.toString();
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        file.delete();
    }

    public static void deleteDirectory(String directoryPath) {
        File directoryFile = new File(directoryPath);
        File[] files = directoryFile.listFiles();
        if(files != null) {
            for(File f : files) {
                if(f.isDirectory())
                    deleteDirectory(f.getAbsolutePath());
                else
                    f.delete();
            }
        }
        directoryFile.delete();
    }

    public static void makeDirectories(String directoryPath) {
        File directory = new File(directoryPath);
        directory.mkdirs();
    }

    public static void moveFile(String sourceFilePath, String targetFilePath) throws Throwable {
        // Check if target directory exists, if not create it
        File targetFile = new File(targetFilePath);
        if(!targetFile.getParentFile().exists())
            targetFile.getParentFile().mkdirs();

        Files.move(Paths.get(sourceFilePath), Paths.get(targetFilePath));
    }

    public static void copyFile(String sourceFilePath, String targetFilePath) throws Throwable {
        // Check if target directory exists, if not create it
        File targetFile = new File(targetFilePath);
        if(!targetFile.getParentFile().exists())
            targetFile.getParentFile().mkdirs();

        Files.copy(Paths.get(sourceFilePath), Paths.get(targetFilePath));
    }

	private static JSONObject getUsernamePasswordFromJ2CAlias(String j2cAlias) throws Throwable {
        // Access WebSphere J2C profile with bpmadmin credentials
        // http://stackoverflow.com/questions/4663534/how-to-access-authentication-alias-from-ejb-deployed-to-websphere-6-1
        JSONObject jsonCredential = new JSONObject();
        String username = null;
        String password = null;
        if(j2cAlias != null && !j2cAlias.equals("")) {
            HashMap map = new HashMap();
            map.put(Constants.MAPPING_ALIAS, j2cAlias);
            CallbackHandler callbackHandler = WSMappingCallbackHandlerFactory.getInstance().getCallbackHandler(map, null);
            LoginContext loginContext = new LoginContext("DefaultPrincipalMapping", callbackHandler);
            loginContext.login();
            Subject subject = loginContext.getSubject();
            Set credentials = subject.getPrivateCredentials();
            PasswordCredential passwordCredential = (PasswordCredential) credentials.iterator().next();
            username = passwordCredential.getUserName();
            password = new String(passwordCredential.getPassword());
            jsonCredential.put("username", username);
            jsonCredential.put("password", password);
        }
        return jsonCredential;
    }

    // This can be used when REST API calls need to be made using BAW system account
    public static String invokeREST_J2CAlias(String restUrl, String j2cAlias, Map httpHeaderMap, String httpMethod, String requestBody, Boolean sendFileAsBody, String filePath) throws Throwable {
        JSONObject jsonCredential = getUsernamePasswordFromJ2CAlias(j2cAlias);
        return invokeREST(restUrl, jsonCredential.get("username").toString(), jsonCredential.get("password").toString(), httpHeaderMap, httpMethod, requestBody, false, sendFileAsBody, filePath);
    }

    // This method supports Basic Authentication and works for calls made in BPM/BAW OnCloud environments, just be sure to setup a functional serviceId via 
    // Admin --> Service Credentials in Self-Service Portal. Additionally, username and password can be left null for non-authenticated calls such as OAuth token requests. 
    public static String invokeREST(String restUrl, String username, String password, Map httpHeaderMap, String httpMethod, String requestBody, Boolean sendFileAsBody, String filePath) throws Throwable {
        return invokeREST(restUrl, username, password, httpHeaderMap, httpMethod, requestBody, false, sendFileAsBody, filePath);
    }

    // This can be used during developer to make Java IDE-based testing when SSL certs are problematic in testing the REST call - set the last parameter to true
    static String invokeREST(String restUrl, String username, String password, Map httpHeaderMap, String httpMethod, String requestBody, Boolean useTrustAllCerts, Boolean sendFileAsBody, String filePath) throws Throwable {
        // Use this switch for unit testing to avoid having to get certs working
        if(useTrustAllCerts) {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        }

        // Establish HTTPS connection with REST endpoint and for POST/PUT commands, transmit JSON request body
        URL url = new URL(restUrl);
        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setRequestMethod(httpMethod);

        if(username != null && password != null) {
            String authorization = "Basic " + Base64.getEncoder().encodeToString((new String(username + ":" + password).getBytes()));
            httpUrlConnection.setRequestProperty("Authorization", authorization);
        }

        if(httpHeaderMap != null && httpHeaderMap.size() > 0) {
            Set set = httpHeaderMap.entrySet();
            Iterator i = set.iterator();
            while(i.hasNext()) {
                Map.Entry entry = (Map.Entry)i.next();
                httpUrlConnection.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
            }
        }

        if ((httpMethod.equals("PUT") || httpMethod.equals("POST"))) {
        	httpUrlConnection.setDoOutput(true);
        	if (sendFileAsBody != false) {
				OutputStream outputStreamToRequestBody = httpUrlConnection.getOutputStream();
				File file = new File(filePath);
				if (file.exists()) {
					FileInputStream inputStreamToFile = new FileInputStream(file);
					BufferedWriter httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(outputStreamToRequestBody));
					int bytesRead;
					byte[] dataBuffer = new byte[1024];
					
					while ((bytesRead = inputStreamToFile.read(dataBuffer)) != -1) {
						outputStreamToRequestBody.write(dataBuffer, 0, bytesRead);
					}
					outputStreamToRequestBody.flush();
					httpRequestBodyWriter.close();
					inputStreamToFile.close();
				}
			} else if (requestBody != null && requestBody.length() > 0) {
				OutputStreamWriter osw = new OutputStreamWriter(httpUrlConnection.getOutputStream());
				osw.write(requestBody);
				osw.flush();
			} else if(requestBody == null) {
				// If the requestBody is null, typically a 411 Content-Length required message is thrown, so write an empty string to satisfy this case 
				OutputStreamWriter osw = new OutputStreamWriter(httpUrlConnection.getOutputStream());
				osw.write("");
				osw.flush();
			}
		}

        String response = "";
    	String errorContent = "";
        httpUrlConnection.connect();

        //System.out.println("httpUrlConnection.getResponseCode(): "+ httpResponseCode);

    	try {
            String contentDisposition = httpUrlConnection.getHeaderField("Content-Disposition");
            // Check the content-disposition header for "attachment" and if found convert to base64 and return, otherwise just return body as string
            if(contentDisposition != null && contentDisposition.startsWith("attachment")) {
            	InputStream is = httpUrlConnection.getInputStream();
            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            	byte[] buffer = new byte[1024];
            	for(int length; (length = is.read(buffer)) != -1; ) {
            	     baos.write(buffer, 0, length);
            	 }
            	 response = baos.toString("UTF-8");            
            } else {
            	InputStreamReader isr = new InputStreamReader(httpUrlConnection.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                   	response += line + "\r\n";
                }
                br.close();
                isr.close();
            }
    	} catch(Exception e) {
            if(httpUrlConnection.getErrorStream() != null) {
            	InputStreamReader isr = new InputStreamReader(httpUrlConnection.getErrorStream());
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                	errorContent += line;
                }
                br.close();
                isr.close();
                System.out.println("httpUrlConnection.getErrorStream(): " + errorContent);
            }
            // Changed implementation to not throw error from Java, instead capture error in 
            // errorContent and output to BAW service then handle throwing error from within BAW. 
            //throw e;
    	} finally {
    		httpUrlConnection.disconnect();
    	}

    	JSONObject jsonResult = new JSONObject();
        jsonResult.put("httpResponseCode", httpUrlConnection.getResponseCode());
        jsonResult.put("httpResponseMessage", httpUrlConnection.getResponseMessage());
    	jsonResult.put("httpResponseContent", response);
    	jsonResult.put("httpErrorContent", errorContent);
        return jsonResult.serialize();
    }
}
