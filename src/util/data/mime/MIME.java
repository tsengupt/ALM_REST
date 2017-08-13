package util.data.mime;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.io.FilenameUtils;


public class MIME {

    private final Properties map = new Properties();

    private static MIME mime;

    private MIME() throws IOException {
        map.load(MIME.class.
                getResourceAsStream(
                        "/util/data/mime/mime.types.properties"));

    }

    public static String getType(File f) {
        try {
            return getType(f.getName());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    public static String getType(String fileName) {
        try {
            return getTypeFor(FilenameUtils.getExtension(fileName));
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    private static String getTypeFor(String ext) {
        try {
            if (mime == null) {
                mime = new MIME();
            }
            return mime.map.getProperty(ext);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }
    

}
