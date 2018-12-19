/*
* @ Author - Digistr.
* @ Info - Reads The channelBuffer Then Adds A Packet To The World Game Packets Queue.
*/

package com.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import com.model.World;
import com.model.Player;
import com.packet.Packet;
import com.packet.PacketReceiver;

public class RSDecoder extends FrameDecoder 
{
	private final Player me;
	private final Packet packet;
	
	public RSDecoder(Player me) 
	{
		packet = new Packet();
		this.me = me;
	}

      /*
      * 0 or more is a normal packet length.
      * -1 means read the firsts byte to decide the packets length.
      * -2 means skip because the packet was fragmented or not handled yet.
      */
	public static final byte[] PACKET_LENGTHS = new byte[] {
	   //0---1---2---3---4---5---6---7---8---9
	    -2,  8, -2,  6,  6, -2, -2, -2, -2, -2, // 0
	    -2, -1, -2, -2, -2, -2, -2, -2, -2, -2, // 1
	    -2, -2,  8, -2, -2, -2, -2, 13, -2,  6, // 2
	    -2,  6, -2, -2,  1,  8, -2,  6, -2, -2, // 3
	    -2,  8, -2, -2, -2, -2, -1,  2, -2, -2, // 4
	    -2, -2, -1, -2, -2,  8, -2, -2, -2, -1, // 5
	    -2, -2, -2, -2, -2,  6, -2, -2, -2, -2, // 6
	     6,  8, -2, -2, -2, -2, -2, -2, -2, -2, // 7
	    -2, -2,  8, -2, -2, -2, -2,  6, -1,  8, // 8
	    -2, -2, -2, -2, -2, -2,  2, -2, -2, -2, // 9
	    -2, -2,  8, -2, -2,  6, -2,  8, -2, -2, // 10
	    -2, -2, -2, -2, -2, -2,  2, -2, -2, -2, // 11
	    -2, -1,  8, -2,  8,  8, -2, -2, -2, -2, // 12
	    -2,  8, -2, -2, 14, -2, -2, -2, -2, -2, // 13
	    -2, -2, -2, -2, -2,  6, -2, -2, -2, -1, // 14
	     4,  0,  8, -2,  6, -2, -2, -2, -2, -2, // 15
	    -2, -2, -2,  8, -2, -2,  9,  4, -2,  4, // 16
	    -2, -2, -2, -2,  6,  6,  0, -2, -2, -2, // 17
	    -2, -2, -2, -2, -2,  2,  0, -2, -2, -2, // 18
	    12, -2,  6, -2, -2, -2,  2, -2, 11, -2, // 19
	    -2, -2, -1,  6, 10, -2, 16, -2, 14, -2, // 20
	    -2, -2, -2, -2, -2,  4, -2, -2, -2, -2, // 21
	    -2, -2, -2, -2, -2, -2,  2, -2, -2, -2, // 22
	    -2, -2, -2, -2,  6, -2,  0, -2, 14, -2, // 23
	    -2,  8, -2, -2, -2, -2, -2,  2, -2,  4, // 24
	     9,  10, -1, -2, -2, -2 		    // 25
	};
    /*
    * Decodes a ChannelBuffer(Raw Data) into a Packet(Server Data) which contains an opcode , length , data.
    */ 
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception 
	{
		if (buffer.readableBytes() > 0 && buffer.readableBytes() < 256) 
		{
			buffer.markReaderIndex();
			int id = buffer.readByte() & 0xFF;
			int length = PACKET_LENGTHS[id];
			if (length == -1) 
			{
				//READ VARIABLE PACKET LENGTH

				if (buffer.readableBytes() > 0)
					length = buffer.readByte() & 0xFF;
			}
	
			if (length == -2)
			{
				//SKIP UNHANDLED PACKET!
				System.out.println("[UNMANAGED PACKET] - "+id+" Length: "+length+" Readable Data: "+buffer.readableBytes());
				buffer.skipBytes(buffer.readableBytes());
				return buffer;
			}
			if (buffer.readableBytes() < length || length < 0)
			{
				//BLOCK PACKET FRAGMENTATION!
				buffer.resetReaderIndex();
				return null;
			}
			long t1 = System.nanoTime();
			packet.set(buffer, id, length);	
			PacketReceiver.send(me, packet);
			buffer.skipBytes(packet.readableBytes()); //Skip and remaining data that was unused.
			long t2 = System.nanoTime();
			//System.out.println("[HANDLED PACKET - 2] - "+id+" Length: "+length+ " Packet Data: " + packet.readableBytes() + " Remaining Data: "+ buffer.readableBytes() + " Time: " + (t2-t1));	
			return buffer;
		}
		buffer.skipBytes(buffer.readableBytes());
		return null;
	}
}
