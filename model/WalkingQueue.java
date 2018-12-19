/*
* @ Author - Digistr
* @ Objective - Handles everything to do with players walking.
*/

package com.model;

public class WalkingQueue {

	private short index;
	private byte firstTurnX, firstTurnY;
	private byte[][] steps = new byte[104][3];
	private byte readerIndex = 0;
	private byte readerLength = 0;
	public byte faceDirection;
	public boolean running = false;
	public int runLeft = 640000;
	public byte speed = 0;
	public boolean clientPathing = false;
	public int clientBaseX, clientBaseY;
	public int clientPathX, clientPathY;
	public byte walkDir = -1,runDir = -1;

   /*
   * Creates A Instance of the WalkingQueue.
   * @ index - this is the players index so we can use the player.
   * @ faceDirection - Direction player is currently facing my own version of it for player following.
   */
	protected WalkingQueue() 
	{

	}

	public void setIndex(short index) {
		this.index = index;
	}

   /*
   * Returns true if the player is able to run.
   */

	private boolean isRunning() {
		return running && runLeft > 6399;
	}

	public void setClientPathing(int baseX, int baseY)
	{
		clientBaseX = clientPathX = baseX;
		clientBaseY = clientPathY = baseY;
		clientPathing = true;
	}

   /*
   * Add steps with the first two coordinates read from Walking Packet.
   */
	public void setFirstDirection(short x, short y, Location l) 
	{
		firstTurnX = (byte)(x - l.x);
		firstTurnY = (byte)(y - l.y);
		byte addX = 1;
		byte addY = 0;
		byte dirX = 2;
		byte dirY = 3;
		int addSteps = firstTurnX;
		if (addSteps == 0) {
			addX = 0;
			dirX = 1;	
		} else if(addSteps < 0) {
			addSteps = -firstTurnX;
			addX = -1;
			dirX = 0;
		} else {
			dirY = 2;
		}
		if (firstTurnY < 0) {
			if (-firstTurnY > addSteps)
				addSteps = -firstTurnY;
			dirY = 5;
			addY = -1;	
		} else if (firstTurnY > 0) {
			if (firstTurnY > addSteps)
				addSteps = firstTurnY;
			addY = 1;
			dirY = 0;
		}
		byte dir = (byte)(dirX + dirY);
		for (int i = 0; i < addSteps; i++)
			addStepToQueue(addX, addY, dir);
		firstTurnX = 0;
		firstTurnY = 0;
	}

   /*
   * Adds Steps For each direction turned.
   */
	public void addDirectionChangeStep(byte x, byte y) 
	{
		int difX = x - firstTurnX;
		int difY = y - firstTurnY;
		firstTurnX = x;
		firstTurnY = y;
		byte addX = 1;
		byte addY = 0;
		byte dirX = 2;
		byte dirY = 3;
		int addSteps = difX;
		if (addSteps == 0) {
			addX = 0;
			dirX = 1;	
		} else if(addSteps < 0) {
			addSteps = -difX;
			addX = -1;
			dirX = 0;
		} else {
			dirY = 2;
		}
		if (difY < 0) {
			if (-difY > addSteps)
				addSteps = -difY;
			addY = -1;
			dirY = 5;		
		} else if (difY > 0) {
			if (difY > addSteps)
				addSteps = difY;
			addY = 1;
			dirY = 0;
		}
		byte dir = (byte)(dirX + dirY);
		for (int i = 0; i < addSteps; i++)
			addStepToQueue(addX, addY, dir);
	}

   /*
   * Adds a single step to the walking queue.
   */
	public void addStepToQueue(byte x, byte y, byte dir) 
	{
		if (readerLength >= steps.length)
			return;
		steps[readerLength][0] = x;
		steps[readerLength][1] = y;
		steps[readerLength++][2] = dir;
	}

	private static final byte[][] FACE_DIRS = 
	{
		{1, -1} , {0, -1}, {-1, -1},
		{1,  0} , {0,  0}, {-1,  0},
		{1,  1} , {0,  1}, {-1,  1}
	};

	private static final byte[] DIRECTIONS = 
	{
		 5,  6,  7,
		 3, -1,  4,
		 0,  1,  2
	};

