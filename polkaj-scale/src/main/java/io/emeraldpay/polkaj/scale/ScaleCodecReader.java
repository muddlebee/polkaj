package io.emeraldpay.polkaj.scale;

import io.emeraldpay.polkaj.scale.reader.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SCALE codec reader
 */
public class ScaleCodecReader {

    private static final Logger logger = LoggerFactory.getLogger(ScaleCodecReader.class);

    public static final UByteReader UBYTE = new UByteReader();
    public static final UInt16Reader UINT16 = new UInt16Reader();
    public static final UInt32Reader UINT32 = new UInt32Reader();
    public static final UInt128Reader UINT128 = new UInt128Reader();
    public static final Int32Reader INT32 = new Int32Reader();
    public static final CompactUIntReader COMPACT_UINT = new CompactUIntReader();
    public static final CompactBigIntReader COMPACT_BIGINT = new CompactBigIntReader();
    public static final BoolReader BOOL = new BoolReader();
    public static final BoolOptionalReader BOOL_OPTIONAL = new BoolOptionalReader();
    public static final StringReader STRING = new StringReader();

    private byte[] source;
    private int pos = 0;

    public ScaleCodecReader(byte[] source) {
        this.source = source;
    }

    /**
     * @return true if has more elements
     */
    public boolean hasNext() {
        return pos < source.length;
    }

    /**
     * Move reader position forward (or backward for negative value)
     *
     * @param len amount to bytes to skip
     */
    public void skip(int len) {
        if (len < 0 && Math.abs(len) > pos) {
            throw new IllegalArgumentException("Position cannot be negative: " + pos + " " + len);
        }
        pos += len;
    }

    /**
     * Specify a new position
     *
     * @param pos position
     */
    public void seek(int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("Position cannot be negative: " + pos);
        } else if (pos >= source.length) {
            throw new IllegalArgumentException("Position " + pos + " must be strictly smaller than source length: " + source.length);
        }

        this.pos = pos;
    }

    /**
     * @return a next single byte from reader
     */
    public byte readByte() {
        if (!hasNext()) {
            throw new IndexOutOfBoundsException("Cannot read " + pos + " of " + source.length);
        }
        return source[pos++];
    }

    /**
     * Read complex value from the reader
     *
     * @param scaleReader reader implementation
     * @param <T>         resulting type
     * @return read value
     */
    public <T> T read(ScaleReader<T> scaleReader) {
        if (scaleReader == null) {
            throw new IllegalArgumentException("ScaleReader cannot be null");
        }
        try {
            return scaleReader.read(this);
        } catch (Exception e) {
            throw e;
        }
    }

    public int readUByte() {
        return UBYTE.read(this);
    }

    public int readUint16() {
        return UINT16.read(this);
    }

    public long readUint32() {
        return UINT32.read(this);
    }

    public BigInteger readUint128() {
        return UINT128.read(this);
    }

    public int readCompactInt() {
        return COMPACT_UINT.read(this);
    }

    //readCompactBigInt
    public BigInteger readCompactBigInt() {
        return COMPACT_BIGINT.read(this);
    }

    public boolean readBoolean() {
        return BOOL.read(this);
    }

    /**
     * Read optional value from the reader
     *
     * @param scaleReader reader implementation
     * @param <T>         resulting type
     * @return optional read value
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> readOptional(ScaleReader<T> scaleReader) {
        if (scaleReader instanceof BoolReader || scaleReader instanceof BoolOptionalReader) {
            return (Optional<T>) BOOL_OPTIONAL.read(this);
        }
        boolean some = readBoolean();
        if (some) {
            return Optional.of(read(scaleReader));
        } else {
            return Optional.empty();
        }
    }

    public byte[] readUint256() {
        return readByteArray(32);
    }

    public byte[] readByteArray() {
        int len = readCompactInt();
        return readByteArray(len);
    }


    public byte[] readByteArray(int len) {
//        System.out.println("len: {}" + len);
//        System.out.println("pos: {}"+ pos);
//        System.out.println("source.length: {}" + source.length);
        if (pos + len > source.length) {
            throw new IllegalArgumentException("Not enough data to read " + len + " bytes");
        }
        byte[] result = new byte[len];
        System.arraycopy(source, pos, result, 0, result.length);
        pos += len;
//        System.out.println("");
//        System.out.println("result : {}"+ Arrays.toString(result));
//        System.out.println("");
        return result;
    }

    /**
     * Read string, encoded as UTF-8 bytes
     *
     * @return string value
     */
    public String readString() {
        return new String(readByteArray());
    }

}
