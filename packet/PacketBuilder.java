/*
* @ Author - Digistr, 
* @ Info - Only Vital Methods for the 474 Protocal have been added.
*/

package com.packet;

public final class PacketBuilder {

	private static final int[] BIT_MASK = {0, 0x1, 0x3, 0x7, 0xf, 0x1f, 0x3f, 0x7f,
	    0xff, 0x1ff, 0x3ff, 0x7ff, 0xfff, 0x1fff, 0x3fff, 0x7fff, 0xffff,
	    0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff,
	    0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff,
	    0x3fffffff, 0x7fffffff, -1};

	private byte[] buffer;
	private int writerIndex;
	private int oldWriterPosition;
	private int bitPosition;

	public PacketBuilder() {
		buffer = new byte[16];
	}

	public PacketBuilder(int length) {
		buffer = new byte[length];
	}

	public PacketBuilder(byte[] data) {
		buffer = data;
		writerIndex = buffer.length;
	}

	public int writerIndex() {
		return writerIndex;
	}

	public byte[] array() {
		return buffer;
	}

	public int capacity() {
		return buffer.length;
	}

	public void reset() {
		buffer = new byte[16];
		writerIndex = 0;
	}

	public PacketBuilder createPacket(int id) {
		ensureBytes(1);
		buffer[writerIndex++] = (byte)id;
		return this;
	}

	public PacketBuilder createPacketTypeByte(int id) {
		ensureBytes(2);
		buffer[writerIndex++] = (byte)id;
		oldWriterPosition = writerIndex;
		writerIndex++;
		return this;
	}

	public PacketBuilder createPacketTypeShort(int id) {
		ensureBytes(3);
 		buffer[writerIndex++] = (byte)id;
		oldWriterPosition = writerIndex;
 		writerIndex++;
		writerIndex++;
		return this;
	}

	public void endPacketTypeByte() {
		buffer[oldWriterPosition] = (byte)(writerIndex - oldWriterPosition - 1);
	}

	public void endPacketTypeShort() {
		buffer[oldWriterPosition] = (byte)((writerIndex - oldWriterPosition - 2) >> 8);
		buffer[oldWriterPosition + 1] = (byte)(writerIndex - oldWriterPosition - 2);
	}

	public PacketBuilder addByte(byte value) {
		ensureBytes(1);
		buffer[writerIndex++] = value;
		return this;
	}

	public PacketBuilder addByteA(int value) {
		ensureBytes(1);
		buffer[writerIndex++] = (byte) (value + 128);
		return this;
	}
	
	public PacketBuilder addByteS(int value) {
		ensureBytes(1);
		buffer[writerIndex++] = (byte) (128 - value);
		return this;
	}

	public PacketBuilder addByteC(int value) {
		ensureBytes(1);
		buffer[writerIndex++] = (byte) -value;
		return this;
	}

	public PacketBuilder addBytes(PacketBuilder builder) {
		ensureBytes(builder.writerIndex);
		for (int i = 0; i < builder.writerIndex; i++)
			buffer[writerIndex++] = builder.buffer[i];
		return this;
	}

	public PacketBuilder addBytes(byte[] data) {
		ensureBytes(data.length);
		System.arraycopy(data, 0, buffer, writerIndex, data.length);
		writerIndex += data.length;
		return this;
	}

	public PacketBuilder addBytes(byte[] data, int start, int end) {
		ensureBytes(end - start + 1);
		for (int i = start; i < end; i++)
			buffer[writerIndex++] = data[i];
		return this;
	}

	public PacketBuilder addBytesReversed(PacketBuilder builder) {
		ensureBytes(builder.writerIndex);
		for (int i = builder.writerIndex - 1; i > -1; i--)
			buffer[writerIndex++] = builder.buffer[i];
		return this;
	}

	public PacketBuilder addBytesReversed(byte[] data, int offset, int length) {
		ensureBytes(data.length);
		for (int k = (length + offset) - 1; k >= length; k--)
			buffer[writerIndex++] = data[k];
		return this;
	}

	public PacketBuilder addSmart(int value) {
		if (value < 128)
			return addByte((byte) value);
		return addShort(value + 32768);
	}

	public PacketBuilder addShort(int value) {
		ensureBytes(2);
		buffer[writerIndex++] = (byte) (value >> 8);
		buffer[writerIndex++] = (byte) value;
		return this;
	}

	public PacketBuilder addShortA(int value) {
		ensureBytes(2);
		buffer[writerIndex++] = (byte) (value >> 8);
		buffer[writerIndex++] = (byte) (value + 128);
		return this;
	}

