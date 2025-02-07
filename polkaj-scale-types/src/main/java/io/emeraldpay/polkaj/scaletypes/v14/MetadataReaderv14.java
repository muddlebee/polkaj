package io.emeraldpay.polkaj.scaletypes.v14;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;
import io.emeraldpay.polkaj.scale.reader.EnumReader;
import io.emeraldpay.polkaj.scale.reader.ListReader;
import io.emeraldpay.polkaj.scale.reader.UnionReader;

import java.util.List;

public class MetadataReaderv14 implements ScaleReader<MetadataContainer> {

    private static final Logger logger = LoggerFactory.getLogger(MetadataReaderv14.class);

    public static final EnumReader<MetadataContainer.Hasher> HASHER_ENUM_READER = new EnumReader<>(MetadataContainer.Hasher.values());
    public static final ListReader<String> STRING_LIST_READER = new ListReader<>(ScaleCodecReader.STRING);
    public static final ScaleReader<Integer> INT32_READER = ScaleCodecReader.INT32;


    @Override
    public MetadataContainer read(ScaleCodecReader rdr) {
        try {
            MetadataContainer result = new MetadataContainer();
            result.setMagicNumber(INT32_READER.read(rdr));
            result.setVersion(rdr.readUByte());
            result.setMetadata(new MetadataScaleReader().read(rdr));
            return result;
        } catch (Exception e) {
            System.out.println("Error reading metadata: " + e);
            return null;
        }
    }

//    //class MetadataV14
//    static class MetadataV14 implements ScaleReader<MetadataContainer.Metadata> {
//        @Override
//        public MetadataContainer.Metadata read(ScaleCodecReader rdr) {
//            MetadataContainer.Metadata metadata = new MetadataContainer.Metadata();
//            metadata.setV14(new MetadataScaleReader().read(rdr));
//            return metadata;
//        }
//    }

    static class MetadataScaleReader implements ScaleReader<MetadataContainer.Metadata> {

        // ListReader for Pallets
        public static final ListReader<MetadataContainer.Pallet> MODULE_LIST_READER = new ListReader<>(new PalletReader());

        @Override
        public MetadataContainer.Metadata read(ScaleCodecReader rdr) {
            MetadataContainer.Metadata v14 = new MetadataContainer.Metadata();
            v14.setLookup(new LookupReader().read(rdr));         // Using the LookupReader
            v14.setPallets(MODULE_LIST_READER.read(rdr));       // Assuming pallets remain a list
            v14.setExtrinsic(new ExtrinsicReader().read(rdr));  // Using the ExtrinsicReader
            v14.setType(rdr.readCompactInt());
            return v14;
        }
    }

    public static class LookupReader implements ScaleReader<MetadataContainer.Lookup> {

        // List Reader for TypeFields
        public static final ListReader<MetadataContainer.TypeFields> TYPE_FIELDS_LIST_READER = new ListReader<>(new TypeFieldsReader());

        @Override
        public MetadataContainer.Lookup read(ScaleCodecReader rdr) {
                MetadataContainer.Lookup result = new MetadataContainer.Lookup();
                result.setTypes(TYPE_FIELDS_LIST_READER.read(rdr));
                return result;
        }
    }

    static class TypeFieldsReader implements ScaleReader<MetadataContainer.TypeFields> {

        @Override
        public MetadataContainer.TypeFields read(ScaleCodecReader rdr) {
            MetadataContainer.TypeFields result = new MetadataContainer.TypeFields();
            result.setId(rdr.readCompactInt());
            result.setType(new LookupTypesReader().read(rdr));
            return result;
        }
    }

    static class LookupTypesReader implements ScaleReader<MetadataContainer.Type> {
        private static final ListReader<String> STRING_LIST_READER = new ListReader<>(ScaleCodecReader.STRING);
        private static final ListReader<MetadataContainer.Param> PARAM_LIST_READER = new ListReader<>(new ParamReader());
        private static final DefReader DEF_READER = new DefReader();

        @Override
        public MetadataContainer.Type read(ScaleCodecReader rdr) {

            MetadataContainer.Type result = new MetadataContainer.Type();
            result.setPath(STRING_LIST_READER.read(rdr));
            result.setParams(PARAM_LIST_READER.read(rdr));
            result.setDef(DEF_READER.read(rdr));
            result.setDocs(STRING_LIST_READER.read(rdr));
            return result;
        }

        public static class DefReader implements ScaleReader<MetadataContainer.Def> {

            public static final DefTypeReader DEF_TYPE_READER = new DefTypeReader();

            @Override
            public MetadataContainer.Def read(ScaleCodecReader rdr) {
                MetadataContainer.Def result = new MetadataContainer.Def();
                result.setType(DEF_TYPE_READER.read(rdr));
                return result;
            }

        }

        public static class ParamReader implements ScaleReader<MetadataContainer.Param> {
            @Override
            public MetadataContainer.Param read(ScaleCodecReader rdr) {
                MetadataContainer.Param result = new MetadataContainer.Param();
                result.setName(rdr.readString());
                rdr.readOptional(ScaleCodecReader.COMPACT_UINT).ifPresent(result::setType);
                return result;
            }
        }
    }

