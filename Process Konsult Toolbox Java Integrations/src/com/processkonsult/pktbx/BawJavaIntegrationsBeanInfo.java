package com.processkonsult.pktbx;

import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;
import java.beans.SimpleBeanInfo;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

public class BawJavaIntegrationsBeanInfo extends SimpleBeanInfo {
    /* BeanInfo classes are used to provide more user-friendly descriptions for Java integration parameters via the IBM BAW designer environment */

    private Class beanClass = BawJavaIntegrations.class; // The class this BeanInfo refers to
    @Override
    public MethodDescriptor[] getMethodDescriptors() {
        try {
			ArrayList<MethodDescriptor> methodDescriptors = new ArrayList<MethodDescriptor>();

			// Describe the Methods and add directly into a list
			methodDescriptors.add(getMethodDescription(
                    "performLDAPLookupJ2CAlias",
                    new String[] { "namingFactory","ldapUrl","j2cAlias","searchBase","searchQuery","attributeListCsv"},
                    new Class[] { String.class,String.class,String.class,String.class,String.class,String.class }));

			methodDescriptors.add(getMethodDescription(
                    "performLDAPLookup",
                    new String[] { "namingFactory","ldapUrl","bindUserName","bindUserPassword","searchBase","searchQuery","attributeListCsv"},
                    new Class[] { String.class,String.class,String.class,String.class,String.class,String.class,String.class }));

			methodDescriptors.add(getMethodDescription(
                    "setFilePermissions",
                    new String[] { "filePath", "posixFilePermissions"},
                    new Class[] { String.class, String.class }));
			
			methodDescriptors.add(getMethodDescription(
                    "listDirectory",
                    new String[] { "folderPath", "excludeDirectories"},
                    new Class[] { String.class, Boolean.class }));
			
			methodDescriptors.add(getMethodDescription(
                    "getFileDetailsFromDirectory",
                    new String[] { "directoryPath"},
                    new Class[] { String.class }));
			
			methodDescriptors.add(getMethodDescription(
                    "deleteFile",
                    new String[] { "filePath" },
                    new Class[] { String.class }));
			
			methodDescriptors.add(getMethodDescription(
                    "deleteDirectory",
                    new String[] { "directoryPath" },
                    new Class[] { String.class }));
			
			methodDescriptors.add(getMethodDescription(
                    "makeDirectories",
                    new String[] { "directoryPath" },
                    new Class[] { String.class }));
			
			methodDescriptors.add(getMethodDescription(
                    "moveFile",
                    new String[] { "sourceFilePath", "targetFilePath"},
                    new Class[] { String.class, String.class }));

			methodDescriptors.add(getMethodDescription(
                    "copyFile",
                    new String[] { "sourceFilePath", "targetFilePath"},
                    new Class[] { String.class, String.class }));

			methodDescriptors.add(getMethodDescription(
					"invokeREST_J2CAlias",
                    new String[] { "restUrl", "j2cAlias", "httpHeaderMap", "httpMethod", "requestBody", "sendFileAsBody", "filePath" },
                    new Class[] { String.class, String.class, Map.class, String.class, String.class, Boolean.class, String.class }));

			methodDescriptors.add(getMethodDescription(
					"invokeREST",
                    new String[] { "restUrl", "username", "password", "httpHeaderMap", "httpMethod", "requestBody", "sendFileAsBody", "filePath" },
                    new Class[] { String.class, String.class, String.class, Map.class, String.class, String.class, Boolean.class, String.class }));

			return methodDescriptors.toArray(new MethodDescriptor[0]);
			
        } catch (Exception e) {
            return super.getMethodDescriptors();
        }
    }

    // Build Method descriptor
    private MethodDescriptor getMethodDescription(String methodName, String parameters[], Class classes[]) throws NoSuchMethodException {
        MethodDescriptor methodDescriptor = null;
        Method method = beanClass.getMethod(methodName, classes);

        if (method != null) {
            ParameterDescriptor paramDescriptors[] = new ParameterDescriptor[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                ParameterDescriptor param = new ParameterDescriptor();
                param.setShortDescription(parameters[i]);
                param.setDisplayName(parameters[i]);
                paramDescriptors[i] = param;
            }
            methodDescriptor = new MethodDescriptor(method, paramDescriptors);
        }

        return methodDescriptor;
    }

}

