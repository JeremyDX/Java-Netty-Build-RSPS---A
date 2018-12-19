/*
* @ Author - Digistr
* @ info - this reads incoming reports from players weather they are mod, abusetype, and abuser.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.util.GameUtility;

public class ReportPlayer implements PacketAssistant {

	public ReportPlayer() {

	}

	public void send(Player p, Packet packet){
		long abuser = packet.readLong();
		int rights = packet.readByte();
		int report = packet.readByte();
		int sendPacket = packet.readByte();
		String name = GameUtility.longToString(abuser);	
	}
}