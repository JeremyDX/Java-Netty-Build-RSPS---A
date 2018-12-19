/*
* @ Author - Digistr
* @ info - To Contain Vital Information About A Scheduled Task.
*/
package com.util;

public abstract class Task {

	public static Task EMPTY = new Task(0, false) 
	{
		@Override
		public void execute() 
		{

		}
	};

	private final byte ORIGINAL_CYCLE;
	public final boolean REPEAT;
	public byte curCycle;
	private boolean isDead;
	public boolean has_executed;

	public Task(int curCycle, final boolean REPEAT) 
	{
		ORIGINAL_CYCLE = (byte)(curCycle);
		this.curCycle = ORIGINAL_CYCLE;
		this.REPEAT = REPEAT;
	}

	public boolean canExecute() {
		return --curCycle <= 0;
	}

	public boolean listed() 
	{
		return curCycle > 0;
	}

	public void reset() 
	{
		curCycle = ORIGINAL_CYCLE;
		isDead = false;
	}

	public void die() 
	{
		isDead = true;
	}

	public boolean isDead() {
		return isDead;
	}

	@Override
	public String toString() {
		return "Cycle: " + curCycle + " Repeat: " + REPEAT + " Dead: " + isDead;
	}

	public abstract void execute();

}