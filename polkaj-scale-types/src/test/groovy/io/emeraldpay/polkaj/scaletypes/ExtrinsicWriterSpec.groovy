package io.emeraldpay.polkaj.scaletypes

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import io.emeraldpay.polkaj.types.Address
import io.emeraldpay.polkaj.types.DotAmount
import io.emeraldpay.polkaj.types.Hash512
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class ExtrinsicWriterSpec extends Specification {

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
            call = new BalanceTransfer(4, 0).tap {
                destination = Address.from("5GW83GQ53B3UGAMCyoFeFTJV8GKwU6bDjF3iRksdxtSt8QNU")
                balance = DotAmount.from(0.01, DotAmount.Westies);
            }
        }
        when:
        def buf = new ByteArrayOutputStream()
        def writer = new ScaleCodecWriter(buf)
        writer.write(codec, tx)
        writer.close()
        def act = Hex.encodeHexString(buf.toByteArray())
        then:
        act == "41028400b8fdf4f080eeaa6d3f32a445c91c7effa6ffef16d5fe81783837ab7a23602b3b01bc11655de6e7461b0951353db25f4aaf67a58db547fa3a2f20cbcd7772ba715f8ccbe9d8bddf253c7f6e6f6acb83848a7da1f27de248afca10d3291de92ede8ce5000c00040000483eae8765348ef3e347e6b55995f99353223a8b28cf63829554933bcd5e801d0780cff40808"
    }

    def "Encode known transfer_keep_alive"() {
        setup:
        def codec = new ExtrinsicWriter(new BalanceTransferWriter())
        def tx = new Extrinsic().tap {
            tx = new Extrinsic.TransactionInfo().tap {
                sender = Address.from("5FqBfbPzAD8v8M3XQQEixXJW7HmXZ8JLqLfibxj8zjuPkipz")
                era = 0
                nonce = 0
                tip = DotAmount.fromPlancks(7750000718L, DotAmount.Westies)
                signature = new Extrinsic.ED25519Signature(
                        Hash512.from("0x6b47873769d702332fc2dd76d2891178c3b813aa2175c06b31074e6b163ecb95196c6a520894d4f1a36806490d7213a213834d6c12186bf89ce311d481ae1d09")
                )
            }
            call = new BalanceTransfer(4, 3).tap {
                destination = Address.from("5GAiqfv7kwGxnLpCue9pFt7zwt4u1aoYM7p9tHJPGMjNHpEz")
                balance = DotAmount.from(0.1, DotAmount.Westies)
            }
        }
        when:
        def buf = new ByteArrayOutputStream()
        def writer = new ScaleCodecWriter(buf)
        writer.write(codec, tx)
        writer.close()
        def act = Hex.encodeHexString(buf.toByteArray())
        then:
        act == "51028400a6a11c9cf2b58fd914ffc8f667e31e8e6175514833a2892100c8c3bcc9049061006b47873769d702332fc2dd76d2891178c3b813aa2175c06b31074e6b163ecb95196c6a520894d4f1a36806490d7213a213834d6c12186bf89ce311d481ae1d090000074ea0efcd01040300b587b6f4e35da071696161b345b378eb282c884a03d23cf7e44ba27cf3f63d4c0700e8764817"
    }
}
