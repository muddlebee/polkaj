package io.emeraldpay.polkaj.scaletypes.metadata

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import io.emeraldpay.polkaj.scale.reader.ListReader
import io.emeraldpay.polkaj.scaletypes.v14.MetadataReaderv14
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class PalletsReaderTest extends Specification {

     MetadataReaderv14.ListPalletReader palletReader = new MetadataReaderv14.ListPalletReader()

    def "Test read method"() {
        setup:
        String hex = this.getClass().getClassLoader().getResourceAsStream("metadata-pallet-hex.txt").text
        byte[] data = Hex.decodeHex(hex.startsWith("0x") ? hex.substring(2) : hex)
        ScaleCodecReader rdr = new ScaleCodecReader(data)

        when:
        def result = palletReader.read(rdr)

        then:
        result != null
    }

}