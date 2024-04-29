package io.emeraldpay.polkaj.scaletypes.v14;


import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;
import io.emeraldpay.polkaj.scale.reader.EnumReader;
import io.emeraldpay.polkaj.scale.reader.ListReader;
import io.emeraldpay.polkaj.scale.reader.UnionReader;

public class MetadataReaderv14 implements ScaleReader<MetadataContainer> {

    // ListReader for Lookup
    public static final ListReader<MetadataContainer.Lookup> LOOKUP_LIST_READER = new ListReader<>(new LookupReader());

    // ListReader for Pallets
    public static final ListReader<MetadataContainer.Pallet> MODULE_LIST_READER = new ListReader<>(new PalletReader());

    // ListReader for Extrinsic
    public static final ListReader<MetadataContainer.Extrinsic> EXTRINSIC_LIST_READER = new ListReader<>(new ExtrinsicReader());

    public static final EnumReader<MetadataContainer.Hasher> HASHER_ENUM_READER = new EnumReader<>(MetadataContainer.Hasher.values());

    public static final ListReader<String> STRING_LIST_READER = new ListReader<>(ScaleCodecReader.STRING);


    @Override
    public MetadataContainer read(ScaleCodecReader rdr) {
        MetadataContainer result = new MetadataContainer();
        result.setMagicNumber(ScaleCodecReader.INT32.read(rdr));

        MetadataContainer.Metadata metadata = new MetadataContainer.Metadata();

        MetadataContainer.MetadataV14 v14 = new MetadataContainer.MetadataV14();
        metadata.setV14(v14); // Adding a v14 class

        v14.setLookup(new LookupReader().read(rdr));         // Using the LookupReader
        v14.setPallets(MODULE_LIST_READER.read(rdr));       // Assuming pallets remain a list
        v14.setExtrinsic(new ExtrinsicReader().read(rdr));  // Using the ExtrinsicReader

        return result;
    }

    static class LookupReader implements ScaleReader<MetadataContainer.Lookup> {

        @Override
        public MetadataContainer.Lookup read(ScaleCodecReader rdr) {
            MetadataContainer.Lookup result = new MetadataContainer.Lookup();
            result.setTypes(new ListReader<>(new TypesReader()).read(rdr));
            return result;
        }

    }

    private static class TypesReader implements ScaleReader<MetadataContainer.Types>{
        public MetadataContainer.Types read(ScaleCodecReader rdr) {
            MetadataContainer.Types result = new MetadataContainer.Types();
            result.setId(rdr.readUByte());
            result.setType(new TypeReader().read(rdr));
            return result;
        }
    }

    static class PalletReader implements ScaleReader<MetadataContainer.Pallet> {
        public static final ListReader<MetadataContainer.CallVariant> CALL_VARIANT_LIST_READER = new ListReader<>(new CallVariantReader());

        @Override
        public MetadataContainer.Pallet read(ScaleCodecReader rdr) {
            MetadataContainer.Pallet result = new MetadataContainer.Pallet();
            result.setName(rdr.readString());
            result.setStorage(new StorageReader().read(rdr));
            result.setCalls(new CallsReader().read(rdr));
            return result;
        }
    }

    private static class StorageReader implements ScaleReader<MetadataContainer.Storage> {
        public static final ListReader<MetadataContainer.StorageItem> ENTRY_LIST_READER = new ListReader<>(new StorageItemReader());

        @Override
        public MetadataContainer.Storage read(ScaleCodecReader rdr) {
            MetadataContainer.Storage result = new MetadataContainer.Storage();
            result.setPrefix(rdr.readString());
            result.setItems(ENTRY_LIST_READER.read(rdr));
            return result;
        }

    }

    private static class StorageItemReader implements ScaleReader<MetadataContainer.StorageItem> {
        public static final EnumReader<MetadataContainer.Modifier> MODIFIER_ENUM_READER = new EnumReader<>(MetadataContainer.Modifier.values());
        public static final TypeReader TYPE_READER = new TypeReader();

