/*
* @ Author - Digistr.
* @ info - Much more simplified then the VAR_FIXED, VAR_BYTE , VAR_SHORT stuff found in most servers.
* @ moreinfo - I Would like to research more on netty to be sure the below way is the best way.
*/

package com.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import com.packet.PacketBuilder;

public class RSEncoder extends OneToOneEncoder {

   /*
   * Encodes a Packet(Server Data) message into a ChannelBuffer(Raw Data) which any client can interpret/use.
   */
	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
	    	PacketBuilder packet = (PacketBuilder) msg;
		return ChannelBuffers.copiedBuffer(packet.array(), 0, packet.writerIndex());
	}
}
