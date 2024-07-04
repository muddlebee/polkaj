package io.emeraldpay.polkaj.schnorrkel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class NativeLibraryLoader {

    public static boolean loadLibrary(String libName) {
        try {
            if (!extractAndLoadJNI(libName)) {
                // load the native library, this is for running tests
                System.loadLibrary(libName);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Failed to extract JNI library from Jar file. " + e.getClass() + ":" + e.getMessage());
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load native library. Native methods are unavailable. Error: " + e.getMessage());
        }
        return false;
    }

    private static boolean extractAndLoadJNI(String libName) throws IOException {
        String filename = System.mapLibraryName(libName);
        String classpathFile = "/native/" + filename;

        // extract native lib to the filesystem
        InputStream lib = NativeLibraryLoader.class.getResourceAsStream(classpathFile);
        System.out.println(classpathFile);
        if (lib == null) {
            System.err.println("Library " + classpathFile + " is not found in the classpath");
            return false;
        }
        Path dir = Files.createTempDirectory(libName);
        Path target = dir.resolve(filename);

        Files.copy(lib, target);
        System.load(target.toFile().getAbsolutePath());
        System.out.println("library " + classpathFile + " is loaded");

        // setup JVM to delete files on exit, when possible
        target.toFile().deleteOnExit();
        dir.toFile().deleteOnExit();
        return true;
    }
}