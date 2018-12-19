/*
* @ Author - Digistr.
* @ Info - Taken right from the some Netty tuturial and coverted to fit my needs.
* @ Objective - channelDisconnect() & channelExeception() will disconnect a player from main World List when these are called.
*/

package com.codec;

import java.net.InetSocketAddress;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import com.model.Player;
import com.packet.Packet;
import com.model.World;

public class ChannelHandler extends SimpleChannelHandler 
{
	private Player me;

      /*
      * Sets the players index as an attachment to this handler.
      */ 
	public void setAttachment(Player me) 
	{
		this.me = me;
	}

      /*
      * Each method is sent when it's name happens.
      */
	@Override
	public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) 
	{
		System.out.println("Connection Began!");
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) 
	{
		if (me == null || (me.details().STATUS & 0x4) == 0x0)	
			return;

		me.details().STATUS ^= 0x4; //Disable Connection To Server.
		me.packetDispatcher().crashDisconnectSecurity(); //Run Security Disconnects Before We Terminate Player.
		if(!World.remove(me))
			System.out.println("WARNING!!! - FATAL ERROR DISCONNECTING PLAYER: " + me.details().USERNAME_AS_STRING);
	}
}