        @Override
        public MetadataContainer.StorageItem read(ScaleCodecReader rdr) {
            MetadataContainer.StorageItem result = new MetadataContainer.StorageItem();
            result.setName(rdr.readString());
            result.setModifier(MODIFIER_ENUM_READER.read(rdr));
            result.setType(rdr.read(TYPE_READER));
            result.setFallback(rdr.readByteArray());
            result.setDocs(STRING_LIST_READER.read(rdr));
            return result;
        }
    }

    private static class CallsReader implements ScaleReader<MetadataContainer.Calls> {

        @Override
        public MetadataContainer.Calls read(ScaleCodecReader rdr) {
            MetadataContainer.Calls calls = new MetadataContainer.Calls();
            calls.setType(rdr.readUByte());
            return calls;
        }
    }

    private static class CallVariantReader implements ScaleReader<MetadataContainer.CallVariant> {
        public MetadataContainer.CallVariant read(ScaleCodecReader rdr) {
            MetadataContainer.CallVariant result = new MetadataContainer.CallVariant();
            result.setName(rdr.readString());
            result.setIndex(rdr.readUByte());
            return result;
        }
    }

    // ExtrinsicReader
    static class ExtrinsicReader implements ScaleReader<MetadataContainer.Extrinsic> {
        public static final ListReader<MetadataContainer.SignedExtension> SIGNED_EXTENSION_LIST_READER = new ListReader<>(new SignedExtension());

        @Override
        public MetadataContainer.Extrinsic read(ScaleCodecReader rdr) {
            MetadataContainer.Extrinsic result = new MetadataContainer.Extrinsic();
            result.setType(rdr.readUByte());
            result.setVersion(rdr.readUByte());
            result.setSignedExtensions(SIGNED_EXTENSION_LIST_READER.read(rdr));
            return result;
        }

    }


    private static class SignedExtension implements ScaleReader<MetadataContainer.SignedExtension> {
        @Override
        public MetadataContainer.SignedExtension read(ScaleCodecReader rdr) {
            MetadataContainer.SignedExtension result = new MetadataContainer.SignedExtension();
            result.setIdentifier(rdr.readString());
            result.setType(rdr.readUByte());
            result.setAdditionalSigned(rdr.readUByte());
            return result;
        }
    }

    static class TypeReader implements ScaleReader<MetadataContainer.CustomType<?>> {

        @SuppressWarnings("unchecked")
        private static final UnionReader<MetadataContainer.CustomType<?>> TYPE_UNION_READER = new UnionReader<>(
                new TypePlainReader(),
                new TypeMapReader(),
                new TypeDoubleMapReader()
        );

        @Override
        public MetadataContainer.CustomType<?> read(ScaleCodecReader rdr) {
            return TYPE_UNION_READER.read(rdr).getValue();
        }
    }

    static class TypePlainReader implements ScaleReader<MetadataContainer.PlainType> {
        @Override
        public MetadataContainer.PlainType read(ScaleCodecReader rdr) {
            return new MetadataContainer.PlainType(rdr.readString());
        }
    }

    static class TypeMapReader implements ScaleReader<MetadataContainer.MapType> {

        @Override
        public MetadataContainer.MapType read(ScaleCodecReader rdr) {
            MetadataContainer.MapDefinition definition = new MetadataContainer.MapDefinition();
            definition.setHasher(HASHER_ENUM_READER.read(rdr));
            definition.setKey(rdr.readString());
            definition.setType(rdr.readString());
            definition.setIterable(rdr.readBoolean());
            return new MetadataContainer.MapType(definition);
        }
    }

    static class TypeDoubleMapReader implements ScaleReader<MetadataContainer.DoubleMapType> {

        @Override
        public MetadataContainer.DoubleMapType read(ScaleCodecReader rdr) {
            MetadataContainer.DoubleMapDefinition definition = new MetadataContainer.DoubleMapDefinition();
            definition.setFirstHasher(HASHER_ENUM_READER.read(rdr));
            definition.setFirstKey(rdr.readString());
            definition.setSecondKey(rdr.readString());
            definition.setType(rdr.readString());
            definition.setSecondHasher(HASHER_ENUM_READER.read(rdr));
            return new MetadataContainer.DoubleMapType(definition);
        }
    }

}
