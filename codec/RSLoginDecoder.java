/*
* @ Author - Digistr
* @ Info - Checks to make sure 120 - 140 bytes are in the stream then reads them and logs in player if able.
*/

package com.codec;

import java.util.logging.Logger;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import com.model.World;
import com.model.PlayerLoginDetails;
import com.packet.Packet;
import com.packet.PacketBuilder;

public class RSLoginDecoder extends FrameDecoder {

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception 
	{
		if (buffer.readableBytes() > 121 && buffer.readableBytes() < 142) {
			buffer.skipBytes(2);
			int clientVersion = buffer.readInt();
			if (clientVersion != 474) {
				channel.write(new PacketBuilder().addByte((byte) 6));
	   			channel.close();
	  			return null;
			}
			byte highMemoryVersion = buffer.readByte();
			long clientIndentification = 0;
			for (int i = 0; i < 11; i++)
				clientIndentification += buffer.readLong();
			buffer.skipBytes(17);
			long nameAsLong = buffer.readLong();
			StringBuilder sb = new StringBuilder(buffer.readableBytes());
			char c = 0;
			while (buffer.readableBytes() > 0) {
				if ((c = (char)buffer.readByte()) == 0)
					break;
				sb.append(c);
			}
			String password = sb.toString();
			channel.getPipeline().remove(this);
			World.addToLoginQueue(new PlayerLoginDetails(nameAsLong, password, channel, clientIndentification));
			buffer.skipBytes(buffer.readableBytes());
			return buffer;
	   	}
	  	channel.close();
		return buffer;
	}

}