package io.emeraldpay.polkaj.schnorrkel;

public class BIP39 {

    public static native String generate(int words);
    public static native byte[] toEntropy(String phrase);
    public static native byte[] toMiniSecret(String phrase, String password);
    public static native byte[] toSeed(String phrase, String password);
    public static native boolean validate(String phrase);

}