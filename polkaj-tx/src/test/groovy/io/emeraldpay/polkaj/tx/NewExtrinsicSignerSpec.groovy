package io.emeraldpay.polkaj.tx

import io.emeraldpay.polkaj.scaletypes.BalanceTransfer
import io.emeraldpay.polkaj.scaletypes.BalanceTransferWriter
import io.emeraldpay.polkaj.scaletypes.Extrinsic
import io.emeraldpay.polkaj.schnorrkel.Schnorrkel
import io.emeraldpay.polkaj.schnorrkel.SchnorrkelNative
import io.emeraldpay.polkaj.ss58.SS58Type
import io.emeraldpay.polkaj.types.Address
import io.emeraldpay.polkaj.types.DotAmount
import io.emeraldpay.polkaj.types.Hash256
import io.emeraldpay.polkaj.types.Hash512
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class NewExtrinsicSignerSpec extends Specification {

    def "Sign amount"() {
        Schnorrkel schnorrkel = Schnorrkel.getInstance();
        String seedPhrase = "cause trip unique fossil hello supreme release know design marriage never filter";
        String password = "";
        byte[] seed = SchnorrkelNative.toMiniSecret(seedPhrase, password);
        Schnorrkel.KeyPair aliceKey = schnorrkel.generateKeyPairFromSeed(seed);

        Address source = new Address(SS58Type.Network.WESTEND, aliceKey.getPublicKey());
        Address dest = Address.from("5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty");
        DotAmount tip = DotAmount.from(0, DotAmount.Westies);
        setup:
        ExtrinsicContext extrinsic = ExtrinsicContext.newBuilder()
                .runtime(26, 1014000)
                .genesis(Hash256.from("0xe143f23803ac50e8f6f8e62695d1ce9e4e1d68aa36c1cd2cfd15340213f3423e"))
                .nonce(77) //TODO: correct
                .eraBlockHash(Hash256.from("0xe143f23803ac50e8f6f8e62695d1ce9e4e1d68aa36c1cd2cfd15340213f3423e"))
                .tip(tip)
                .build()
        def transfer = AccountRequests.transfer()
                .module(4, 3)
                .from(Address.from("5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"))
                .to(Address.from("5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty"))
                .amount(DotAmount.fromDots(123))
                .sign(TestKeys.aliceKey, extrinsic)
                .build()
        def act = transfer.encodeRequest()
//        then:
//        println("")

//        BalanceTransfer call = new BalanceTransfer(4, 3).tap {
//            destination = dest
//            balance = DotAmount.from(0.01, DotAmount.Westies);
//        }
//        ExtrinsicSigner signer = new ExtrinsicSigner<>(new BalanceTransferWriter())
//        when:
//        def payload = signer.getPayload(extrinsic, call)
//        then:
//        println("ExtrinsicContext: ${extrinsic}")
//        println("Payload: ${Hex.encodeHexString(payload)}")

//        Hex.encodeHexString(payload) == "900500008eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a4804001c0012000000030000004c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd51224c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122"

//        when:
//        def valid = signer.isValid(extrinsic, call,
//                new Extrinsic.SR25519Signature(Hash512.from("0xc6d3033548c4b2752b50da78e936d894d946de79b14be126a9dd61100c736d7a6a66c2973ebc8f65ed969a1de0f9cecdeaf8c22130486a27a875f8b35ce5828c")),
//                source
//        )
//        then:
//        valid
    }

    def "Sign small amount"() {
        setup:
        Schnorrkel schnorrkel = Schnorrkel.getInstance();
        String seedPhrase = "cause trip unique fossil hello supreme release know design marriage never filter";
        String password = "";
        byte[] seed = SchnorrkelNative.toMiniSecret(seedPhrase, password);
        Schnorrkel.KeyPair aliceKey = schnorrkel.generateKeyPairFromSeed(seed);
        Address source = new Address(SS58Type.Network.WESTEND, aliceKey.getPublicKey());
        Address dest = Address.from("5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty");
        ExtrinsicContext context = ExtrinsicContext.newBuilder()
                .runtime(26, 1014000)
                .genesis(Hash256.from("0xe143f23803ac50e8f6f8e62695d1ce9e4e1d68aa36c1cd2cfd15340213f3423e"))
                .nonce(77) //TODO: correct
                .eraBlockHash(Hash256.from("0xe143f23803ac50e8f6f8e62695d1ce9e4e1d68aa36c1cd2cfd15340213f3423e"))
                .tip(DotAmount.from(0, DotAmount.Westies))
                .build()
        BalanceTransfer call = new BalanceTransfer(4, 3).tap {
            destination = dest
            balance = DotAmount.from(0.01, DotAmount.Westies)
        }
        ExtrinsicSigner signer = new ExtrinsicSigner<>(new BalanceTransferWriter())
        when:
        def signature = new Extrinsic.SR25519Signature(signer.sign(context, call, aliceKey))
        then:
        println("signature: ${signature}")
        //output 0x88f770dca7621d49f15afca72e2d092063c7a6a1812aab44edc84ac6da8f9d08f02eb21d5d757518f1242ed3bb81a0c020760670f6adf71b6a865027cd3e5081
    }
}
