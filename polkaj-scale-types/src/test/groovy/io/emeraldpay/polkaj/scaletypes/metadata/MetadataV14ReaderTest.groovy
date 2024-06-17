package io.emeraldpay.polkaj.scaletypes.metadata

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import io.emeraldpay.polkaj.scaletypes.MetadataReader
import io.emeraldpay.polkaj.scaletypes.v14.MetadataReaderv14
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class MetadataV14ReaderTest extends Specification {


    def "Test read method"() {
        setup:
        String hex = this.getClass().getClassLoader().getResourceAsStream("metadata-v14-westend").text
        byte[] data = Hex.decodeHex(hex.substring(2))
        when:
        def rdr = new ScaleCodecReader(data)
        def metadata = rdr.read(new MetadataReaderv14())

        then:
        metadata != null
    }

}