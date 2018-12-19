/*
* @ Author - Digistr
* @ info1 - A Basic Player Class with all the class's and variable you need for a basic Player.
* @ info2 - All these varibles are technically 'final' however some where unable to recieve that modifier.
*/

package com.model;

import org.jboss.netty.channel.Channel;
import com.packet.PacketDispatcher;
import com.packet.PacketBuilder;
import com.util.Task;
import com.util.BooleanContainer;
import com.util.IndexContainer;
import com.util.FileManagement;

public class Player 
{
	private final PlayerLoginDetails DETAILS;
	private final Location LOCATION;
	private final Location LASTLOCATION;
	private final Location TELEPORT;
	private final Skills SKILLS;
	private final UpdateFlags UPDATEFLAGS;
	private final PacketDispatcher PACKETDISPATCHER;
	private final WalkingQueue WALKINGQUEUE;
	private final Bank BANK;
	private final Trade TRADE;
	private final Equipment EQUIPMENT;
	private final Inventory INVENTORY;
	private final Combat COMBAT;
	private final Specials SPECIALS;
	private final BooleanContainer GROUND_ITEMS;
	private final IndexContainer PLAYER_LIST;
	private final IndexContainer NPC_LIST;
	private final InterfaceContainer INTERFACE;
	private final PacketBuilder PACKET;
	private final FightType FIGHTTYPE;
	private final Damage DAMAGE; 
	private final PlayerChat PLAYER_CHAT;
	private final ItemsOnDeath ITEMS_ON_DEATH;
	private final Follow FOLLOW;
	private final Prayer PRAYER;

	public Task ground_item_task = Task.EMPTY;

	public short INDEX;
	public short RANK;

	private byte actionBlock;
	private byte lastHitDelay;

    /*
    * Creates a Player instance, and stores the players login details.
    */
	public Player(PlayerLoginDetails details) 
	{
		DETAILS = details;
		LOCATION = new Location();
		LASTLOCATION = new Location();
		TELEPORT = new Location(-1,-1,-1);
		SKILLS = new Skills(this);
		FOLLOW = new Follow(this);
		PACKETDISPATCHER = new PacketDispatcher(this);
		UPDATEFLAGS = new UpdateFlags();
		WALKINGQUEUE = new WalkingQueue();
		BANK = new Bank();
		TRADE = new Trade();
		EQUIPMENT = new Equipment();
		INVENTORY = new Inventory();
		COMBAT = new Combat(this);
		SPECIALS = new Specials();
		GROUND_ITEMS = new BooleanContainer();
		PLAYER_LIST = new IndexContainer();
		NPC_LIST = new IndexContainer();
		INTERFACE = new InterfaceContainer();
		FIGHTTYPE = new FightType();
		DAMAGE = new Damage();
		PRAYER = new Prayer();
		PLAYER_CHAT = new PlayerChat();
		ITEMS_ON_DEATH = new ItemsOnDeath();
		PACKET = new PacketBuilder(4000);
	}

    /*
    * Once we have a player Index, we set our previously created class's with our index.
    * for more memory effecient way of accessing our player's account.
    */
	public void finish() 
	{
		UPDATEFLAGS.setIndex(INDEX);
		WALKINGQUEUE.setIndex(INDEX);
		BANK.setIndex(INDEX);
		TRADE.setIndex(INDEX);
		EQUIPMENT.setIndex(INDEX);
		INVENTORY.setIndex(INDEX);
		SPECIALS.setIndex(INDEX);
		DAMAGE.setIndex(INDEX);
		ITEMS_ON_DEATH.setIndex(INDEX);
		PLAYER_CHAT.setIndex(INDEX);
	}

	public PlayerLoginDetails details() {
		return DETAILS;	
	}

	public Location location() {
		return LOCATION;	
	}

	public Location lastLocation() {
		return LASTLOCATION;	
	}

	public Location teleLocation() {
		return TELEPORT;
	}

	public void setCoords(int x, int y, int z) {
		LOCATION.setCoords(x,y,z);
	}
	
	public void setLastLocation() {
		LASTLOCATION.setLocation(LOCATION);
	}

	public boolean isTeleporting() {
		return TELEPORT.z != -1;
	}

	public void setTeleport() {
		LOCATION.setLocation(TELEPORT);
		TELEPORT.setCoords(-1,-1,-1);
	}

	public void setTeleport(int x, int y, int z) {
		TELEPORT.setCoords(x,y,z);
		updateFlags().flagsAreUpdated[1] = true;
	}

	public PacketDispatcher packetDispatcher() {
		return PACKETDISPATCHER;
	}

	public UpdateFlags updateFlags() {
		return UPDATEFLAGS;	
	}

	public WalkingQueue walkingQueue() {
		return WALKINGQUEUE;	
	}

	public Skills skills() {
		return SKILLS;	
	}

	public Bank bank() {
		return BANK;	
	}

	public Trade trade() {
		return TRADE;	
	}

	public Equipment equipment() {
		return EQUIPMENT;	
	}

	public Inventory inventory() {
		return INVENTORY;	
	}

	public ItemsOnDeath itemsOnDeath() {
		return ITEMS_ON_DEATH;
	}

	public Combat combat() {
		return COMBAT;
	}

	public Specials special() {
		return SPECIALS;
	}

	public PlayerChat chat() {
		return PLAYER_CHAT;
	}

	public Follow follow() {
		return FOLLOW;
	}

	public FightType fightType() {
		return FIGHTTYPE;
	}

	public Damage damage() {
		return DAMAGE;
	}

	public Prayer prayer() {
		return PRAYER;
	}

	public BooleanContainer groundItems() {
		return GROUND_ITEMS;
	}

	public IndexContainer playerList() {
		return PLAYER_LIST;
	}

	public IndexContainer npcList() {
		return NPC_LIST;
	}

	public InterfaceContainer interfaceContainer() {
		return INTERFACE;
	}

	public Channel getSession() {
		return DETAILS.CHANNEL;	
	}

	public PacketBuilder getPacket() {
		return PACKET;
	}

	public boolean isUnableToWalk() {
		return UPDATEFLAGS.flagsAreUpdated[1] || isTeleporting() || SKILLS.levels[3] == 0;
	}

	/*
	 * Add any situation which would prevent this player from being disconneted from our World player list. 
	 * This ensure the player remains logged in until we are ready to fully disconnect them. 
	*/

	public boolean isDisconnectable()
	{
		return true; //lastHitDelay <= 0;
	}

	public boolean isBusy()
	{
		return INTERFACE.mainInterface != -1;
	}
   /*
   * Executes Tasks every 600ms could vary depending on Computer / Lag / How much is in here.
   * Only put tasks which execute all the time! even if its one time every 2 minutes.
   * This is faster then TaskQueue Handler but it's only meant for tasks which must run all the time.
   *
   * Info On Whats Current Here:
   * sendNextPosition() - Sends the players walking on the server must be done every 600ms.
   */
	public void tick() 
	{
		WALKINGQUEUE.sendNextPosition();
	}
}