package io.emeraldpay.polkaj.ss58;

/**
 * The basic format of the address can be described as:
 *
 * base58encode ( concat ( <address-type>, <address>, <checksum> ) )
 * <a href="https://docs.substrate.io/reference/address-formats/#address-type"/>
 *
 */
public class SS58 {

    private final SS58Type type;
    private final byte[] value;
    private final byte[] checksum;

    public SS58(SS58Type type, byte[] value, byte[] checksum) {
        this.type = type;
        this.value = value;
        this.checksum = checksum;
    }

    public SS58Type getType() {
        return type;
    }

    public byte[] getValue() {
        return value;
    }

    public byte[] getChecksum() {
        return checksum;
    }
}
