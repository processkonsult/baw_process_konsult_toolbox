package com.processkonsult.pktbx;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

import com.ibm.json.java.JSONObject;

public class BawJavaIntegrationsTest {
	static Properties prop = new Properties();

	public static void main(String args[]) throws Throwable {
    	
    	prop.load(new FileInputStream("config/passwords.properties"));
    	String response = BawJavaIntegrations.invokeREST("https://localhost:9443/rest/bpm/wle/v1/process/25?parts=data", prop.getProperty("baw.username"), prop.getProperty("baw.password"), null, "GET", null, true, false, null);
    	System.out.println(response);

    	JSONObject jsonObject = new JSONObject();
    	jsonObject = JSONObject.parse(response);
    	System.out.println(jsonObject.get("httpResponseCode"));
    	System.out.println(jsonObject.get("httpResponseMessage"));
    	System.out.println(jsonObject.get("httpResponseContent"));
    	System.out.println(jsonObject.get("httpErrorContent"));

        //String jsonBody = "{\"name\":\"name1\", \"value\": \"value1\"}";
        //String jsonBody = "";
        //String response = invokeREST("https://localhost:9443/rest/bpm/wle/v1/process/657/variable/nvp", "cellAdmin", "cellAdmin", "PUT", jsonBody, true);

        /*
        String[] fileList = listDirectory("F:\\Userdata\\git\\Integrations", true);
        for(int i=0; i<fileList.length; i++)
            System.out.println(fileList[i]);
        */

        /*
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "name1");
        jsonObject.put("value", "value1");
        jsonArray.add(jsonObject);
        jsonObject = new JSONObject();
        jsonObject.put("name", "name2");
        jsonObject.put("value", "value2");
        jsonArray.add(jsonObject);
        System.out.println("jsonArray.toString(): " + jsonArray.toString());
        */

/*
        System.out.println(performLDAPLookup("com.sun.jndi.ldap.LdapCtxFactory",
                "ldap://ldap.processkonsult.com",
                "cn=binduser,cn=users,dc=ldap,dc=processkonsult,dc=com",
                "b1ndus3r",
                "cn=users,dc=ldap,dc=processkonsult,dc=com",
                "(&(objectclass=person)(cn=*domainUser*))",
                "displayName,mail,cn,description"));
 */
    }

}
