package com.tpapad.rabbitzip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

public class ConfigurableGZIPOutputStream extends GZIPOutputStream {

    public ConfigurableGZIPOutputStream(final OutputStream out, final int size) throws IOException {
        super(out, size);
    }

    public ConfigurableGZIPOutputStream withBestCompression() {
        super.def.setLevel(Deflater.BEST_COMPRESSION);
        return this;
    }

    public ConfigurableGZIPOutputStream withNoCompression() {
        super.def.setLevel(Deflater.NO_COMPRESSION);
        return this;
    }
}
