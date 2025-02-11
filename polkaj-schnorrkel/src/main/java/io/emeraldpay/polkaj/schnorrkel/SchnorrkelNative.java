package io.emeraldpay.polkaj.schnorrkel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Wrapper around Rust implementation of the algorithms.
 * <br>
 * See also:
 * <ul>
 *     <li><a href="https://github.com/w3f/schnorrkel">Rust implementatiion</a></li>
 *     <li><a href="https://tools.ietf.org/html/rfc8032">RFC 8032 - Edwards-Curve Digital Signature Algorithm (EdDSA)</a></li>
 * </ul>
 */
public class SchnorrkelNative extends Schnorrkel {

    @Override
    public byte[] sign(byte[] message, KeyPair keypair) throws SchnorrkelException {
        return SchnorrkelNative.sign(keypair.getPublicKey(), keypair.getSecretKey(), message);
    }

    @Override
    public boolean verify(byte[] signature, byte[] message, PublicKey publicKey) throws SchnorrkelException {
        return SchnorrkelNative.verify(signature, message, publicKey.getPublicKey());
    }

    @Override
    public KeyPair generateKeyPair() throws SchnorrkelException {
        try {
            return generateKeyPair(SecureRandom.getInstanceStrong());
        } catch (NoSuchAlgorithmException e) {
            throw new SchnorrkelException("Secure Random is not available");
        }
    }

    //TODO:address can be refactored to use the generateSeed method
    @Override
    public KeyPair generateKeyPair(SecureRandom random) throws SchnorrkelException {
        byte[] seed = new byte[32];
        random.nextBytes(seed);
        byte[] key = keypairFromSeed(seed);
        return decodeKeyPair(key);
    }

    @Override
    public KeyPair generateKeyPairFromSeed(byte[] seed) throws SchnorrkelException {
        byte[] key = keypairFromSeed(seed);
        return decodeKeyPair(key);
    }

    @Override
    public KeyPair deriveKeyPair(KeyPair base, ChainCode chainCode) throws SchnorrkelException {
        byte[] key = deriveHard(encodeKeyPair(base), chainCode.getValue());
        return decodeKeyPair(key);
    }

    @Override
    public KeyPair deriveKeyPairSoft(KeyPair base, ChainCode chainCode) throws SchnorrkelException {
        byte[] key = deriveSoft(encodeKeyPair(base), chainCode.getValue());
        return decodeKeyPair(key);
    }

    @Override
    public PublicKey derivePublicKeySoft(PublicKey base, ChainCode chainCode) throws SchnorrkelException {
        byte[] key = derivePublicKeySoft(base.getPublicKey(), chainCode.getValue());
        return new Schnorrkel.PublicKey(key);
    }

    //generate keypair from seed using BIP39
    public KeyPair generateKeyPair(String password) throws SchnorrkelException {
        byte[] seed = generateSeedFromPassword(password);
        return Schnorrkel.getInstance().generateKeyPairFromSeed(seed);
    }

    @Override
    public KeyPair generateKeyPair(String seedPhrase, String password) throws SchnorrkelException {
        byte[] seed = BIP39.toSeed(seedPhrase, password);
        return Schnorrkel.getInstance().generateKeyPairFromSeed(seed);
    }

    //implementat BIP39 methods
    public static String generate(int words) {
        return BIP39.generate(words);
    }

    //generate seed with input as password
    public static byte[] generateSeedFromPassword(String password) {
        String phrase = BIP39.generate(12);
        return BIP39.toSeed(phrase, password);
    }

    //BIP39 validate
    public static boolean validate(String phrase) {
        return BIP39.validate(phrase);
    }

    //BIP39 toEntropy
    public static byte[] toEntropy(String phrase) {
        return BIP39.toEntropy(phrase);
    }

    //BIP39 toMiniSecret
    public static byte[] toMiniSecret(String phrase, String password) {
        return BIP39.toMiniSecret(phrase, password);
    }

    //BIP39 toSeed
    public static byte[] toSeed(String phrase, String password) {
        return BIP39.toSeed(phrase, password);
    }

