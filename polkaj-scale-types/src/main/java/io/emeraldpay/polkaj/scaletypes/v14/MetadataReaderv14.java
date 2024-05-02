package io.emeraldpay.polkaj.scaletypes.v14;


import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;
import io.emeraldpay.polkaj.scale.reader.EnumReader;
import io.emeraldpay.polkaj.scale.reader.ListReader;
import io.emeraldpay.polkaj.scale.reader.UnionReader;

public class MetadataReaderv14 implements ScaleReader<MetadataContainer> {


    public static final EnumReader<MetadataContainer.Hasher> HASHER_ENUM_READER = new EnumReader<>(MetadataContainer.Hasher.values());

    public static final ListReader<String> STRING_LIST_READER = new ListReader<>(ScaleCodecReader.STRING);


    @Override
    public MetadataContainer read(ScaleCodecReader rdr) {

        MetadataContainer result = new MetadataContainer();
        result.setMagicNumber(ScaleCodecReader.INT32.read(rdr));

        MetadataContainer.Metadata metadata = new MetadataContainer.Metadata();
        metadata.setV14(new MetadataScaleReader().read(rdr));

        return result;
    }

    static class LookupReader implements ScaleReader<MetadataContainer.Lookup> {

        @Override
        public MetadataContainer.Lookup read(ScaleCodecReader rdr) {
            MetadataContainer.Lookup result = new MetadataContainer.Lookup();
            result.setTypes(new ListReader<>(new LookupTypesReader()).read(rdr));
            return result;
        }

    }

    public static class LookupTypesReader implements ScaleReader<MetadataContainer.Type> {
        public static final ListReader<String> STRING_LIST_READER = new ListReader<>(ScaleCodecReader.STRING);
        public static final ListReader<MetadataContainer.Param> PARAM_LIST_READER = new ListReader<>(new ParamReader());
        public static final DefReader DEF_READER = new DefReader();

        @Override
        public MetadataContainer.Type read(ScaleCodecReader rdr) {
            MetadataContainer.Type result = new MetadataContainer.Type();
            result.setPath(STRING_LIST_READER.read(rdr));
            result.setParams(PARAM_LIST_READER.read(rdr));
            result.setDef(DEF_READER.read(rdr));
            return result;
        }

        public static class DefReader implements ScaleReader<MetadataContainer.Def> {
            public static final VariantReader VARIANT_READER = new VariantReader();

            @Override
            public MetadataContainer.Def read(ScaleCodecReader rdr) {
                MetadataContainer.Def result = new MetadataContainer.Def();
                result.setVariant(VARIANT_READER.read(rdr));
                return result;
            }

            public static class VariantReader implements ScaleReader<MetadataContainer.Variant> {
                public static final ListReader<MetadataContainer.CallVariant> CALL_VARIANT_LIST_READER = new ListReader<>(new CallVariantReader());

                @Override
                public MetadataContainer.Variant read(ScaleCodecReader rdr) {
                    MetadataContainer.Variant result = new MetadataContainer.Variant();
                    result.setVariants(CALL_VARIANT_LIST_READER.read(rdr));
                    return result;
                }
            }
        }

        public static class ParamReader implements ScaleReader<MetadataContainer.Param> {
            @Override
            public MetadataContainer.Param read(ScaleCodecReader rdr) {
                MetadataContainer.Param result = new MetadataContainer.Param();
                result.setName(rdr.readString());
                result.setType(rdr.readUByte());
                return result;
            }
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

    static class StorageReader implements ScaleReader<MetadataContainer.Storage> {
        public static final ListReader<MetadataContainer.StorageItem> ENTRY_LIST_READER = new ListReader<>(new StorageItemReader());

        @Override
        public MetadataContainer.Storage read(ScaleCodecReader rdr) {
            MetadataContainer.Storage result = new MetadataContainer.Storage();
            result.setPrefix(rdr.readString());
            result.setItems(ENTRY_LIST_READER.read(rdr));
            return result;
        }

    }

    static class StorageItemReader implements ScaleReader<MetadataContainer.StorageItem> {
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

    static class CallsReader implements ScaleReader<MetadataContainer.Calls> {

        @Override
        public MetadataContainer.Calls read(ScaleCodecReader rdr) {
            MetadataContainer.Calls calls = new MetadataContainer.Calls();
            calls.setType(rdr.readUByte());
            return calls;
        }
    }

    static class CallVariantReader implements ScaleReader<MetadataContainer.CallVariant> {
        public MetadataContainer.CallVariant read(ScaleCodecReader rdr) {
            MetadataContainer.CallVariant result = new MetadataContainer.CallVariant();
            result.setName(rdr.readString());
            result.setIndex(rdr.readUByte());
            result.setFields(new ListReader<>(new FieldReader()).read(rdr));
            result.setDocs(STRING_LIST_READER.read(rdr));
            return result;
        }

        static class FieldReader implements ScaleReader<MetadataContainer.Field> {
            @Override
            public MetadataContainer.Field read(ScaleCodecReader rdr) {
                MetadataContainer.Field result = new MetadataContainer.Field();
                result.setName(rdr.readString());
                result.setType(rdr.readUByte());
                result.setTypeName(rdr.readString());
                result.setDocs(STRING_LIST_READER.read(rdr));
                return result;
            }
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


    static class SignedExtension implements ScaleReader<MetadataContainer.SignedExtension> {
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
        public static final UnionReader<MetadataContainer.CustomType<?>> TYPE_UNION_READER = new UnionReader<>(new TypePlainReader(), new TypeMapReader());

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
            //    definition.setIterable(rdr.readBoolean());
            return new MetadataContainer.MapType(definition);
        }
    }

//    static class TypeDoubleMapReader implements ScaleReader<MetadataContainer.DoubleMapType> {
//
//        @Override
//        public MetadataContainer.DoubleMapType read(ScaleCodecReader rdr) {
//            MetadataContainer.DoubleMapDefinition definition = new MetadataContainer.DoubleMapDefinition();
//            definition.setFirstHasher(HASHER_ENUM_READER.read(rdr));
//            definition.setFirstKey(rdr.readString());
//            definition.setSecondKey(rdr.readString());
//            definition.setType(rdr.readString());
//            definition.setSecondHasher(HASHER_ENUM_READER.read(rdr));
//            return new MetadataContainer.DoubleMapType(definition);
//        }
//    }

    class MetadataScaleReader implements ScaleReader<MetadataContainer.MetadataV14> {

        // ListReader for Pallets
        public static final ListReader<MetadataContainer.Pallet> MODULE_LIST_READER = new ListReader<>(new PalletReader());

        @Override
        public MetadataContainer.MetadataV14 read(ScaleCodecReader rdr) {
            MetadataContainer.MetadataV14 v14 = new MetadataContainer.MetadataV14();
            v14.setLookup(new LookupReader().read(rdr));         // Using the LookupReader
            v14.setPallets(MODULE_LIST_READER.read(rdr));       // Assuming pallets remain a list
            v14.setExtrinsic(new ExtrinsicReader().read(rdr));  // Using the ExtrinsicReader
            return v14;
        }
    }
}
