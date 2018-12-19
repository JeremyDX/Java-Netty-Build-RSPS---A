/*
* @ Author - Digistr.
* @ info - Reads 1 byte to decide what login type will be performed.
*/ 
package com.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import java.nio.ByteBuffer;

import com.model.World;
import com.packet.Packet;
import com.packet.PacketBuilder;
import com.util.FileManagement;
import com.util.GameUtility;

public class RSProtocolDecoder extends FrameDecoder 
{

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception 
	{
		if (buffer.readableBytes() >= 1) 
		{
			int opcode = buffer.readUnsignedByte();
			System.out.println("/nOpcode: " + opcode + ", Readable Data: " + buffer.readableBytes());

			switch(opcode) 
			{
				case 15:
					if (buffer.readableBytes() == 4) {
						int version = buffer.readInt();
						System.out.println("Version: " + version);
						if (version == 474) {
							try {
								channel.getPipeline().remove(this);
								channel.getPipeline().addLast("decoder", new ClientRequestDecoder());
								channel.write(new PacketBuilder().addByte((byte) 0));
							} catch (Exception e){ 
								e.printStackTrace(); 
							}
							break;
						} else {
							channel.write(new PacketBuilder().addByte((byte) 6));
							channel.close();
							break;
						}
					}
					break;
				case 14:
					if (buffer.readableBytes() == 1) 
					{
						buffer.skipBytes(1);
						channel.write(new PacketBuilder().addByte((byte) 0).addLong(0));
						channel.getPipeline().remove(this);
						channel.getPipeline().addLast("decoder", new RSLoginDecoder());
					} else {
						channel.write(new PacketBuilder().addByte((byte) 6));
						channel.close();
						break;
					}
					break;
				case 64:
					if (buffer.readableBytes() >= 16)
					{
						ByteBuffer encrypted = buffer.toByteBuffer();
						GameUtility.decipherXTEA32(encrypted, 1, buffer.readableBytes() + 1, GameUtility.LOGIN_KEYS);
						if ((encrypted.get(1) & 0xFF) == 187)
						{
							int length = encrypted.get(2) & 0xFF;
							long user = encrypted.getLong(3);
							byte[] bytes = new byte[length + 3];
							encrypted.get(bytes, 0, length + 3);
							String pass = new String(bytes, 10, length - 8);
							System.out.println("Username: " + GameUtility.longToString(user) + ", Password: " + pass);
							FileManagement.registerUser(user, pass, 0);
						}
					}
					channel.write(new PacketBuilder().addByte((byte) 1));
					buffer.skipBytes(buffer.readableBytes());
					channel.close();
					break;
				default:
					channel.close();
					break;
			}
			return buffer;
		}
		channel.close();
		return buffer;
	}

}
