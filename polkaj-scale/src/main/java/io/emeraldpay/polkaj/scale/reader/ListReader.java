package io.emeraldpay.polkaj.scale.reader;

import io.emeraldpay.polkaj.scale.ScaleReader;
import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ListReader<T> implements ScaleReader<List<T>> {
    private static final Logger logger = LoggerFactory.getLogger(ListReader.class);

    private ScaleReader<T> scaleReader;

    public ListReader(ScaleReader<T> scaleReader) {
        this.scaleReader = scaleReader;
    }

    @Override
    public List<T> read(ScaleCodecReader rdr) {
        try {
            int size = rdr.readCompactInt();
            List<T> result = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                result.add(rdr.read(scaleReader));
            }
            return result;
        } catch (Exception e) {
            System.out.println("Exception in ListReader: " + e);
            throw e;
        }
    }
}
