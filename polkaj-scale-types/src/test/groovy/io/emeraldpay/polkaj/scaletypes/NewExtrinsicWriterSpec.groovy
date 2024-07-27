package io.emeraldpay.polkaj.scaletypes

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import io.emeraldpay.polkaj.types.Address
import io.emeraldpay.polkaj.types.DotAmount
import io.emeraldpay.polkaj.types.Hash512
import spock.lang.Specification

class NewExtrinsicWriterSpec extends Specification {

    def "Encode known transfer"() {
        setup:
        def codec = new ExtrinsicWriter(new BalanceTransferWriter())
        def tx = new Extrinsic().tap {
            tx = new Extrinsic.TransactionInfo().tap {
                sender = Address.from("5GW83GQ53B3UGAMCyoFeFTJV8GKwU6bDjF3iRksdxtSt8QNU")
                era = 0
                nonce = 77
                tip = new DotAmount(BigInteger.ZERO, DotAmount.Westies)
                signature = new Extrinsic.SR25519Signature(
                        Hash512.from("0x28f876bd966b8f6bdf64f454744762e6a2093261674fc2b2f500703ed63ad37c4158efb077bc9fc23fed5fcf6b2a8e70410befdfdea2de2a0396262440a8838d")
                )
            }
            call = new BalanceTransfer(4, 3).tap {
                destination = Address.from("5GW83GQ53B3UGAMCyoFeFTJV8GKwU6bDjF3iRksdxtSt8QNU")
                balance = DotAmount.from(0.01, DotAmount.Westies);
            }
        }
        when:
        def buf = new ByteArrayOutputStream()
        def writer = new ScaleCodecWriter(buf)
        then:
        writer.write(codec, tx)
        writer.close()

     //   def act = "8400c45379a6be360d9d9aed6c72508765e2f9fbca9a9ec7af60b1413b90f87fdb570128f876bd966b8f6bdf64f454744762e6a2093261674fc2b2f500703ed63ad37c4158efb077bc9fc23fed5fcf6b2a8e70410befdfdea2de2a0396262440a8838d00350100040300c45379a6be360d9d9aed6c72508765e2f9fbca9a9ec7af60b1413b90f87fdb570700e40b5402"
    }
}
