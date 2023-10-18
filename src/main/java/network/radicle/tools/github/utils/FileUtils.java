package network.radicle.tools.github.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;

public class FileUtils {
    public static String getFileType(byte[] fileBytes) {
        try {
            var contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(fileBytes));
            if (contentType != null) {
                return contentType;
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    public static String getBase64Prefix(byte[] fileBytes) {
        var fileType = getFileType(fileBytes);
        if (fileType != null) {
            return "data:" + fileType + ";base64,";
        } else {
            return "data:application/octet-stream;base64,";
        }
    }
}
