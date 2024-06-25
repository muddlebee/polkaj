/*
package io.emeraldpay.polkaj.schnorrkel;

import io.emeraldpay.polkaj.schnorrkel.BIP39
import spock.lang.Specification
import spock.lang.Unroll

class BIP39Spec extends Specification {

    private static final Class<? extends Object> BIP39 = ;

    def setup() {
        // Mock the native methods
        GroovySpy(BIP39, global: true)
    }

    def "generate should return a mnemonic phrase of the correct length"() {
        given:
        BIP39.generate(12) >> "word1 word2 word3 word4 word5 word6 word7 word8 word9 word10 word11 word12"

        when:
        def result = BIP39.generate(12)

        then:
        result.split(" ").length == 12
    }

    def "toEntropy should convert a phrase to entropy"() {
        given:
        def phrase = "word1 word2 word3 word4 word5 word6 word7 word8 word9 word10 word11 word12"
        BIP39.toEntropy(phrase) >> [1, 2, 3, 4] as byte[]

        when:
        def result = BIP39.toEntropy(phrase)

        then:
        result == [1, 2, 3, 4] as byte[]
    }

    def "toMiniSecret should convert a phrase and password to a mini secret"() {
        given:
        def phrase = "word1 word2 word3 word4 word5 word6 word7 word8 word9 word10 word11 word12"
        def password = "mypassword"
        BIP39.toMiniSecret(phrase, password) >> [5, 6, 7, 8] as byte[]

        when:
        def result = BIP39.toMiniSecret(phrase, password)

        then:
        result == [5, 6, 7, 8] as byte[]
    }

    def "toSeed should convert a phrase and password to a seed"() {
        given:
        def phrase = "word1 word2 word3 word4 word5 word6 word7 word8 word9 word10 word11 word12"
        def password = "mypassword"
        BIP39.toSeed(phrase, password) >> [9, 10, 11, 12] as byte[]

        when:
        def result = BIP39.toSeed(phrase, password)

        then:
        result == [9, 10, 11, 12] as byte[]
    }

    @Unroll
    def "validate should return #expected for phrase #phrase"() {
        given:
        BIP39.validate(phrase) >> expected

        expect:
        BIP39.validate(phrase) == expected

        where:
        phrase                                                                              | expected
        "word1 word2 word3 word4 word5 word6 word7 word8 word9 word10 word11 word12"        | true
        "invalid phrase"                                                                    | false
        "word1 word2 word3 word4 word5 word6 word7 word8 word9 word10 word11 word12 word13" | false
    }
}*/
