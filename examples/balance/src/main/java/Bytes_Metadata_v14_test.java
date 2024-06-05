import io.emeraldpay.polkaj.api.PolkadotApi;
import io.emeraldpay.polkaj.api.StandardCommands;
import io.emeraldpay.polkaj.apiws.JavaHttpSubscriptionAdapter;
import io.emeraldpay.polkaj.types.ByteData;
import io.emeraldpay.polkaj.types.DotAmountFormatter;

import java.util.concurrent.CompletableFuture;


public class Bytes_Metadata_v14_test {

    private static final DotAmountFormatter AMOUNT_FORMAT = DotAmountFormatter.autoFormatter();


    public static void main(String[] args) throws Exception {

        //     String api = "wss://westend.api.onfinality.io/public-ws";
         String api = "wss://polkadot-rpc.dwellir.com";

        if (args.length >= 1) {
                api = args[0];
            }
            System.out.println("Connect to: " + api);

            final JavaHttpSubscriptionAdapter adapter = JavaHttpSubscriptionAdapter.newBuilder().connectTo(api).build();
            PolkadotApi client = PolkadotApi.newBuilder().subscriptionAdapter(adapter).build();
            System.out.println("Connected: " + adapter.connect().get());

            try {
//                // get current runtime metadata to correctly build the extrinsic
//                MetadataContainer metadata = client.execute(
//                                StandardCommands.getInstance().stateMetadata()
//                        )
//                        .thenApply(ScaleExtract.fromBytesData(new MetadataReaderv14()))
//                        .get();
//                System.out.println(metadata);

                CompletableFuture<ByteData> meta = client.execute(
                        StandardCommands.getInstance().stateMetadata()
                );

                // process meta and print byte data
                byte[] metaBytes = meta.get().getBytes();
                System.out.println("metaBytes.length " + metaBytes.length);

                // convert metaBytes to string
                String decodedString = new String(metaBytes, "UTF-8");
                System.out.println(decodedString);


                adapter.close();

            }catch (Exception e){
                e.printStackTrace();
                adapter.close();
            }

    }
}
