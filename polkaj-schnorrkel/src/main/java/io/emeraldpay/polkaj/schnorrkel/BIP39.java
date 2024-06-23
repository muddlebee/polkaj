package io.emeraldpay.polkaj.schnorrkel;

public class BIP39 {
//    static {
//        System.loadLibrary("bip39_rust");
//    }

    public static native String generate(int words);
    public static native byte[] toEntropy(String phrase);
    public static native byte[] toMiniSecret(String phrase, String password);
    public static native byte[] toSeed(String phrase, String password);
    public static native boolean validate(String phrase);


    public static void main(String[] args) {
        System.out.println("BIP39.generate(12) = " + BIP39.generate(12));
    }
}