    public static class ListPalletReader implements ScaleReader<List<MetadataContainer.Pallet>> {
        @Override
        public List<MetadataContainer.Pallet> read(ScaleCodecReader rdr) {
            return new ListReader<>(new PalletReader()).read(rdr);
        }
    }

    public static class PalletReader implements ScaleReader<MetadataContainer.Pallet> {

        @Override
        public MetadataContainer.Pallet read(ScaleCodecReader rdr) {
                MetadataContainer.Pallet result = new MetadataContainer.Pallet();
                result.setName(rdr.readString());
                rdr.readOptional(new StorageReader()).ifPresent(result::setStorage);
                rdr.readOptional(new CallsReader()).ifPresent(result::setCalls);
                rdr.readOptional(new EventReader()).ifPresent(result::setEvents);
                result.setConstants(new ListReader<>(new ConstantReader()).read(rdr));
                rdr.readOptional(new ErrorsReader()).ifPresent(result::setErrors);
                result.setIndex(rdr.readUByte());
                return result;
        }
    }

    static class ErrorsReader implements ScaleReader<MetadataContainer.Errors> {
        @Override
        public MetadataContainer.Errors read(ScaleCodecReader rdr) {
            MetadataContainer.Errors result = new MetadataContainer.Errors();
            result.setType(rdr.readCompactInt());
            return result;
        }
    }

    static class ConstantReader implements ScaleReader<MetadataContainer.Constant> {
        @Override
        public MetadataContainer.Constant read(ScaleCodecReader rdr) {
            MetadataContainer.Constant result = new MetadataContainer.Constant();
            result.setName(rdr.readString());
            result.setType(rdr.readCompactInt());
            result.setValue(rdr.readByteArray());
            result.setDocs(STRING_LIST_READER.read(rdr));
            return result;
        }
    }

    static class EventReader implements ScaleReader<MetadataContainer.Events> {
        @Override
        public MetadataContainer.Events read(ScaleCodecReader rdr) {
            MetadataContainer.Events result = new MetadataContainer.Events();
            result.setType(rdr.readCompactInt());
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
            calls.setType(rdr.readCompactInt());
            return calls;
        }
    }

    static class CallVariantReader implements ScaleReader<MetadataContainer.CallVariant> {
        public MetadataContainer.CallVariant read(ScaleCodecReader rdr) {
            MetadataContainer.CallVariant result = new MetadataContainer.CallVariant();
            result.setName(rdr.readString());
            result.setFields(new ListReader<>(new FieldReader()).read(rdr));
            //TODO " index: u8;" in docs
            result.setIndex(rdr.readUByte());
            result.setDocs(STRING_LIST_READER.read(rdr));
            return result;
        }
    }

    public static class FieldReader implements ScaleReader<MetadataContainer.Field> {

        @Override
        public MetadataContainer.Field read(ScaleCodecReader rdr) {
            MetadataContainer.Field result = new MetadataContainer.Field();
            rdr.readOptional(ScaleCodecReader.STRING).ifPresent(result::setName);
            result.setType(rdr.readCompactInt());
            rdr.readOptional(ScaleCodecReader.STRING).ifPresent(result::setTypeName);
            result.setDocs(STRING_LIST_READER.read(rdr));
            return result;
        }
    }

    public static class ExtrinsicReader implements ScaleReader<MetadataContainer.Extrinsic> {
        public static final ListReader<MetadataContainer.SignedExtension> SIGNED_EXTENSION_LIST_READER = new ListReader<>(new SignedExtension());

        @Override
        public MetadataContainer.Extrinsic read(ScaleCodecReader rdr) {
            MetadataContainer.Extrinsic result = new MetadataContainer.Extrinsic();
            result.setType(rdr.readCompactInt());
            result.setVersion(rdr.readUByte());
            result.setSignedExtensions(SIGNED_EXTENSION_LIST_READER.read(rdr));
            return result;
        }
    }


    static public class SignedExtension implements ScaleReader<MetadataContainer.SignedExtension> {
        @Override
        public MetadataContainer.SignedExtension read(ScaleCodecReader rdr) {
            MetadataContainer.SignedExtension result = new MetadataContainer.SignedExtension();
            result.setIdentifier(rdr.readString());
            result.setType(rdr.readCompactInt());
            result.setAdditionalSigned(rdr.readCompactInt());
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
            return new MetadataContainer.PlainType(rdr.readCompactInt());
        }
    }

    static class TypeMapReader implements ScaleReader<MetadataContainer.MapType> {

        @Override
        public MetadataContainer.MapType read(ScaleCodecReader rdr) {
            MetadataContainer.MapDefinition definition = new MetadataContainer.MapDefinition();
            ListReader<MetadataContainer.Hasher> HASHER_LIST_READER = new ListReader<>(HASHER_ENUM_READER);
            definition.setHashers(HASHER_LIST_READER.read(rdr));
            definition.setKey(rdr.readCompactInt());
            definition.setValue(rdr.readCompactInt());
            return new MetadataContainer.MapType(definition);
        }
    }

