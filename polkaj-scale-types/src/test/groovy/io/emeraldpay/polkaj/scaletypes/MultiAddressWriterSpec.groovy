package io.emeraldpay.polkaj.scaletypes

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import io.emeraldpay.polkaj.scale.UnionValue
import io.emeraldpay.polkaj.types.Address
import spock.lang.Specification

class MultiAddressWriterSpec extends Specification{

    def "AccountIDWriter writes MultiAddress.AccountID correctly"() {
        given:
        def accountIDWriter = new MultiAddressWriter.AccountIDWriter()
        def outStream = new ByteArrayOutputStream()
        def scaleWriter = new ScaleCodecWriter(outStream)
        def accountID = new MultiAddress.AccountID(new byte[32]) // Assuming a constructor exists for simplicity
        def MultiAddressWriter SENDER_WRITER = new MultiAddressWriter();
        def address = new UnionValue(0, new MultiAddress.AccountID(Address.from("ED3aw4s68wTDscCbWnCCw94qSrkA1D8HcUXC8ytaoM2X2xd")))


        when:
        SENDER_WRITER.write(scaleWriter, address)
        accountIDWriter.write(scaleWriter, accountID)
        def result = outStream.toByteArray()

        then:
        result.size() == 32 // Verify that the account ID was written correctly
    }
}
