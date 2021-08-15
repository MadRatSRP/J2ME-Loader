package ru.playsoftware.j2meloader.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    
    private static final int BUFFER_SIZE = 16384;
    
    public static byte[] toByteArray(InputStream stream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[BUFFER_SIZE];
        int len;
        while ((len = stream.read(buf)) != -1) {
            outputStream.write(buf, 0, len);
        }
        return outputStream.toByteArray();
    }
}
