package melnica.server.classloader;

import java.io.File;
import java.io.FileInputStream;

public class MelnicaClassLoader extends ClassLoader {

    private String webApplicationClassFolderPath;
    
    public MelnicaClassLoader(String applicationName) {
    	webApplicationClassFolderPath = generateBasePath(applicationName); 
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
    	
        String fullName = name.replace('.', '/');
        fullName += ".class";

        String path = webApplicationClassFolderPath + fullName ;
        try {
            FileInputStream classFileInputStream = new FileInputStream(path);
            byte[] binaryDataOfClassFile = new byte[classFileInputStream.available()];
            
            classFileInputStream.read(binaryDataOfClassFile);
            Class<?> res = defineClass(name, binaryDataOfClassFile, 0, binaryDataOfClassFile.length);
            classFileInputStream.close();
            
            return res;
        } catch(Exception e) {
            return super.findClass(name);
        }
    }
    
    private String generateBasePath(String applicationName) {
    	    	
    	StringBuilder builder = new StringBuilder();
    	builder.append(MelnicaClassLoaderConstant.WEB_APPLICATION_DIRECTORY);
    	builder.append(File.separator);
    	builder.append(MelnicaClassLoaderConstant.WEB_APPLICATION_FOLDER_NAME);
    	builder.append(File.separator);
    	builder.append(applicationName);
    	builder.append(File.separator);
    	builder.append(MelnicaClassLoaderConstant.WEB_APPLICATION_INFO_FOLDER_NAME);
    	builder.append(File.separator);
    	builder.append(MelnicaClassLoaderConstant.WEB_APPLICATION_CLASS_FOLDER_NAME);
    	builder.append(File.separator);
    	return builder.toString();
    }
}