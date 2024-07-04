package io.emeraldpay.polkaj.tx;

import java.util.concurrent.CompletableFuture;

import io.emeraldpay.polkaj.api.PolkadotApi;
import io.emeraldpay.polkaj.api.StandardCommands;
import io.emeraldpay.polkaj.json.RuntimeVersionJson;
import io.emeraldpay.polkaj.scaletypes.AccountInfo;
import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.DotAmount;
import io.emeraldpay.polkaj.types.Hash256;
import lombok.Data;

/**
 * Context to execute an Extrinsic
 */
@Data
public class ExtrinsicContext {

    private Hash256 blockHash;
    private Era era = Era.IMMORTAL;
    private Hash256 genesisHash;

    //TODO: java compatible
    // method: AnyU8a | IMethod<AnyTuple>;
    private long nonce = 0;
    private int specVersion;
    private DotAmount tip = DotAmount.ZERO;
    private int transactionVersion;
    private long assetId;


    /**
     * Start new builder for the context
     *
     * @return builder
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Automatic asynchronous configuration based on current state of the blockchain
     *
     * @param sender sender
     * @param api    client to current blockchain
     * @return future for the builder
     */
    public static CompletableFuture<Builder> newAutoBuilder(Address sender, PolkadotApi api) {
        return new AutoBuilder(sender).fetch(api);
    }


    public static final class Builder {
        private int transactionVersion; //26
        private int specVersion; //1014000;
        private Hash256 genesis = Hash256.empty();
        private Hash256 blockHash = Hash256.empty();

        private long nonce = 0;
        private Era era = Era.IMMORTAL;
        private DotAmount tip = DotAmount.ZERO;
        //     private long eraHeight = 0;

        public Builder runtime(RuntimeVersionJson version) {
            return runtime(version.getTransactionVersion(), version.getSpecVersion());
        }

        public Builder runtime(int txVersion, int specVersion) {
            this.transactionVersion = txVersion;
            this.specVersion = specVersion;
            return this;
        }

        public Builder genesis(Hash256 genesis) {
            this.genesis = genesis;
            if (era.isImmortal()) {
                this.blockHash = genesis;
            }
            return this;
        }

        public Builder eraBlockHash(Hash256 eraBlockHash) {
            this.blockHash = eraBlockHash;
            return this;
        }

        public Builder nonce(long nonce) {
            this.nonce = nonce;
            return this;
        }

        public Builder era(Era era) {
            this.era = era;
            return this;
        }

        public Builder tip(DotAmount amount) {
            this.tip = amount;
            return this;
        }

        public ExtrinsicContext build() {
            ExtrinsicContext context = new ExtrinsicContext();
            context.setTransactionVersion(transactionVersion);
            context.setSpecVersion(specVersion);
            context.setGenesisHash(genesis);
            context.setBlockHash(blockHash);
            context.setNonce(nonce);
            context.setEra(era);
            context.setTip(tip);
            return context;
        }
    }

    public static final class AutoBuilder {

        private final Address sender;

        public AutoBuilder(Address sender) {
            this.sender = sender;
        }

        //TODO:inspect RuntimeVersionJson
        public CompletableFuture<Builder> fetch(PolkadotApi api) {
            CompletableFuture<AccountInfo> accountInfo = AccountRequests.balanceOf(sender).execute(api);
            CompletableFuture<Hash256> genesis = api.execute(
                    StandardCommands.getInstance().getBlockHash(0)
            );
            CompletableFuture<RuntimeVersionJson> runtimeVersion = api.execute(
                    StandardCommands.getInstance().getRuntimeVersion()
            );

            return CompletableFuture.allOf(accountInfo, genesis, runtimeVersion)
                    .thenApply((ignore) ->
                            new Builder()
                                    .runtime(runtimeVersion.join())
                                    .nonce(accountInfo.join().getNonce())
                                    .genesis(genesis.join())
                    );
        }
    }

    @Override
    public String toString() {
        return "ExtrinsicContext{" +
                "txVersion=" + transactionVersion +
                ", runtimeVersion=" + specVersion +
                ", genesis=" + genesisHash +
                ", eraBlockHash=" + blockHash +
                ", nonce=" + nonce +
                ", era=" + era +
                ", tip=" + tip +
                '}';
    }
}
