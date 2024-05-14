import io.emeraldpay.polkaj.api.PolkadotApi;
import io.emeraldpay.polkaj.api.StandardCommands;
import io.emeraldpay.polkaj.apiws.JavaHttpSubscriptionAdapter;
import io.emeraldpay.polkaj.scale.ScaleExtract;
import io.emeraldpay.polkaj.scaletypes.v14.MetadataContainer;
import io.emeraldpay.polkaj.scaletypes.v14.MetadataReaderv14;
import io.emeraldpay.polkaj.types.DotAmountFormatter;


public class Metadata_v14_test {

    private static final DotAmountFormatter AMOUNT_FORMAT = DotAmountFormatter.autoFormatter();


    public static void main(String[] args) throws Exception {

            String api = "wss://westend.api.onfinality.io/public-ws";
          //  String api = "wss://rpc-polkadot.luckyfriday.io";
            if (args.length >= 1) {
                api = args[0];
            }
            System.out.println("Connect to: " + api);

            final JavaHttpSubscriptionAdapter adapter = JavaHttpSubscriptionAdapter.newBuilder().connectTo(api).build();
            PolkadotApi client = PolkadotApi.newBuilder().subscriptionAdapter(adapter).build();
            System.out.println("Connected: " + adapter.connect().get());

            try {
                // get current runtime metadata to correctly build the extrinsic
                MetadataContainer metadata = client.execute(
                                StandardCommands.getInstance().stateMetadata()
                        )
                        .thenApply(ScaleExtract.fromBytesData(new MetadataReaderv14()))
                        .get();
                System.out.println(metadata);

            }catch (Exception e){
                e.printStackTrace();
                adapter.close();
            }

    }
}
