package swoop.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IO {

    private static final int BUFFER_SIZE = 1024;

    public static StringBuilder toString(InputStream input, String charset) throws IOException {
        StringBuilder builder = new StringBuilder(Math.max(16, input.available()));

        InputStreamReader reader = new InputStreamReader(input, charset);
        char[] buffer = new char[BUFFER_SIZE];
        int read;
        while ((read = reader.read(buffer)) > 0) {
            builder.append(buffer, 0, read);
        }
        return builder;
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (IOException e) {
            // ignore
        }
    }
}
