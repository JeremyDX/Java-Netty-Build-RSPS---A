/*
* @ Author - Digistr.
* @ info - The interface that each Packet uses.
*/

package com.packet;

import com.model.Player;

public interface PacketAssistant
{
	public void send(Player p, Packet packet);
}
