package io.emeraldpay.polkaj.scaletypes.v14;

import lombok.Data;
import java.util.List;

@Data
public class MetadataContainer {
    private Integer magicNumber;
    private Metadata metadata;

    @Data
    public static class Metadata {
        private MetadataV14 v14;
    }

    @Data
    public static class MetadataV14 {
        private Lookup lookup;
        private List<Pallet> pallets;
        private Extrinsic extrinsic;
    }

    @Data
    public static class Lookup {
        private List<Type> types;
    }

    @Data
    public static class Type {
        private List<String> path;
        private List<Param> params;
        private Def def;
    }

    @Data
    public static class Param {
        private String name;
        private Integer type;
    }

    @Data
    public static class Def {
        private Variant variant;
    }

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

    public static enum Modifier {
        OPTIONAL, DEFAULT, REQUIRED
    }

    public static enum Hasher {
        BLAKE2_128, BLAKE2_256, BLAKE2_256_CONCAT, TWOX_128, TWOX_256, TWOX_64_CONCAT, IDENTITY
    }


    public static enum TypeId {
        PLAIN(String.class),
        MAP(MapDefinition.class);

     //   DOUBLEMAP(DoubleMapDefinition.class);

        private final Class<?> clazz;

        TypeId(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Class<?> getClazz() {
            return clazz;
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