	public PacketBuilder addLEShort(int value) {
		ensureBytes(2);
		buffer[writerIndex++] = (byte) value;
		buffer[writerIndex++] = (byte) (value >> 8);
		return this;
	}

	public PacketBuilder addLEShortA(int value) {
		ensureBytes(2);
		buffer[writerIndex++] = (byte) (value + 128);
		buffer[writerIndex++] = (byte) (value >> 8);
		return this;
	}

	public PacketBuilder addTriByte(int value)
	{
		ensureBytes(3);
		buffer[writerIndex++] = (byte) (value >> 16);
		buffer[writerIndex++] = (byte) (value >> 8);
		buffer[writerIndex++] = (byte) (value >> 0);
		return this;
	}

	public PacketBuilder addInt(int value) {
		ensureBytes(4);
		buffer[writerIndex++] = (byte) (value >> 24);
		buffer[writerIndex++] = (byte) (value >> 16);
		buffer[writerIndex++] = (byte) (value >> 8);
		buffer[writerIndex++] = (byte) value;
		return this;
	}

	public PacketBuilder addLEInt(int value) {
		ensureBytes(4);
		buffer[writerIndex++] = (byte) value;
		buffer[writerIndex++] = (byte) (value >> 8);
		buffer[writerIndex++] = (byte) (value >> 16);
		buffer[writerIndex++] = (byte) (value >> 24);
		return this;
	}

	public PacketBuilder addInt1(int value) {
		ensureBytes(4);
		buffer[writerIndex++] = ((byte) (value >> 8));
		buffer[writerIndex++] = ((byte) value);
		buffer[writerIndex++] = ((byte) (value >> 24));
		buffer[writerIndex++] = ((byte) (value >> 16));
		return this;
	}

	public PacketBuilder addInt2(int value) {
		ensureBytes(4);
		buffer[writerIndex++] = ((byte) (value >> 16));
		buffer[writerIndex++] = ((byte) (value >> 24));
		buffer[writerIndex++] = ((byte) value);
		buffer[writerIndex++] = ((byte) (value >> 8));
		return this;
	}

	public PacketBuilder addLong(long value) {
		ensureBytes(8);
		buffer[writerIndex++] = (byte)(int)(value >> 56);
		buffer[writerIndex++] = (byte)(int)(value >> 48);
		buffer[writerIndex++] = (byte)(int)(value >> 40);
		buffer[writerIndex++] = (byte)(int)(value >> 32);
		buffer[writerIndex++] = (byte)(int)(value >> 24);
		buffer[writerIndex++] = (byte)(int)(value >> 16);
		buffer[writerIndex++] = (byte)(int)(value >> 8);
		buffer[writerIndex++] = (byte)(int)(value);
		return this;
	}

	public PacketBuilder addString(String s) {
		char[] data = s.toCharArray();
		ensureBytes(data.length + 1);
		for (int i = 0; i < data.length; i++)
			buffer[writerIndex++] = (byte)data[i];
		buffer[writerIndex++] = 0;
		return this;
	}

   /*
   * Basically it just verify's there is enough room in the buffer.
   */ 
	public void ensureBytes(int newCapacity) {
		if ((newCapacity + writerIndex) >= buffer.length) {
			byte[] newBuffer = new byte[(newCapacity + writerIndex) * 2];
			System.arraycopy(buffer, 0, newBuffer, 0, writerIndex);
			buffer = newBuffer;
		}
	}

    /*
    * These methods are used in player updating.
    */

	public PacketBuilder startBitWriter() {
		bitPosition = writerIndex * 8;
		return this;
	}

	public PacketBuilder endBitWriter() {
		writerIndex = (bitPosition + 7) / 8;
		return this;
	}

	public PacketBuilder addBits(int numBits, final int value) {
		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		bitPosition += numBits;
		writerIndex = (bitPosition + 7) / 8;
		ensureBytes(writerIndex);
		for (; numBits > bitOffset; bitOffset = 8) {
			buffer[bytePos] &= ~ BIT_MASK[bitOffset];
			buffer[bytePos++] |= (value >> (numBits - bitOffset)) & BIT_MASK[bitOffset];
			numBits -= bitOffset;
		}
		if (numBits == bitOffset) {
			buffer[bytePos] &= ~ BIT_MASK[bitOffset];
			buffer[bytePos] |= value & BIT_MASK[bitOffset];
		} else {
			buffer[bytePos] &= ~ (BIT_MASK[numBits] << (bitOffset - numBits));
			buffer[bytePos] |= (value & BIT_MASK[numBits]) << (bitOffset - numBits);
		}
		return this;
	}

}
