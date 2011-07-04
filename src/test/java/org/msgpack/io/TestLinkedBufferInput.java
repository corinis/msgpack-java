package org.msgpack.io;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.EOFException;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.value.Value;

import junit.framework.TestCase;

public class TestLinkedBufferInput extends TestCase {
    @Test
    public void testReadByte() throws IOException {
        byte[] src = new byte[8];
        src[0] = (byte)1;
        src[2] = (byte)1;
        src[4] = (byte)1;
        ByteArrayInputStream bin = new ByteArrayInputStream(src);
        DataInputStream b1 = new DataInputStream(bin);
        LinkedBufferInput b2 = new LinkedBufferInput(8);
        b2.feed(src);

        for(int i=0; i < src.length; i++) {
            assertEquals(b1.readByte(), b2.readByte());
        }

        try {
            b2.readByte();
            assertTrue(false);
        } catch(EOFException eof) {
            assertTrue(true);
        }
    }

    @Test
    public void testFeedByteArrayCopy() throws IOException {
        byte[] small = new byte[8];
        small[0] = (byte)1;
        small[2] = (byte)1;
        small[4] = (byte)1;

        byte[] large = new byte[16];
        large[0] = (byte)1;
        large[3] = (byte)1;
        large[6] = (byte)1;
        large[10] = (byte)1;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        LinkedBufferInput b2 = new LinkedBufferInput(11);

        for(int i=0; i < 3; i++) {
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(small);
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(large);
            }
        }

        byte[] src = bout.toByteArray();
        ByteArrayInputStream bin = new ByteArrayInputStream(src);
        DataInputStream b1 = new DataInputStream(bin);

        for(int i=0; i < src.length; i++) {
            assertEquals(b1.readByte(), b2.readByte());
        }

