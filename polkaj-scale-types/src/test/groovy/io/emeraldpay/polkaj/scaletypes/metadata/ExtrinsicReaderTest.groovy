package io.emeraldpay.polkaj.scaletypes.metadata

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import io.emeraldpay.polkaj.scale.ScaleReader
import io.emeraldpay.polkaj.scaletypes.v14.MetadataReaderv14
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class ExtrinsicReaderTest extends Specification {

    MetadataReaderv14.ExtrinsicReader extrinsicReader = new MetadataReaderv14.ExtrinsicReader();
    public static final ScaleReader<Integer> INT32_READER = ScaleCodecReader.INT32;

    def "Test read method"() {
        setup:
        String hex = this.getClass().getClassLoader().getResourceAsStream("hex.txt").text
        byte[] data = Hex.decodeHex(hex.substring(2))
        ScaleCodecReader rdr = new ScaleCodecReader(data)

        when:
        def result = extrinsicReader.read(rdr)

        then:
        result != null
        // Add more assertions here based on the expected result
    }

    def "Test read method with large list"() {
        setup:
        byte[] data = Hex.decodeHex("00000371")
        ScaleCodecReader rdr = new ScaleCodecReader(data)

        when:
        def result = INT32_READER.read(rdr)

        then:
        result != null

        where:
        hex         | value
        "00000371"  | 881
    }
}