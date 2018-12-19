/*
* @ Author - Digistr
* @ info - this packet is called when you have entered a person username.
*/

package com.packet.incomingPackets;

import com.packet.PacketAssistant;
import com.packet.Packet;
import com.model.Player;
import com.util.GameUtility;

public class EnteredName implements PacketAssistant {

	public EnteredName() {

	}

	public void send(Player p, Packet packet) 
	{
		long input = packet.readLong();
		switch (p.interfaceContainer().mainInterface)
		{
			case 107:
				p.packetDispatcher().sendInterfaceString(GameUtility.longToString(input), 107, 171);
			break;

			case 590:
				if (input != 0)
				{
					boolean firstTime = p.chat().myClanDisplayName == 0;
					p.chat().myClanDisplayName = input;
					p.packetDispatcher().sendInterfaceString(GameUtility.longToString(input), 590, 32);
					if (firstTime)
					{
						p.chat().updateFriendServer();
						p.packetDispatcher().sendMessage("Your clan " + GameUtility.longToString(input) + " has been created.");
					} else {
						p.chat().initiateClanChatUpdate();
					}
				}
			break;
		}
		System.out.println("Long Input: "+input);
	}
}