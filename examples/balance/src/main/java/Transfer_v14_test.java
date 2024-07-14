import io.emeraldpay.polkaj.api.PolkadotApi;
import io.emeraldpay.polkaj.api.StandardCommands;
import io.emeraldpay.polkaj.api.StandardSubscriptions;
import io.emeraldpay.polkaj.apiws.JavaHttpSubscriptionAdapter;
import io.emeraldpay.polkaj.scale.ScaleExtract;
import io.emeraldpay.polkaj.scaletypes.AccountInfo;
import io.emeraldpay.polkaj.scaletypes.Metadata;
import io.emeraldpay.polkaj.scaletypes.MetadataReader;
import io.emeraldpay.polkaj.schnorrkel.Schnorrkel;
import io.emeraldpay.polkaj.schnorrkel.SchnorrkelException;
import io.emeraldpay.polkaj.schnorrkel.SchnorrkelNative;
import io.emeraldpay.polkaj.ss58.SS58Type;
import io.emeraldpay.polkaj.tx.AccountRequests;
import io.emeraldpay.polkaj.tx.ExtrinsicContext;
import io.emeraldpay.polkaj.types.*;
import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public class Transfer_v14_test {

    private static final DotAmountFormatter AMOUNT_FORMAT = DotAmountFormatter.autoFormatter();


    public static void main(String[] args) throws Exception {
        String api = "wss://westend.api.onfinality.io/public-ws";

        if (args.length >= 1) {
            api = args[0];
        }
        System.out.println("Connect to: " + api);

/*
        Schnorrkel.KeyPair aliceKey;
        Address alice;
        Address bob;
        if (args.length >= 3) {
            System.out.println("Use provided addresses");
            aliceKey = Schnorrkel.getInstance().generateKeyPairFromSeed(Hex.decodeHex(args[1]));
            bob =  Address.from(args[2]);
        } else {
            System.out.println("Use standard accounts for Alice and Bob, expected to run against development network");
            aliceKey = Schnorrkel.getInstance().generateKeyPairFromSeed(
                    Hex.decodeHex("e5be9a5092b81bca64be81d212e7f2f9eba183bb7a90954f7b76361f6edb5c0a")
            );
            bob =  Address.from("5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty");
        }
        alice = new Address(SS58Type.Network.WESTEND , aliceKey.getPublicKey());

        */

        try {
            Schnorrkel schnorrkel = Schnorrkel.getInstance();
            String seedPhrase = "cause trip unique fossil hello supreme release know design marriage never filter";
            String password = "";
            byte[] seed = SchnorrkelNative.toMiniSecret(seedPhrase, password);
            Schnorrkel.KeyPair aliceKey = schnorrkel.generateKeyPairFromSeed(seed);

            Address alice = new Address(SS58Type.Network.WESTEND, aliceKey.getPublicKey());
            System.out.println("TEST_ADDRESS : " + alice.toString());
            assert alice.toString().equals("5GEwX4bq8uzehVgdTKfmPrXPU61XoUdqfCZmWxs1tajKz9K8");

            Address bob = Address.from("5GW83GQ53B3UGAMCyoFeFTJV8GKwU6bDjF3iRksdxtSt8QNU");
            // print address details
            System.out.println("Bob Address Network : " + bob.getNetwork());


  /*      Schnorrkel schnorrkel = Schnorrkel.getInstance();
        String seedPhrase = "scrub thought hamster laptop frog raise begin slide squeeze path famous dinner";
        String password = "test123";
        byte[] seed = SchnorrkelNative.toMiniSecret(seedPhrase, password);
        Schnorrkel.KeyPair baseKeyPair = schnorrkel.generateKeyPairFromSeed(seed);

        // Create a 32-byte chain code for hard derivation
        byte[] chainCodeBytes = new byte[32];
        ByteBuffer.wrap(chainCodeBytes).order(ByteOrder.LITTLE_ENDIAN).put(password.getBytes());
        Schnorrkel.ChainCode chainCode = new Schnorrkel.ChainCode(chainCodeBytes);

        Schnorrkel.KeyPair derivedKeyPair = schnorrkel.deriveKeyPair(baseKeyPair, chainCode);
        Address TEST_ADDRESS = new Address(SS58Type.Network.WESTEND, derivedKeyPair.getPublicKey());
        System.out.println("TEST_ADDRESS : " + TEST_ADDRESS.toString());
        assert TEST_ADDRESS.toString().equals("5EL526Sqyn8o7gwew5nQoftRUiJXYZr22GG7GJ9XMLKZFdco");*/


            Random random = new Random();
//            DotAmount amount = DotAmount.fromPlancks(
//                    Math.abs(random.nextLong()) % DotAmount.fromDots(0.2).getValue().longValue()
//            );

            final JavaHttpSubscriptionAdapter adapter = JavaHttpSubscriptionAdapter.newBuilder().connectTo(api).build();
            try (PolkadotApi client = PolkadotApi.newBuilder().subscriptionAdapter(adapter).build()) {
                System.out.println("Connected: " + adapter.connect().get());

                // Subscribe to block heights
                AtomicLong height = new AtomicLong(0);
                CompletableFuture<Long> waitForBlocks = new CompletableFuture<>();
                client.subscribe(
                        StandardSubscriptions.getInstance().newHeads()
                ).get().handler((event) -> {
                    long current = event.getResult().getNumber();
                    System.out.println("Current height: " + current);
                    if (height.get() == 0) {
                        height.set(current);
                    } else {
                        long blocks = current - height.get();
                        if (blocks > 3) {
                            waitForBlocks.complete(current);
                        }
                    }
                });

                // Subscribe to balance updates
                AccountRequests.AddressBalance aliceAccountRequest = AccountRequests.balanceOf(alice);
                AccountRequests.AddressBalance bobAccountRequest = AccountRequests.balanceOf(bob);
                client.subscribe(
                        StandardSubscriptions.getInstance()
                                .storage(Arrays.asList(
                                        // need to provide actual encoded requests
                                        aliceAccountRequest.encodeRequest(),
                                        bobAccountRequest.encodeRequest())
                                )
                ).get().handler((event) -> {
                    event.getResult().getChanges().forEach((change) -> {
                        AccountInfo value = null;
                        Address target = null;
                        if (aliceAccountRequest.isKeyEqualTo(change.getKey())) {
                            value = aliceAccountRequest.apply(change.getData());
                            target = alice;
                        } else if (bobAccountRequest.isKeyEqualTo(change.getKey())) {
                            value = bobAccountRequest.apply(change.getData());
                            target = bob;
                        } else {
                            System.err.println("Invalid key: " + change.getKey());
                        }
                        if (value != null) {
                            System.out.println("Balance update. User: " + target + ", new balance: " + AMOUNT_FORMAT.format(value.getData().getFree()));
                        }
                    });
                });

//            // get current runtime metadata to correctly build the extrinsic
//            Metadata metadata = client.execute(
//                        StandardCommands.getInstance().stateMetadata()
//                    )
//                    .thenApply(ScaleExtract.fromBytesData(new MetadataReader()))
//                    .get();

                //TODO:1 Fix amount library

                // get current balance to show, optional
                AccountInfo aliceAccount = aliceAccountRequest.execute(client).get();
                BigInteger value = new BigInteger(String.valueOf(100000000));
                DotAmount amount = DotAmount.from(0.01, DotAmount.Westies);
                System.out.println("------");
                System.out.println("Currently available: " + AMOUNT_FORMAT.format(aliceAccount.getData().getFree()));
                System.out.println("Transfer           : " + AMOUNT_FORMAT.format(amount) + " from " + alice + " to " + bob);

                //TODO:4 Perform Transaction

                // prepare context for execution
                ExtrinsicContext context = ExtrinsicContext.newAutoBuilder(alice, client)
                        .get()
                        .build();
                System.out.println("ExtrinsicContext : " + context);
                // prepare call, and sign with sender Secret Key within the context
                Metadata metadata = new Metadata();
                AccountRequests.Transfer transfer = AccountRequests.transfer()
                        //  .runtime(metadata) //TODO: hardcode call index and init
                        .module(4, 3)
                        .from(alice)
                        .to(bob)
                        .amount(amount)
                        .sign(aliceKey, context)
                        .build(); //TODO:2   Encoded call data (from step 2) for args, refer below


                /**
                 * [
                 *   Pallet Index (Balances pallet)
                 *   Call Index (transferKeepAlive function)
                 *   dest (encoded as MultiAddress)
                 *   value (encoded as Compact<u128>)
                 * ]
                 */


                System.out.println("Using genesis : " + context.getGenesisHash());
                System.out.println("Using runtime : " + context.getTransactionVersion() + ", " + context.getSpecVersion());
                System.out.println("Using nonce   : " + context.getNonce());

                ByteData req = transfer.encodeRequest();
                System.out.println("RPC Request Payload: " + req);
//                Hash256 txid = client.execute(
//                        StandardCommands.getInstance().authorSubmitExtrinsic(req)
//                ).get();
//                System.out.println("Tx Hash: " + txid);

                // wait for a few blocks, to show how subscription to storage changes works, which will
                // notify about relevant updates during those blocks
                waitForBlocks.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
