/*
* @ Author - Digistr.
* @ Info - All Known Packets at the moment are listed in an interface array for obtaining them much faster then switch.
*/

package com.packet;

import com.model.Player;
import com.packet.incomingPackets.*;

public class PacketReceiver {

	public static PacketAssistant incomingPacketTypes[] = new PacketAssistant[256];

	private PacketReceiver() { 

	}

	static {
		DeadPacket dead = new DeadPacket();
		Walk walk = new Walk();
		incomingPacketTypes[1] = new RemoveIgnore(); //8
		incomingPacketTypes[3] = new InterfaceOptionFour(); //6
		incomingPacketTypes[4] = new ObjectFifthOption(); //6
		incomingPacketTypes[11] = walk;	//-1
		incomingPacketTypes[22] = new KickClanMember(); //8
		incomingPacketTypes[27] = new Appearence(); //13
		incomingPacketTypes[29] = new InterfaceOptionSix(); //6
		incomingPacketTypes[31] = new ObjectFirstOption(); //6
		incomingPacketTypes[34] = new WindowState(); //1
		incomingPacketTypes[35] = new MagicOnPlayer(); //?
		incomingPacketTypes[37] = new InterfaceOptionThree(); //6
		incomingPacketTypes[41] = new InventoryOptionThree(); //8
		incomingPacketTypes[46] = walk; //-1
		incomingPacketTypes[47] = new ItemExamine(); //2
		incomingPacketTypes[52] = new Chat(); //-1
		incomingPacketTypes[55] = new InterfaceItemOptionFour(); //8
		incomingPacketTypes[59] = walk; //-1
		incomingPacketTypes[65] = new InterfaceOptionNine(); //6
		incomingPacketTypes[70] = new InterfaceOptionFive(); //6
		incomingPacketTypes[71] = new InventoryOptionFour(); //8
		incomingPacketTypes[82] = new EnteredName(); //8
		incomingPacketTypes[87] = new InterfaceOptionTwo(); //6
		incomingPacketTypes[88] = new EnteredString(); //-1
		incomingPacketTypes[89] = new InventoryOptionTwo(); //8
		incomingPacketTypes[96] = new RightClickOptionAttack(); //2
		incomingPacketTypes[102] = new InterfaceItemOptionThree(); //8
		incomingPacketTypes[105] = new GroundItemOptionOne(); //6
		incomingPacketTypes[107] = new InterfaceItemOptionTwo(); //8
		incomingPacketTypes[121] = new FlaggedUserReceiver(); //-1
		incomingPacketTypes[122] = new RemoveFriend(); //8
		incomingPacketTypes[124] = new InventoryOptionOne(); //8
		incomingPacketTypes[125] = new InterfaceItemOptionFive(); //8
		incomingPacketTypes[131] = new AddFriend(); //8
		incomingPacketTypes[134] = new ItemOnObject(); //14
		incomingPacketTypes[145] = new ObjectThirdOption(); //6
		incomingPacketTypes[149] = dead; //-1
		incomingPacketTypes[150] = new MapRegionHasUpdated();//4
		incomingPacketTypes[151] = new CloseInterface(); //0
		incomingPacketTypes[152] = new AddIgnore(); //8
		incomingPacketTypes[154] = new InterfaceOptionEight();//6
		incomingPacketTypes[163] = new JoinClanChat();//8
		incomingPacketTypes[166] = new ItemSwaping(); //9
		incomingPacketTypes[167] = dead;//4
		incomingPacketTypes[169] = dead;//4
		incomingPacketTypes[174] = new ObjectFourthOption(); //6
		incomingPacketTypes[175] = new GroundItemOptionTwo(); //6
		incomingPacketTypes[176] = dead;//-1
		incomingPacketTypes[185] = new RightClickOptionFollow(); //2
		incomingPacketTypes[186] = new Ping();//-1
		incomingPacketTypes[190] = new MagicOnGroundItem(); //14
		incomingPacketTypes[192] = new InterfaceOptionSeven(); //6
		incomingPacketTypes[196] = new NpcOption1();
		incomingPacketTypes[198] = new ReportPlayer(); //11
		incomingPacketTypes[202] = new Commands(); //-1
		incomingPacketTypes[203] = new ObjectSecondOption(); //6
		incomingPacketTypes[204] = new ItemOnPlayer(); //6
		incomingPacketTypes[206] = new ItemOnItem();
		incomingPacketTypes[208] = new ItemOnGroundItem(); //12
		incomingPacketTypes[215] = new Button(); //4
		incomingPacketTypes[226] = new ObjectExamine(); //2
		incomingPacketTypes[234] = new InterfaceOptionOne(); //6
		incomingPacketTypes[236] = new InactiveLogout(); //0
		incomingPacketTypes[238] = new MagicOnInventoryItem(); //14
		incomingPacketTypes[241] = new InterfaceItemOptionOne(); //8
		incomingPacketTypes[247] = new RightClickOptionTrade(); //2
		incomingPacketTypes[249] = new EnteredAmount(); //4
		incomingPacketTypes[250] = new ChangeClanChatRanks(); //9
		incomingPacketTypes[251] = new ButtonTypesChanged(); //10
	}
 
	public static void send(Player p, Packet packet) 
	{
		try {
			incomingPacketTypes[packet.id].send(p, packet);
		} catch (Exception e) {
			System.out.println("Packet: " + packet.id + " Error Occured " + e.toString());
		}
	}

}