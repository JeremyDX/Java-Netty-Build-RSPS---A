/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.model.systems.ClanManager;

public class KickClanMember implements PacketAssistant {
	
	public KickClanMember() {

	}

	public void send(Player p, Packet packet) 
	{
		ClanManager.kickPlayer(p, packet.readLong());
	}
}