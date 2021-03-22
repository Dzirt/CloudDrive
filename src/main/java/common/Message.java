package common;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Message implements Serializable {
    private String name;
    private byte[] data;

    public Message(String name, byte[] data) {
        this.name = name;
        this.data = new byte[data.length];
        this.data = data;
    }

    public Message(String name, String data) {
        this.name = name;
        this.data = data.getBytes(StandardCharsets.UTF_8);
    }
    public Message(String name, long data) {
        this.name = name;
        this.data = longToBytes(data);
    }
    public Message(String name) {
        this.name = name;
    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }
}