    private static Schnorrkel.KeyPair decodeKeyPair(byte[] key) throws SchnorrkelException {
        if (key.length != KEYPAIR_LENGTH) {
            throw new SchnorrkelException("Invalid key generated");
        }
        byte[] secretKey = new byte[SECRET_KEY_LENGTH];
        System.arraycopy(key, 0, secretKey, 0, SECRET_KEY_LENGTH);
        byte[] publicKey = new byte[PUBLIC_KEY_LENGTH];
        System.arraycopy(key, SECRET_KEY_LENGTH, publicKey, 0, PUBLIC_KEY_LENGTH);

        return new Schnorrkel.KeyPair(publicKey, secretKey);
    }

    private static byte[] encodeKeyPair(Schnorrkel.KeyPair keyPair) {
        byte[] result = new byte[KEYPAIR_LENGTH];
        System.arraycopy(keyPair.getSecretKey(), 0, result, 0, keyPair.getSecretKey().length);
        System.arraycopy(keyPair.getPublicKey(), 0, result, SECRET_KEY_LENGTH, keyPair.getPublicKey().length);
        return result;
    }

    // ====================== Mapping to the Native Library ======================

    private static native byte[] sign(byte[] publicKey, byte[] secretKey, byte[] message);
    private static native byte[] keypairFromSeed(byte[] seed);
    private static native boolean verify(byte[] signature, byte[] message, byte[] publicKey);
    private static native byte[] deriveHard(byte[] secret, byte[] cc);
    private static native byte[] deriveSoft(byte[] secret, byte[] cc);
    private static native byte[] derivePublicKeySoft(byte[] publicKey, byte[] cc);

    // ====================== LOAD NATIVE LIBRARY ======================

    private static final String LIBNAME = "polkaj_schnorrkel";

    static {

        try {
            // JVM needs native libraries to be loaded from filesystem, so first we need to extract
            // files for current OS into a temp dir then load the file.
            if(!extractAndLoadJNI()) {
                // load the native library, this is for running tests
                System.loadLibrary(LIBNAME);
            }
        } catch (IOException e) {
            System.err.println("Failed to extract JNI library from Jar file. " + e.getClass() + ":" + e.getMessage());
        } catch (UnsatisfiedLinkError e){
            System.err.println("Failed to load native library. Polkaj Schnorrkel methods are unavailable. Error: " + e.getMessage());
        }
    }

    private static boolean extractAndLoadJNI() throws IOException {
        // define which of files bundled with Jar to extract
        //TODO:OS detect
//        String os = System.getProperty("os.name", "unknown").toLowerCase();
//        if (os.contains("win")) {
//            os = "windows";
//        } else if (os.contains("mac")) {
//            os = "macos";
//        } else if (os.contains("nux")) {
//            os = "linux";
//        } else {
//            System.err.println("Unknown OS: " + os + ". Unable to setup native library for Polkaj Schnorrkel");
//            return false;
//        }
//        String filename = System.mapLibraryName(LIBNAME);
//        String classpathFile = "/native/" + os + "/" + filename;
        String filename = System.mapLibraryName(LIBNAME);
        String classpathFile = "/native/" + filename;

        // extract native lib to the filesystem
        InputStream lib = Schnorrkel.class.getResourceAsStream(classpathFile);
        System.out.println(classpathFile);
        if (lib == null) {
            System.err.println("Library " + classpathFile + " is not found in the classpath");
            return false;
        }
        Path dir = Files.createTempDirectory(LIBNAME);
        Path target = dir.resolve(filename);

        Files.copy(lib, target);
        System.load(target.toFile().getAbsolutePath());
        System.out.println("library " + classpathFile + " is loaded");

        // setup JVM to delete files on exit, when possible
        target.toFile().deleteOnExit();
        dir.toFile().deleteOnExit();
        return true;
    }

    //psvm
    public static void main(String[] args) throws SchnorrkelException {
//        byte[] seed = generateSeed();
//        Schnorrkel.KeyPair keyPair = Schnorrkel.getInstance().generateKeyPairFromSeed(seed);
//        System.out.println("Public Key: " + keyPair.getPublicKey());
//        System.out.println("Secret Key: " + keyPair.getSecretKey());
        System.out.println("BIP39.generate(12) = " + BIP39.generate(18));
        String phrase = BIP39.generate(12);
        byte[] seed = BIP39.toSeed(phrase, "");
        Schnorrkel.KeyPair keyPair = Schnorrkel.getInstance().generateKeyPairFromSeed(seed);
        System.out.println("Public Key: " + Arrays.toString(keyPair.getPublicKey()));
        System.out.println("Secret Key: " + Arrays.toString(keyPair.getSecretKey()));
    }

}
