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
        String hex = this.getClass().getClassLoader().getResourceAsStream("metadata-extrinsic-hex.txt").text
        byte[] data = Hex.decodeHex(hex)
        ScaleCodecReader rdr = new ScaleCodecReader(data)

        when:
        def result = extrinsicReader.read(rdr)

        then:
        result != null
        result.type == 870
        result.version == 4
        // assert signedExtensions first element is "CheckVersion"
        with(result.signedExtensions[0]) {
            identifier == "CheckNonZeroSender"
            type == 872
            additionalSigned == 50
        }
        with(result.signedExtensions[1]) {
            identifier == "CheckSpecVersion"
            type == 873
            additionalSigned == 4
        }
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