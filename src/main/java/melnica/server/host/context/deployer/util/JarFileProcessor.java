package melnica.server.host.context.deployer.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipException;

public class JarFileProcessor {
	
	private static final Pattern PATTERN_EXCLAMATION_MARK = Pattern.compile("!/");
	private static final Pattern PATTERN_CARET = Pattern.compile("\\^/");
	private static final Pattern PATTERN_ASTERISK = Pattern.compile("\\*/");
	    
	public static URL buildJarUrl(File file) throws MalformedURLException {
		
		String fileUrlString = file.toURI().toString();
        String safeString = makeSafeForJarUrl(fileUrlString);
        StringBuilder sb = new StringBuilder();
        sb.append(safeString);
        sb.append("!/");
        return new URL("jar", null, -1, sb.toString());
    }
	
	private static String makeSafeForJarUrl(String input) {
		
		String tmp = PATTERN_EXCLAMATION_MARK.matcher(input).replaceAll("%21/");
        tmp = PATTERN_CARET.matcher(tmp).replaceAll("%5e/");
        tmp = PATTERN_ASTERISK.matcher(tmp).replaceAll("%2a/");
        return tmp;
    }
	
	public static String expandWar(URL warFile, File webAppFilePassed, String pathname) throws IOException {

        JarURLConnection juc = (JarURLConnection) warFile.openConnection();
        juc.setUseCaches(false);
        
        boolean success = false;
        File webAppFile = new File(webAppFilePassed, pathname);
        if (webAppFile.exists()) {
            return webAppFile.getAbsolutePath();
        }

        if(!webAppFile.mkdir() && !webAppFile.isDirectory()) {
            throw new IOException("Web app folder creation error");
        }

	    Path canonicalDocBasePath = webAppFile.getCanonicalFile().toPath();
        try {
        	JarFile jarFile = juc.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String name = jarEntry.getName();
                File expandedFile = new File(webAppFile, name);
                if (!expandedFile.getCanonicalFile().toPath().startsWith(canonicalDocBasePath)) {
                    throw new IllegalArgumentException("Jar entry is not in web app root folder: " + expandedFile.getAbsolutePath());
                }
                int last = name.lastIndexOf('/');
                if (last >= 0) {
                    File parent = new File(webAppFile, name.substring(0, last));
                    if (!parent.mkdirs() && !parent.isDirectory()) {
                        throw new IOException("");
                    }
                }
                if (name.endsWith("/")) {
                    continue;
                }

            	InputStream input = jarFile.getInputStream(jarEntry);
                if (null == input) {
                    throw new ZipException("Zip exception deploy to web app: " + jarEntry.getName());
                }
                expand(input, expandedFile);
            }
            success = true;
        } 
        catch (IOException e) {
            throw e;
        } 
        finally {
            if (!success) {
                deleteDir(webAppFile);
            }
        }

        return webAppFile.getAbsolutePath();
    }
	
	public static String expandDirectory(File webAppFilePassed, String pathname) {
		
        File docBaseAbsoluteFile = new File(webAppFilePassed, pathname);
		if (!docBaseAbsoluteFile.exists()) {
			return "";
		}
		return docBaseAbsoluteFile.getAbsolutePath();
	}
	
	private static void expand(InputStream input, File file) throws IOException {
		
    	BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
        byte buffer[] = new byte[2048];
        while (true) {
            int n = input.read(buffer);
            if (n <= 0)
                break;
            output.write(buffer, 0, n);
        }
        output.close();
    }
	
	public static boolean deleteDir(File dir) {

        String files[] = dir.list();
        if (files == null) {
            files = new String[0];
        }
        for (String s : files) {
            File file = new File(dir, s);
            if (file.isDirectory()) {
                deleteDir(file);
            } else {
                file.delete();
            }
        }

        boolean result;
        if (dir.exists()) {
            result = dir.delete();
        } else {
            result = true;
        }
        return result;
    }
}