	public void moveToEntity(Player me, Player entity, int direction)
	{
		Location loc = me.location();
		Location ent = entity.location();
			

		int xDif = (ent.x + FACE_DIRS[direction][0]) - loc.x;
		int yDif = (ent.y + FACE_DIRS[direction][1]) - loc.y;

		if (clientPathing)
		{
			int cpX = loc.x - clientPathX;
			int cpY = loc.y - clientPathY;

			int sqX = cpX * cpX;
			int sqY = cpY * cpY;

			if (ent.x < clientBaseX)
				clientBaseX = ent.x;
			if (ent.x > clientPathX)
				clientPathX = ent.x;
			if (ent.y < clientBaseY)
				clientBaseY = ent.y;
			if (ent.y > clientPathY)
				clientBaseY = ent.y;

			if ((sqX > 3 || sqY > 3 || (readerLength - readerIndex > 1))) 
				return;
			
			clientPathing = false;
		}

		readerIndex = 0;
		readerLength = 0;

		if (me.combat().attackingOn > -1)
		{
			xDif = ent.x - loc.x;
			yDif = ent.y - loc.y;
				
			int subX = xDif < 0 ? -1 : (xDif == 0 ? 0 : 1);
			int subY = yDif < 0 ? -1 : (yDif == 0 ? 0 : 1);

			int sqX = xDif * xDif;
			int sqY = yDif * yDif;

			if (me.fightType().type > 11)
			{
				if (me.fightType().type < 15)
				{
					if (sqX < 81 && sqY < 81)
						return;
				} else if (sqX < 121 && sqY < 121)
					return;
			}

			int distance = xDif * xDif + yDif * yDif;
			
			if (distance < 9)
			{
				int dir = (subY + 1) * 3 + (subX + 1);
				if (distance > 1)
				{
					if (entity.follow().followIndex == me.INDEX && entity.INDEX > me.INDEX && (speed > 0 || entity.walkingQueue().speed > 0))
						return;

					if (distance == 2) 
					{
						if (entity.walkingQueue().speed == 0 || direction == 1 || direction == 7)
						{
							subY = 0;
							dir = 4 + subX;
						} else {
							subX = 0;
							dir = 3 * subY + 4;
						}
					}
					addStepToQueue((byte)subX, (byte)subY, DIRECTIONS[dir]);
				} else
				{	
					if (distance == 0 && entity.walkingQueue().speed == 0)
						addStepToQueue((byte)-1, (byte)0, (byte)3);
					else {
						if (dir == 4 && direction == 3)
						{	
							if (entity.walkingQueue().speed == 1 && me.walkingQueue().speed == 1)
							{
								if (entity.follow().followIndex == me.INDEX && entity.INDEX > me.INDEX)
									return;
								addStepToQueue((byte)1, (byte)0, (byte)4);
							}
						}		
					}
				}
				return;
			}	
		}

		int subX = xDif < 0 ? -1 : (xDif == 0 ? 0 : 1);
		int subY = yDif < 0 ? -1 : (yDif == 0 ? 0 : 1);

		int sqX = xDif * xDif;
		int sqY = yDif * yDif;	

		int dir = (subY + 1) * 3 + (subX + 1);
		int distance = sqX + sqY;
		
		if (sqX > 0 || sqY > 0)
		{
			if ((running || entity.walkingQueue().running) && (me.combat().attackingOn > -1 || entity.combat().attackingOn > -1) && (distance == 9 || distance == 18))
			{
				if (entity.follow().followIndex == me.INDEX && entity.INDEX > me.INDEX)
				{
					return;
				}
			}

			addStepToQueue((byte)subX, (byte)subY, DIRECTIONS[dir]);

			if (running)
			{
				xDif -= subX;
				yDif -= subY;

				subX = xDif < 0 ? -1 : (xDif == 0 ? 0 : 1);
				subY = yDif < 0 ? -1 : (yDif == 0 ? 0 : 1);

				sqX = xDif * xDif;
				sqY = yDif * yDif;

				dir = (subY + 1) * 3 + (subX + 1);

				if (sqX > 0 || sqY > 0)
				{
					addStepToQueue((byte)subX, (byte)subY, DIRECTIONS[dir]);
				}
			}
		}
	}

