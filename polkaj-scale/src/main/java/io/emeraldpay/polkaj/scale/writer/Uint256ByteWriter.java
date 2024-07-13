package io.emeraldpay.polkaj.scale.writer;

import io.emeraldpay.polkaj.scale.ScaleWriter;

public class Uint256ByteWriter implements ScaleWriter<byte[]> {

    @Override
    public void write(io.emeraldpay.polkaj.scale.ScaleCodecWriter wrt, byte[] value) throws java.io.IOException {
        if (value.length != 32) {
            throw new IllegalArgumentException("Value must be 32 byte array");
        }
        wrt.writeByteArray(value);
    }
}
