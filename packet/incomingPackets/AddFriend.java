/*
* @ Author - Digistr
* @ info - none.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;

public class AddFriend implements PacketAssistant {

	public AddFriend() {

	}

	public void send(Player p, Packet packet){
		long username = packet.readLong();
		if (p.details().USERNAME_AS_LONG != username)
			p.chat().addFriend(username, 0);	
	}
}