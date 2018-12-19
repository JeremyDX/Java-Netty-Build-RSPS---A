/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.Packet;
import com.packet.PacketBuilder;
import com.packet.PacketAssistant;

import com.model.Player;
import com.model.World;
import com.util.GameUtility;
import com.model.systems.Clan;
import com.model.systems.ClanManager;

import java.util.Iterator;
import java.util.HashMap;

public class Chat implements PacketAssistant {
	
	public Chat() {

	}

	public void send(Player me, Packet packet) 
	{
		if (packet.readableBytes() < 4)
			return;

		Short chatEffects = (short) packet.readShort();
		int length = packet.readByte();
		byte[] chatText = new byte[length];
		GameUtility.decryptText(chatText, packet.array(), packet.getStarterIndex() + 3);
		if (chatText[0] == 47)  
		{
			if ((me.chat().clanResponseCode & 0x10) != 0x10)
			{
				me.packetDispatcher().sendMessage("You are not currently within a clan chat channel...");
				return;
			}
			Clan clan = ClanManager.mapped_clans.get(me.chat().attemptingToJoin);
			if (clan == null)
				return;
			if (clan.members.get(me.details().USERNAME_AS_LONG) < clan.speakerRank)
			{
				me.packetDispatcher().sendMessage("You are not high enough rank to speak in this clan chat channel.");
				return;
			}	
			byte[] chat = new byte[256];
			chat[0] = (byte) (chatText.length - 1);
			int offset = GameUtility.encryptText(chat, chatText, 1);
			PacketBuilder clanMessagePacket = me.packetDispatcher().sendClanMessage(me, clan.displayName, offset, chat);
			
			Iterator iterator = clan.members.keySet().iterator();
			while (iterator.hasNext())
			{	
				World.getPlayerByName((long)iterator.next()).getPacket().addBytes(clanMessagePacket);
			}
			return;
		}
		me.updateFlags().setChat(chatText, chatEffects);
	}
}