package io.emeraldpay.polkaj.scaletypes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HexFormat;

import io.emeraldpay.polkaj.scale.ScaleCodecWriter;
import io.emeraldpay.polkaj.scale.ScaleWriter;

public class ExtrinsicWriter<CALL extends ExtrinsicCall> implements ScaleWriter<Extrinsic<CALL>> {

    private static final TransactionInfoWriter TX_WRITER = new TransactionInfoWriter();
    private final ScaleWriter<CALL> callScaleWriter;

    public ExtrinsicWriter(ScaleWriter<CALL> callScaleWriter) {
        this.callScaleWriter = callScaleWriter;
    }

    @Override
    public void write(ScaleCodecWriter wrt, Extrinsic<CALL> value) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ScaleCodecWriter internal = new ScaleCodecWriter(buf);
        int type = Extrinsic.TYPE_BIT_SIGNED + (Extrinsic.TYPE_UNMASK_VERSION & 4);

        System.out.println("Type: " + type + " (hex: " + String.format("%02X", type) + ")");

        internal.writeByte(type);
        internal.write(TX_WRITER, value.getTx());
        internal.write(callScaleWriter, value.getCall());

        byte[] extrinsicBytes = buf.toByteArray();
        System.out.println("Full Extrinsic SCALE-encoded: " + HexFormat.of().formatHex(extrinsicBytes));

        wrt.writeAsList(extrinsicBytes);
    }

    static class TransactionInfoWriter implements ScaleWriter<Extrinsic.TransactionInfo> {

        private static final MultiAddressWriter SENDER_WRITER = new MultiAddressWriter();
        private static final EraWriter ERA_WRITER = new EraWriter();

        @Override
        public void write(ScaleCodecWriter wrt, Extrinsic.TransactionInfo value) throws IOException {
            System.out.println("Sender: " + value.getSender());
            byte[] senderBytes = captureWrite(SENDER_WRITER, value.getSender());
            System.out.println("Sender SCALE-encoded: " + HexFormat.of().formatHex(senderBytes));
            wrt.writeByteArray(senderBytes);

            System.out.println("Signature Type: " + value.getSignature().getType());
            System.out.println("Signature Value: " + HexFormat.of().formatHex(value.getSignature().getValue().getBytes()));
            byte[] signatureBytes = captureSignatureWrite(value);
            System.out.println("Signature SCALE-encoded: " + HexFormat.of().formatHex(signatureBytes));
            wrt.writeByteArray(signatureBytes);

            System.out.println("Era: " + value.getEra());
            byte[] eraBytes = captureWrite(ERA_WRITER, value.getEra());
            System.out.println("Era SCALE-encoded: " + HexFormat.of().formatHex(eraBytes));
            wrt.writeByteArray(eraBytes);

            System.out.println("Nonce: " + value.getNonce());
            byte[] nonceBytes = captureWrite(ScaleCodecWriter.COMPACT_BIGINT, BigInteger.valueOf(value.getNonce()));
            System.out.println("Nonce SCALE-encoded: " + HexFormat.of().formatHex(nonceBytes));
            wrt.writeByteArray(nonceBytes);

            System.out.println("Tip: " + value.getTip().getValue());
            byte[] tipBytes = captureWrite(ScaleCodecWriter.COMPACT_BIGINT, value.getTip().getValue());
            System.out.println("Tip SCALE-encoded: " + HexFormat.of().formatHex(tipBytes));
            wrt.writeByteArray(tipBytes);
        }

        private <T> byte[] captureWrite(ScaleWriter<T> writer, T value) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ScaleCodecWriter tempWriter = new ScaleCodecWriter(baos);
            writer.write(tempWriter, value);
            return baos.toByteArray();
        }

        private byte[] captureSignatureWrite(Extrinsic.TransactionInfo value) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ScaleCodecWriter tempWriter = new ScaleCodecWriter(baos);
            Extrinsic.Signature signature = value.getSignature();
            tempWriter.writeByte(signature.getType().getCode());
            tempWriter.writeByteArray(signature.getValue().getBytes());
            return baos.toByteArray();
        }
    }
}