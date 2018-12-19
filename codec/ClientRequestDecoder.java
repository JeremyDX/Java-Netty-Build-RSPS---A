/*
* @ Author - Digistr , The array below is ripped from rune474 v2.
* @ info - Check for 4 bytes and reads the first byte and the returns a buffer with the length read, and sends ukeys.
*/

package com.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import com.packet.PacketBuilder;

public class ClientRequestDecoder extends FrameDecoder {

	private static final byte[] LOAD_UP_KEYS = {
		-1,0,-1,0,0,0,0,-128,-2,-69,-92,95,0,0,0,0,
		43,61,92,-40,0,0,0,0,-7,-76,26,-31,0,0,0,-2,
		92,-80,107,-41,0,0,0,108,90,98,-32,25,0,0,0,
		20,-90,-124,46,119,0,0,0,84,10,-28,49,48,0,
		0,0,0,103,-9,-101,90,0,0,0,116,42,19,-99,-8,
		0,0,0,25,-55,-93,70,58,0,0,0,3,46,-53,-92,-83,
		0,0,0,0,30,44,-35,98,0,0,0,0,-127,-57,-52,-118,
		0,0,0,83,7,-114,106,62,0,0,0,1,-93,-116,-10,
		-108,0,0,0,1,-72,-14,77,33,0,0,0,0
	};

	private static final PacketBuilder SEND_LOAD_KEYS = new PacketBuilder(LOAD_UP_KEYS);

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception 
	{
		System.out.println("Decoder: " + buffer.readableBytes());
		if (buffer.readableBytes() < 8)
			return null;
		int readLength = buffer.readByte();
		if (buffer.readableBytes() == readLength + 4) {
			buffer.skipBytes(readLength);
			channel.write(SEND_LOAD_KEYS).addListener(ChannelFutureListener.CLOSE);
			return buffer.readBytes(4);
		}
		channel.close();
		return buffer;
	}

}
