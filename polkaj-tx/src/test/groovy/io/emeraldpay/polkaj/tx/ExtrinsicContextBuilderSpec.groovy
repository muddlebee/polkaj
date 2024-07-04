package io.emeraldpay.polkaj.tx

import io.emeraldpay.polkaj.types.DotAmount
import io.emeraldpay.polkaj.types.Hash256
import spock.lang.Specification

class ExtrinsicContextBuilderSpec extends Specification {

    def "Default build"() {
        when:
        def act = ExtrinsicContext.newBuilder()
                .build()
        then:
        act.nonce == 0
        act.era.immortal
        act.blockHash == Hash256.empty()
     //   act.eraHeight == 0
        act.genesisHash == Hash256.empty()
        act.tip == DotAmount.ZERO
        act.specVersion == 254
        act.transactionVersion == 1
    }

    def "Set genesis"() {
        when:
        def act = ExtrinsicContext.newBuilder()
                .genesis(Hash256.from("0x35170a58d341fd81c07ee349438da400ecfb625782cd25e29774203080a54f45"))
                .build()
        then:
        act.genesisHash == Hash256.from("0x35170a58d341fd81c07ee349438da400ecfb625782cd25e29774203080a54f45")
    }

    def "Set genesis for immortal sets era hash"() {
        when:
        def act = ExtrinsicContext.newBuilder()
                .genesis(Hash256.from("0x35170a58d341fd81c07ee349438da400ecfb625782cd25e29774203080a54f45"))
                .build()
        then:
        act.era.immortal
        act.blockHash == Hash256.from("0x35170a58d341fd81c07ee349438da400ecfb625782cd25e29774203080a54f45")
    }

    def "Set runtime"() {
        when:
        def act = ExtrinsicContext.newBuilder()
                .runtime(10, 34)
                .build()
        then:
        act.transactionVersion == 10
        act.specVersion == 34
    }

    def "Set eraBlockHash"() {
        when:
        def act = ExtrinsicContext.newBuilder()
                .eraBlockHash(Hash256.from("0x35170a58d341fd81c07ee349438da400ecfb625782cd25e29774203080a54f45"))
                .build()
        then:
        act.blockHash == Hash256.from("0x35170a58d341fd81c07ee349438da400ecfb625782cd25e29774203080a54f45")
    }

    def "Set nonce"() {
        when:
        def act = ExtrinsicContext.newBuilder()
                .nonce(50161)
                .build()
        then:
        act.nonce == 50161
    }

    def "Set era"() {
        when:
        def act = ExtrinsicContext.newBuilder()
                .era(new Era.Mortal(32768, 20000))
                .build()
        then:
        !act.era.immortal
        act.era.toInteger() == 0x9c4e
    }

    def "Set tip"() {
        when:
        def act = ExtrinsicContext.newBuilder()
                .tip(DotAmount.fromDots(1.2))
                .build()
        then:
        act.tip == DotAmount.fromDots(1.2)
    }

}