        try {
            b2.readByte();
            assertTrue(false);
        } catch(EOFException eof) {
            assertTrue(true);
        }
    }

    @Test
    public void testFeedByteArrayNoCopy() throws IOException {
        byte[] small = new byte[8];
        small[0] = (byte)1;
        small[2] = (byte)1;
        small[4] = (byte)1;

        byte[] large = new byte[16];
        large[0] = (byte)1;
        large[3] = (byte)1;
        large[6] = (byte)1;
        large[10] = (byte)1;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        LinkedBufferInput b2 = new LinkedBufferInput(11);

        for(int i=0; i < 3; i++) {
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(small, true);
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(large, true);
            }
        }

        byte[] src = bout.toByteArray();
        ByteArrayInputStream bin = new ByteArrayInputStream(src);
        DataInputStream b1 = new DataInputStream(bin);

        for(int i=0; i < src.length; i++) {
            assertEquals(b1.readByte(), b2.readByte());
        }

        try {
            b2.readByte();
            assertTrue(false);
        } catch(EOFException eof) {
            assertTrue(true);
        }
    }

    @Test
    public void testFeedByteArrayCopyNoCopy() throws IOException {
        byte[] small = new byte[8];
        small[0] = (byte)1;
        small[2] = (byte)1;
        small[4] = (byte)1;

        byte[] large = new byte[16];
        large[0] = (byte)1;
        large[3] = (byte)1;
        large[6] = (byte)1;
        large[10] = (byte)1;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        LinkedBufferInput b2 = new LinkedBufferInput(11);

        for(int i=0; i < 3; i++) {
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(small);
            }
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(small, true);
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(large);
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(large, true);
            }
        }

        byte[] src = bout.toByteArray();
        ByteArrayInputStream bin = new ByteArrayInputStream(src);
        DataInputStream b1 = new DataInputStream(bin);

        for(int i=0; i < src.length; i++) {
            assertEquals(b1.readByte(), b2.readByte());
        }

        try {
            b2.readByte();
            assertTrue(false);
        } catch(EOFException eof) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetPrimitives() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        DataOutputStream o = new DataOutputStream(bo);
        o.writeByte((byte)2);
        o.writeShort((short)2);
        o.writeInt(2);
        o.writeLong(2L);
        o.writeFloat(1.1f);
        o.writeDouble(1.1);
        byte[] src = bo.toByteArray();

        LinkedBufferInput b = new LinkedBufferInput(1024);

        for(int i=0; i < 2; i++) {
            b.feed(src);

            assertEquals((byte)2, b.getByte());
            assertEquals((byte)2, b.getByte());
            b.advance();
            assertEquals((short)2, b.getShort());
            assertEquals((short)2, b.getShort());
            b.advance();
            assertEquals(2, b.getInt());
            assertEquals(2, b.getInt());
            b.advance();
            assertEquals(2L, b.getLong());
            assertEquals(2L, b.getLong());
            b.advance();
            assertEquals(1.1f, b.getFloat());
            assertEquals(1.1f, b.getFloat());
            b.advance();
            assertEquals(1.1, b.getDouble());
            assertEquals(1.1, b.getDouble());
            b.advance();
        }
    }

    @Test
    public void testGetPrimitivesChunks() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        DataOutputStream o = new DataOutputStream(bo);
        o.writeByte((byte)2);
        o.writeShort((short)2);
        o.writeInt(2);
        o.writeLong(2L);
        o.writeFloat(1.1f);
        o.writeDouble(1.1);
        byte[] src = bo.toByteArray();

        LinkedBufferInput b = new LinkedBufferInput(1024);

        for(int i=0; i < 2; i++) {
            for(int j=0; j < src.length; j++) {
                b.feed(src, j, 1, true);
            }

            assertEquals((byte)2, b.getByte());
            assertEquals((byte)2, b.getByte());
            b.advance();
            assertEquals((short)2, b.getShort());
            assertEquals((short)2, b.getShort());
            b.advance();
            assertEquals(2, b.getInt());
            assertEquals(2, b.getInt());
            b.advance();
            assertEquals(2L, b.getLong());
            assertEquals(2L, b.getLong());
            b.advance();
            assertEquals(1.1f, b.getFloat());
            assertEquals(1.1f, b.getFloat());
            b.advance();
            assertEquals(1.1, b.getDouble());
            assertEquals(1.1, b.getDouble());
            b.advance();
        }
    }


    @Test
    public void testFeedByteBufferCopy() throws IOException {
        byte[] small = new byte[8];
        small[0] = (byte)1;
        small[2] = (byte)1;
        small[4] = (byte)1;

        byte[] large = new byte[16];
        large[0] = (byte)1;
        large[3] = (byte)1;
        large[6] = (byte)1;
        large[10] = (byte)1;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        LinkedBufferInput b2 = new LinkedBufferInput(11);

        for(int i=0; i < 3; i++) {
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(ByteBuffer.wrap(small));
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(ByteBuffer.wrap(large));
            }
        }

        byte[] src = bout.toByteArray();
        ByteArrayInputStream bin = new ByteArrayInputStream(src);
        DataInputStream b1 = new DataInputStream(bin);

        for(int i=0; i < src.length; i++) {
            assertEquals(b1.readByte(), b2.readByte());
        }

        try {
            b2.readByte();
            assertTrue(false);
        } catch(EOFException eof) {
            assertTrue(true);
        }
    }

    @Test
    public void testFeedByteBufferNoCopy() throws IOException {
        byte[] small = new byte[8];
        small[0] = (byte)1;
        small[2] = (byte)1;
        small[4] = (byte)1;

        byte[] large = new byte[16];
        large[0] = (byte)1;
        large[3] = (byte)1;
        large[6] = (byte)1;
        large[10] = (byte)1;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        LinkedBufferInput b2 = new LinkedBufferInput(11);

        for(int i=0; i < 3; i++) {
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(ByteBuffer.wrap(small), true);
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(ByteBuffer.wrap(large), true);
            }
        }

        byte[] src = bout.toByteArray();
        ByteArrayInputStream bin = new ByteArrayInputStream(src);
        DataInputStream b1 = new DataInputStream(bin);

        for(int i=0; i < src.length; i++) {
            assertEquals(b1.readByte(), b2.readByte());
        }

        try {
            b2.readByte();
            assertTrue(false);
        } catch(EOFException eof) {
            assertTrue(true);
        }
    }

    @Test
    public void testFeedByteBufferCopyNoCopy() throws IOException {
        byte[] small = new byte[8];
        small[0] = (byte)1;
        small[2] = (byte)1;
        small[4] = (byte)1;

        byte[] large = new byte[16];
        large[0] = (byte)1;
        large[3] = (byte)1;
        large[6] = (byte)1;
        large[10] = (byte)1;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        LinkedBufferInput b2 = new LinkedBufferInput(11);

        for(int i=0; i < 3; i++) {
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(ByteBuffer.wrap(small));
            }
            for(int j=0; j < 7; j++) {
                bout.write(small);
                b2.feed(ByteBuffer.wrap(small), true);
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(ByteBuffer.wrap(large));
            }
            for(int j=0; j < 4; j++) {
                bout.write(large);
                b2.feed(ByteBuffer.wrap(large), true);
            }
        }

        byte[] src = bout.toByteArray();
        ByteArrayInputStream bin = new ByteArrayInputStream(src);
        DataInputStream b1 = new DataInputStream(bin);

        for(int i=0; i < src.length; i++) {
            assertEquals(b1.readByte(), b2.readByte());
        }

        try {
            b2.readByte();
            assertTrue(false);
        } catch(EOFException eof) {
            assertTrue(true);
        }
    }

}