   /*
   * Walk to what ever coordinate. Not to be used for Following an Entity. See @ moveToEntity(Player, Player, int);
   */
	public void walkToCoords(Location loc, int x, int y) 
	{
		reset();
		int toX = x - loc.x;
		int toY = y - loc.y;
		int difX = toX < 0 ? -toX : toX;
		int difY = toY < 0 ? -toY : toY;
		int difference = difX;
		if (difX > difY) {
			if (toX < 0) {
				for (int i = 0; i < (difX - difY); ++i) {
					addStepToQueue((byte)-1, (byte)0, (byte)3);					
				}
			} else {
				for (int i = 0; i < (difX - difY); ++i) {
					addStepToQueue((byte)1, (byte)0, (byte)4);
				}
			}
			difference = difY;
		} else if (difY > difX) {
			if (toY > 0) {
				for (int i = 0; i < (difY - difX); ++i) {
					addStepToQueue((byte)0, (byte)1, (byte)1);
				}	
			} else {
				for (int i = 0; i < (difY - difX); ++i) {
					addStepToQueue((byte)0, (byte)-1, (byte)6);
				}
			}
		}
		if (toX > 0) {
			if (toY > 0) {
				for (int i = 0; i < difference; ++i) {
					addStepToQueue((byte)1, (byte)1, (byte)2);
				}
			} else {
				for (int i = 0; i < difference; ++i) {
					addStepToQueue((byte)1, (byte)-1, (byte)7);
				}
			}
		} else {
			if (toY > 0) {
				for (int i = 0; i < difference; ++i) {
					addStepToQueue((byte)-1, (byte)1, (byte)0);
				}
			} else {
				for (int i = 0; i < difference; ++i) {
					addStepToQueue((byte)-1, (byte)-1, (byte)5);
				}
			}
		}	
	}

   /*
   * walks to the next step(s) in the walking queue.
   */
	public void sendNextPosition() 
	{
		Player player = World.getPlayerByClientIndex(index);
		if ((player.details().STATUS & 0xE) != 0x6)
			return;

		if (player.isTeleporting()) {
			walkDir = -1;
			runDir = -1;
			reset();
			player.setTeleport();
		} else {
			speed = 0;
			walkDir = getNextDir(player);
			if (walkDir != -1)
				speed = 1;
			runDir = -1;
			if (isRunning() && speed == 1) {
				runDir = getNextDir(player);
				if (runDir != -1) {
					speed = 2;
					runLeft -= 6400;
				}
				if (runLeft < 6400) {
					running = false;
					player.packetDispatcher().sendConfig(173, 0);
				}
			} else {
				float increment = (player.skills().levels[16] + 100);
				increment *= increment / (speed + 1.5F);
				increment *= (2 - speed);

				runLeft += (int)increment;
				if (runLeft > 640000)
					runLeft = 640000;
			}
			player.packetDispatcher().sendEnergy((byte)(runLeft * 0.00015625D));
			if (speed == 0) {
				player.packetDispatcher().sendRemoveMapFlag();
			}
		}
		int differenceX = player.lastLocation().regionX() - player.location().regionX();
		int differenceY = player.lastLocation().regionY() - player.location().regionY();
		if (differenceX > 3)
			player.updateFlags().flagsAreUpdated[0] = true;
		if (differenceY > 3)
			player.updateFlags().flagsAreUpdated[0] = true;
		if (differenceX < -3)
			player.updateFlags().flagsAreUpdated[0] = true;
		if (differenceY < -3)
			player.updateFlags().flagsAreUpdated[0] = true;
		if (player.updateFlags().flagsAreUpdated[0]) 
		{
			if (walkDir != -1)
				player.location().minus(steps[--readerIndex][0], steps[readerIndex][1]);
			if (runDir != -1)
				player.location().minus(steps[--readerIndex][0], steps[readerIndex][1]);
			walkDir = -1;
			runDir = -1;
		}
	}

   /*
   * Gets the next direction of the walking queue and stores the players new position.
   * Along with storing the next face direction used in player follow.
   */
	private byte getNextDir(Player player) 
	{
		if (readerIndex >= readerLength)
		{
			clientPathing = false;
			return -1;
		}

		boolean walkable = com.util.PathObjectSystem.isWalkable(player.location().x, player.location().y, player.location().x + steps[readerIndex][0],player.location().y + steps[readerIndex][1],player.location().z,steps[readerIndex][2]);
		player.location().add(steps[readerIndex][0], steps[readerIndex][1]);

		if (clientPathing)
		{
			if (player.location().x >= clientBaseX && player.location().x <= clientPathX && 
				player.location().y >= clientBaseY && player.location().y <= clientPathY)
			{
				clientPathing = false;
			}
		}

		byte[] faceDirs = {0,1,2,3,5,6,7,8,4};
		faceDirection = faceDirs[steps[readerIndex][2]];
		return steps[readerIndex++][2];
	}

   /*
   * Resets the walking queue.
   */
	public void reset() 
	{
		readerIndex = 0;
		readerLength = 0;
		steps[0][0] = 0;
		steps[0][1] = 0;
		steps[0][2] = -1;
	}
	
}