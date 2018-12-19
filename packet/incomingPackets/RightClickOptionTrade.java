/*
* @ Author - Digistr
* @ info - Handles trading players by sending requests.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.model.World;

public class RightClickOptionTrade implements PacketAssistant {

	public RightClickOptionTrade() {

	}

	public void send(Player p, Packet packet){
		short clientIndex = (short)packet.readShortA();
		if (clientIndex == p.INDEX)
			return;
		Player traderRequesting = World.getPlayerByCheckingIndex(clientIndex);
		if (traderRequesting != null) {
			p.trade().sendRequest(p, traderRequesting);
		}
	}
}