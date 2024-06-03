package io.emeraldpay.polkaj.scaletypes.v14;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MetadataContainer {
    private Integer magicNumber;
    private Integer version;
    private Metadata metadata;

//    @Data
//    public static class Metadata {
//        private MetadataV14 V14;
//    }

    @Data
    public static class Metadata {
        private Lookup lookup;
        private List<Pallet> pallets;
        private Extrinsic extrinsic;
        private Integer type;
    }

    @Data
    public static class Lookup {
        private List<TypeFields> types;
    }

    @Data
    public static class TypeFields {
        private Integer id;
        private Type type;
    }

    @Data
    public static class Type {
        private List<String> path;
        private List<Param> params;
        private Def def;
        private List<String> docs;
    }

    @Data
    public static class Param {
        private String name;
        private Integer type;
    }

    @Data
    public static class Def {
        private CustomType<?> type;
    }


    @Data
    public static class Field {
        private String name;
        private Integer type;
        private String typeName;
        private List<String> docs;
    }

    @Data
    public static class Extrinsic {
        private Integer type;
        private Integer version;
        private List<SignedExtension> signedExtensions;
    }

    @Data
    public static class SignedExtension {
        private String identifier;
        private Integer type;
        private Integer additionalSigned;
    }

    @Data
    public static class Pallet {
        private String name;
        private Storage storage;
        private Calls calls;
        private Events events;
        private List<Constant> constants;
        private Errors errors;
        private Integer index;
    }

    @Data
    public static class Storage {
        private String prefix;
        private List<StorageItem> items;
    }

    @Data
    public static class StorageItem {
        private String name;
        private Modifier modifier;
        private CustomType<?> type;
        private byte[] fallback;
        private List<String> docs;
    }


    public abstract static class CustomType<T> {
        private final T value;

        public CustomType(T value) {
            this.value = value;
        }

        public abstract TypeId getId();

        public T get() {
            return value;
        }

        @SuppressWarnings("unchecked")
        public <X> CustomType<X> cast(Class<X> clazz) {
            if (clazz.isAssignableFrom(getId().getClazz())) {
                return (CustomType<X>) this;
            }
            throw new ClassCastException("Cannot cast " + getId().getClazz() + " to " + clazz);
        }
    }


    public static enum Modifier {
        OPTIONAL, DEFAULT, REQUIRED
    }

    public static enum Hasher {
        BLAKE2_128, BLAKE2_256, BLAKE2_256_CONCAT, TWOX_128, TWOX_256, TWOX_64_CONCAT, IDENTITY
    }


    public static enum TypeId {
        PLAIN(String.class),
        MAP(MapDefinition.class),

        //   DOUBLEMAP(DoubleMapDefinition.class);

        COMPOSITE(Composite.class),
        VARIANT(Variant.class),
        ARRAY(Array.class),
        SEQUENCE(Sequence.class),
        TUPLE(IntegerList.class),

        //TODO: Si1TypeDefPrimitive
        PRIMITIVE(String.class),
        COMPACT(Compact.class),
        BIT_SEQUENCE(BitSequence.class),
        HISTORIC_META(String.class);

        private final Class<?> clazz;

        TypeId(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Class<?> getClazz() {
            return clazz;
        }
    }

    //BitSequence
    @Data
    public static class BitSequence{
        /**
         *    readonly bitStoreType
         *     readonly bitOrderType
         */
        private Integer bitStoreType;
        private Integer bitOrderType;
    }

    //BitSequenceType
    public static class BitSequenceType extends CustomType<BitSequence> {
        public BitSequenceType(BitSequence value) {
            super(value);
        }

        @Override
        public TypeId getId() {
            return TypeId.BIT_SEQUENCE;
        }
    }


    @Data
    public static class IntegerList extends ArrayList<Integer> {
    }


    //TupleType
    public static class TupleType extends CustomType<IntegerList> {
        public TupleType(IntegerList value) {
            super(value);
        }

        @Override
        public TypeId getId() {
            return TypeId.TUPLE;
        }
    }


    @Data
    public static class Sequence {
        private Integer type;
    }

    private static class SequenceType extends CustomType<Sequence> {
        public SequenceType(Sequence value) {
            super(value);
        }

        @Override
        public TypeId getId() {
            return TypeId.SEQUENCE;
        }
    }

    //Compact type
    @Data
    public static class Compact {
        private Integer type;
    }

    private static class CompactType extends CustomType<Compact> {
        public CompactType(Compact value) {
            super(value);
        }

        @Override
        public TypeId getId() {
            return TypeId.COMPACT;
        }
    }


    public static class PlainType extends CustomType<String> {
        public PlainType(String value) {
            super(value);
        }

        @Override
        public TypeId getId() {
            return TypeId.PLAIN;
        }
    }

    @Data
    public static class MapDefinition {
        private Hasher hasher;
        private String key;
        private String type;
    }

    public static class MapType extends CustomType<MapDefinition> {
        public MapType(MapDefinition value) {
            super(value);
        }

        @Override
        public TypeId getId() {
            return TypeId.MAP;
        }
    }

    // Primitive type
    public static class PrimitiveType extends CustomType<String> {
        public PrimitiveType(String value) {
            super(value);
        }

        @Override
        public TypeId getId() {
            return TypeId.PRIMITIVE;
        }
    }

    //HistoricMeta type
    public static class HistoricMeta extends CustomType<String> {
        public HistoricMeta(String value) {
            super(value);
        }

        @Override
        public TypeId getId() {
            return TypeId.HISTORIC_META;
        }
    }

    //Variant type
    @Data
    public static class Variant {
        private List<CallVariant> variants;
    }

    @Data
    public static class CallVariant {
        private String name;
        private List<Field> fields;
        private Integer index;
        private List<String> docs;
    }
    public static class VariantType extends CustomType<Variant> {
        public VariantType(Variant value) {
            super(value);
        }

        @Override
        public TypeId getId() {
            return TypeId.VARIANT;
        }
    }

    //Composite type
    @Data
    public static class Composite {
        private List<Field> fields;
    }

    public static class CompositeType extends CustomType<Composite> {
        public CompositeType(Composite value) {
            super(value);
        }

        @Override
        public TypeId getId() {
            return TypeId.COMPOSITE;
        }
    }


    // class for            "array": {
    //              "len": 16,
    //              "type": 2
    //            }
    @Data
    public static class Array {
        private Long len;
        private Integer type;
    }

    //extend CustomType for Array
    public static class ArrayType extends CustomType<Array> {
        public ArrayType(Array value) {
            super(value);
        }

        @Override
        public TypeId getId() {
            return TypeId.ARRAY;
        }
    }

//    @Data
//    public static class DoubleMapDefinition {
//        private Hasher firstHasher;
//        private String firstKey;
//        private Hasher secondHasher;
//        private String secondKey;
//        private String type;
//    }
//
//    public static class DoubleMapType extends CustomType<DoubleMapDefinition> {
//        public DoubleMapType(DoubleMapDefinition value) {
//            super(value);
//        }
//
//        @Override
//        public TypeId getId() {
//            return TypeId.DOUBLEMAP;
//        }
//    }

    @Data
    public static class Calls {
        private Integer type;
    }

    @Data
    public static class Events {
        private Integer type;
    }

    @Data
    public static class Constant {
        private String name;
        private Integer type;
        private byte[] value;
        private List<String> docs;
    }

    @Data
    public static class Errors {
        private Integer type;
    }


}