    static class DefTypeReader implements ScaleReader<MetadataContainer.CustomType<?>> {

        @SuppressWarnings("unchecked")
        public static final UnionReader<MetadataContainer.CustomType<?>> DEF_UNION_READER = new UnionReader<>(
                new CompositeTypeReader(),
                new VariantTypeReader(),
                new SequenceReader(),
                new ArrayReader(),
                new TupleReader(),
                new PrimitiveTypeReader(),
                new CompactReader(),
                new BitSequenceReader(),
                new HistoricMetaReader()
        );

        @Override
        public MetadataContainer.CustomType<?> read(ScaleCodecReader rdr) {
            return DEF_UNION_READER.read(rdr).getValue();
        }

        static class PrimitiveTypeReader implements ScaleReader<MetadataContainer.PrimitiveType> {
            @Override
            public MetadataContainer.PrimitiveType read(ScaleCodecReader rdr) {
                return new MetadataContainer.PrimitiveType(rdr.readUByte());
            }
        }

        static class CompositeTypeReader implements ScaleReader<MetadataContainer.CompositeType> {

            @Override
            public MetadataContainer.CompositeType read(ScaleCodecReader rdr) {
                return new MetadataContainer.CompositeType(new CompositeReader().read(rdr));
            }

            class CompositeReader implements ScaleReader<MetadataContainer.Composite> {

                @Override
                public MetadataContainer.Composite read(ScaleCodecReader rdr) {
                    MetadataContainer.Composite result = new MetadataContainer.Composite();
                    result.setFields(new ListReader<>(new FieldReader()).read(rdr));
                    return result;
                }
            }
        }

        static class VariantTypeReader implements ScaleReader<MetadataContainer.VariantType> {

            @Override
            public MetadataContainer.VariantType read(ScaleCodecReader rdr) {
                return new MetadataContainer.VariantType(new VariantReader().read(rdr));
            }

            class VariantReader implements ScaleReader<MetadataContainer.Variant> {

                @Override
                public MetadataContainer.Variant read(ScaleCodecReader rdr) {
                    MetadataContainer.Variant result = new MetadataContainer.Variant();
                    result.setVariants(new ListReader<>(new CallVariantReader()).read(rdr));
                    return result;
                }
            }
        }

        static class SequenceReader implements ScaleReader<MetadataContainer.SequenceType> {
            @Override
            public MetadataContainer.SequenceType read(ScaleCodecReader rdr) {
                MetadataContainer.Sequence sequence = new MetadataContainer.Sequence();
                sequence.setType(rdr.readCompactInt());
                MetadataContainer.SequenceType result = new MetadataContainer.SequenceType(sequence);
                return result;
            }
        }

        //ArrayReader
        static class ArrayReader implements ScaleReader<MetadataContainer.ArrayType> {
            @Override
            public MetadataContainer.ArrayType read(ScaleCodecReader rdr) {
                MetadataContainer.Array array = new MetadataContainer.Array();
                //TODO "len: u32;" in docs
                array.setLen(rdr.readUint32());
                array.setType(rdr.readCompactInt());
                MetadataContainer.ArrayType result = new MetadataContainer.ArrayType(array);
                return result;
            }
        }

        static class TupleReader implements ScaleReader<MetadataContainer.TupleType> {

            public static final ListReader<Integer> INT32_LIST_READER = new ListReader<>(ScaleCodecReader.COMPACT_UINT);

            @Override
            public MetadataContainer.TupleType read(ScaleCodecReader rdr) {
                MetadataContainer.IntegerList integerList = new MetadataContainer.IntegerList();
                integerList.addAll(INT32_LIST_READER.read(rdr));
                MetadataContainer.TupleType result = new MetadataContainer.TupleType(integerList);
                return result;
            }
        }

        static class CompactReader implements ScaleReader<MetadataContainer.CompactType> {
            @Override
            public MetadataContainer.CompactType read(ScaleCodecReader rdr) {
                MetadataContainer.Compact compact = new MetadataContainer.Compact();
                compact.setType(rdr.readCompactInt());
                MetadataContainer.CompactType result = new MetadataContainer.CompactType(compact);
                return result;
            }
        }

        static class BitSequenceReader implements ScaleReader<MetadataContainer.BitSequenceType> {
            @Override
            public MetadataContainer.BitSequenceType read(ScaleCodecReader rdr) {
                MetadataContainer.BitSequence bitSequence = new MetadataContainer.BitSequence();
                bitSequence.setBitOrderType(rdr.readCompactInt());
                bitSequence.setBitStoreType(rdr.readCompactInt());
                MetadataContainer.BitSequenceType result = new MetadataContainer.BitSequenceType(bitSequence);
                return result;
            }
        }

        static class HistoricMetaReader implements ScaleReader<MetadataContainer.HistoricMetaType> {
            @Override
            public MetadataContainer.HistoricMetaType read(ScaleCodecReader rdr) {
                MetadataContainer.HistoricMetaType result = new MetadataContainer.HistoricMetaType(rdr.readString());
                return result;
            }
        }
    }

}
