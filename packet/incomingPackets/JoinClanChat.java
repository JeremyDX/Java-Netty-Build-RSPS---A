/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.model.systems.ClanManager;

public class JoinClanChat implements PacketAssistant {
	
	public JoinClanChat() {

	}

	public void send(Player me, Packet packet){
		long usernameAsLong = packet.readLong();
		ClanManager.joinClan(me, usernameAsLong); 
	}
}