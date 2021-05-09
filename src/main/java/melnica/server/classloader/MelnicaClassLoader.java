package melnica.server.classloader;

import java.io.File;
import java.io.FileInputStream;

public class MelnicaClassLoader extends ClassLoader {

    private String basePath = System.getProperty("user.dir") + File.separator + "webapps";
    
    public MelnicaClassLoader(String applicationName) {
    	basePath += File.separator + applicationName + File.separator + "WEB-INF" + File.separator + "classes" + File.separator;
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        String fullName = name.replace('.', '/');
        fullName += ".class";

        String path = basePath + fullName ;
        try {
            FileInputStream fis = new FileInputStream(path);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            Class<?> res = defineClass(name, data, 0, data.length);
            fis.close();

            return res;
        } catch(Exception e) {
            return super.findClass(name);
        }
    }
}