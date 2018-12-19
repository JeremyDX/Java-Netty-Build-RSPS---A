/*
* @ Author - Digistr.
* @ Info - Only Vital Methods for the 474 Protocal have been added.
*/

package com.packet;

import org.jboss.netty.buffer.ChannelBuffer;

public class Packet 
{
	private ChannelBuffer buffer;
	public int id;
	private int beginIndex, size;

	public Packet()
	{
	
	}

	public void set(ChannelBuffer buf, int id, int length)
	{
		buffer = buf;
		this.id = id;
		this.beginIndex = buffer.readerIndex();
		this.size = length;
	}

	public int readerIndex() {
		return buffer.readerIndex();
	}

	public int getStarterIndex()
	{
		return beginIndex;
	}

	public byte[] array()
	{
		return buffer.array();
	}

	public void skipBytes(int skip)
	{
		buffer.skipBytes(skip);
	}

	public int readableBytes()
	{
		return (beginIndex + size) - buffer.readerIndex();
	}

	public int capacity()
	{
		return size;
	}

	public int readByte() 
	{
		return buffer.readByte() & 255;
	}
	
	public int readByteA() {
		return (buffer.readByte() - 128) & 255;
	}

	public int readByteS() {
		return (128 - buffer.readByte()) & 255;
	}

	public int readByteC() {
		return (-buffer.readByte()) & 255;
	}

	public int readShort() {
		return (readByte() << 8) | readByte();
	}

	public int readShortA() {
		return (readByte() << 8) | readByteA();
	}

	public int readLEShort() {
		return readByte() | (readByte() << 8);
	}

	public int readLEShortA() {
		return readByteA() | (readByte() << 8);
	}

	public int readInt() {
		return (readByte() << 24) | (readByte() << 16) | (readByte() << 8) | readByte();
	}

	public int readLEInt() {
		return readByte() | (readByte() << 8) | (readByte() << 16) | (readByte() << 24);
	}

	public int readInt1() {
		return (readByte() << 8) | readByte() | (readByte() << 24) | (readByte() << 16); 
	}

	public int readInt2() {
		return (readByte() << 16) | (readByte() << 24) | readByte() | (readByte() << 8); 
	}

	public long readLong() {
        	return ((0xffffffffL & (long)readInt()) << 32) + (0xffffffffL & (long)readInt());
	}

	public long readLong1() {
        	return ((0xffffffffL & (long)readInt1()) << 32) + (0xffffffffL & (long)readInt1());
	}

	public String readString() 
	{
		StringBuilder sb = new StringBuilder();
		byte b;
		while((b = buffer.readByte()) != 0) {
			sb.append((char) b);
		}
		return sb.toString();
	}
}
