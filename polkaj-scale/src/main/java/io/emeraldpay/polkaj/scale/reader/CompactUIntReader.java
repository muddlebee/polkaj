package io.emeraldpay.polkaj.scale.reader;

import io.emeraldpay.polkaj.scale.CompactMode;
import io.emeraldpay.polkaj.scale.ScaleReader;
import io.emeraldpay.polkaj.scale.ScaleCodecReader;

public class CompactUIntReader implements ScaleReader<Integer> {

    /**
     * @param rdr reader with the encoded data
     * @return integer value
     * @throws UnsupportedOperationException if the value is encoded with more than four bytes (use {@link CompactBigIntReader})
     */
    @Override
    public Integer read(ScaleCodecReader rdr) {
        try {
            int i = rdr.readUByte();
            CompactMode mode = CompactMode.byValue((byte) (i & 0b11));
            System.out.println("mode: " + mode);
            if (mode == CompactMode.SINGLE) {
                return i >> 2;
            }
            if (mode == CompactMode.TWO) {
                return (i >> 2)
                        + (rdr.readUByte() << 6);
            }
            if (mode == CompactMode.FOUR) {
                return (i >> 2) +
                        (rdr.readUByte() << 6) +
                        (rdr.readUByte() << (6 + 8)) +
                        (rdr.readUByte() << (6 + 2 * 8));
            }
            throw new UnsupportedOperationException("Mode " + mode + " is not implemented");
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
            if (e.getMessage().contains("Mode BIGINT is not implemented")) {
                return rdr.readCompactBigInt().intValue();
            } else {
                throw e;
            }
        }
    }
}
