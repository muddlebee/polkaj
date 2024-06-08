package io.emeraldpay.polkaj.scaletypes.metadata

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import io.emeraldpay.polkaj.scaletypes.v14.MetadataReaderv14
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class LookupReaderTest extends Specification {

    MetadataReaderv14.LookupReader lookupReader = new MetadataReaderv14.LookupReader()

    def "Test read method"() {
        setup:
        String hex = this.getClass().getClassLoader().getResourceAsStream("metadata-lookup-hex.txt").text
        byte[] data = Hex.decodeHex(hex.startsWith("0x") ? hex.substring(2) : hex)
        ScaleCodecReader rdr = new ScaleCodecReader(data)

        when:
        def result = lookupReader.read(rdr)

        then:
        result != null
    }

}