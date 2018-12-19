/*
* @ Author - Digistr
* @ info - Handles following players.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.model.World;

public class RightClickOptionFollow implements PacketAssistant {

	public RightClickOptionFollow() {

	}

	public void send(Player player, Packet packet){
		short clientIndex = (short)packet.readLEShortA();
		if (clientIndex == player.INDEX)
			return;
		Player other = World.getPlayerByCheckingIndex(clientIndex);
		if (other != null) {
			player.follow().setFollower(clientIndex);
			player.updateFlags().setFaceToPlayer(clientIndex);
		}
	}
}