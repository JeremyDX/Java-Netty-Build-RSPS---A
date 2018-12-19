package com.util;

public class FileBuilder {
	
	private byte[] buffer;
	private int readerIndex = 0;
	private int writerIndex = 0;

	public void skipBytes(int skipped) {
		writerIndex += skipped;
	}

	public int readableBytes() {
		return (buffer.length - readerIndex);
	}

	public int readerIndex() {
		return readerIndex;
	}

	public int writerIndex() {
		return writerIndex;
	}

	public byte[] array() {
		return buffer;
	}

	public FileBuilder(int length) {
		buffer = new byte[length];
	}

	public FileBuilder(byte[] buffer) {
		this.buffer = buffer;
	}
	
	public int writeLength() {
		int size = writerIndex;
		writerIndex = 0;
		writeShort(size);
		return size;	
	}

	public void writeByte(byte data) {
		buffer[writerIndex++] = data;
	}
	
	public void writeShort(int data) {
		buffer[writerIndex++] = (byte)(data >> 8);
		buffer[writerIndex++] = (byte)(data);
	}

	public void writeInt(int data) {
		buffer[writerIndex++] = (byte)(data >> 24);
		buffer[writerIndex++] = (byte)(data >> 16);
		buffer[writerIndex++] = (byte)(data >> 8);
		buffer[writerIndex++] = (byte)(data);
	}

	public void writeLong(long data) {
		buffer[writerIndex++] = (byte)(data >> 56);
		buffer[writerIndex++] = (byte)(data >> 48);
		buffer[writerIndex++] = (byte)(data >> 40);
		buffer[writerIndex++] = (byte)(data >> 32);
		buffer[writerIndex++] = (byte)(data >> 24);
		buffer[writerIndex++] = (byte)(data >> 16);
		buffer[writerIndex++] = (byte)(data >> 8);
		buffer[writerIndex++] = (byte)(data);
	}

	public void writeString(String s) {
		writeString(s.toCharArray());
	}

	public void writeString(char[] data) {
		for (int i = 0; i < data.length; i++)
			buffer[writerIndex++] = (byte)data[i];
		buffer[writerIndex++] = 0;
	}

	/*
	 * Allows for an actual string length with no variable sizes.
	 * Keep in mind if you do length 20 you can only do a string of size 19.
	 * This method doesn't validate length mismatches so be careful with usuage.
	 */
	public void writeString(char[] data, int length) {
		for (int i = 0; i < data.length; i++)
			buffer[writerIndex++] = (byte)data[i];
		buffer[writerIndex++] = 0;
		writerIndex += length - data.length;
	}

	public int readByte() {
		return buffer[readerIndex++] & 255;
	}

	public int readShort() {
		return (readByte() << 8) | readByte();
	}

	public int readInt() {
		return (readByte() << 24) | (readByte() << 16) | (readByte() << 8) | readByte();
	}

	public long readLong() {
        	return ((0xffffffffL & (long)readInt()) << 32) + (0xffffffffL & (long)readInt());
	}

	public int safeReadByte(int default_value) 
	{
		if (buffer.length - readerIndex > 0)
			return readByte();
		return default_value;
	}

	public int safeReadShort(int default_value) 
	{
		if (buffer.length - readerIndex > 1)
			return readShort();
		return default_value;
	}

	public int safeReadInt(int default_value) 
	{
		if (buffer.length - readerIndex > 3)
			return readInt();
		return default_value;
	}

	public long safeReadLong(int default_value) 
	{
		if (buffer.length - readerIndex > 7)
			return readLong();
		return default_value;
	}

	public String readString() {
		StringBuilder sb = new StringBuilder();
		byte b;
		while((b = buffer[readerIndex++]) != 0) {
			sb.append((char) b);
		}
		return sb.toString();
	}

	public String readString(int fixedSize) {
		StringBuilder sb = new StringBuilder();
		byte b;
		while((b = buffer[readerIndex++]) != 0) {
			sb.append((char) b);
		}
		readerIndex += fixedSize - sb.length();
		return sb.toString();
	}

}