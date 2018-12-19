/*
* @ Author - Digistr
* @ info - This is recieved when flagStatus is 1 when sending Login.
* @ moreinfo - Recieved Every Cycle Length Varies.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class FlaggedUserReceiver implements PacketAssistant {
	
	public FlaggedUserReceiver() {

	}

	public void send(Player p, Packet packet){

	}
}