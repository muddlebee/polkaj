import io.emeraldpay.polkaj.api.PolkadotApi;
import io.emeraldpay.polkaj.api.StandardSubscriptions;
import io.emeraldpay.polkaj.apiws.JavaHttpSubscriptionAdapter;
import io.emeraldpay.polkaj.schnorrkel.Schnorrkel;
import io.emeraldpay.polkaj.ss58.SS58Type;
import io.emeraldpay.polkaj.types.*;
import org.apache.commons.codec.binary.Hex;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public class Fetch_metadata {

    private static final DotAmountFormatter AMOUNT_FORMAT = DotAmountFormatter.autoFormatter();


    public static void main(String[] args) throws Exception {

        try {

            String api = "wss://westend.api.onfinality.io/public-ws";
            if (args.length >= 1) {
                api = args[0];
            }
            System.out.println("Connect to: " + api);

            Schnorrkel.KeyPair aliceKey;
            Address alice;
            Address bob;
            if (args.length >= 3) {
                System.out.println("Use provided addresses");
                aliceKey = Schnorrkel.getInstance().generateKeyPairFromSeed(Hex.decodeHex(args[1]));
                bob = Address.from(args[2]);
            } else {
                System.out.println("Use standard accounts for Alice and Bob, expected to run against development network");
                aliceKey = Schnorrkel.getInstance().generateKeyPairFromSeed(
                        Hex.decodeHex("e5be9a5092b81bca64be81d212e7f2f9eba183bb7a90954f7b76361f6edb5c0a")
                );
                bob = Address.from("5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty");
            }
            alice = new Address(SS58Type.Network.CANARY, aliceKey.getPublicKey());

            Random random = new Random();
            DotAmount amount = DotAmount.fromPlancks(
                    Math.abs(random.nextLong()) % DotAmount.fromDots(0.002).getValue().longValue()
            );

            final JavaHttpSubscriptionAdapter adapter = JavaHttpSubscriptionAdapter.newBuilder().connectTo(api).build();
            PolkadotApi client = PolkadotApi.newBuilder().subscriptionAdapter(adapter).build();
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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
