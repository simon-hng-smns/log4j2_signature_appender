import org.apache.logging.log4j.core.layout.ByteBufferDestination;

import java.nio.ByteBuffer;

public class PseudoByteBufferDestination implements ByteBufferDestination {

    byte[] data;

    @Override public ByteBuffer getByteBuffer() {

        return null;
    }

    @Override public ByteBuffer drain(ByteBuffer buf) {

        return null;
    }

    @Override public void writeBytes(ByteBuffer data) {

        this.data = data.array();
    }

    @Override public void writeBytes(byte[] data, int offset, int length) {

        this.data = data;
    }

    public byte[] getData() {

        return data;
    }
}