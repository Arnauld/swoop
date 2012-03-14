package swoop;

import java.io.File;

import swoop.util.Context;

public interface ResourceHandler {
    void writeResource(String path, File dir, Context context);
}
