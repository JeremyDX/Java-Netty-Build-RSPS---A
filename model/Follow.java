package com.model;

import com.util.PathObjectSystem;
import com.util.Task;
import com.util.TaskQueue;


public class Follow 
{
	private Task task = Task.EMPTY;
	private Player me;
	public short followIndex;

	public Follow(Player me)
	{
		this.me = me;
	}

	public void setFollower(short index) 
	{
		followIndex = index;

		if (!task.has_executed)
		{
			task = new Task(1, true)
			{
				@Override
				public void execute()
				{
					move();
				}
			};
			task.has_executed = true;
			TaskQueue.add(task);
		} else {
			TaskQueue.revive(task);
		}
	}

	public void reset() 
	{
		followIndex = -1;
		me.walkingQueue().clientPathing = false;
	}

	private void move() 
	{
		if (me == null || !me.details().isPlayerActive())
		{
			task.die();
			reset();
			return;
		}

		Player following = World.getPlayerByCheckingIndex(followIndex);

		if (following == null || !following.details().isPlayerActive() || following.skills().levels[3] == 0)
		{
			task.die();
			reset();
			return;
		}

		me.walkingQueue().moveToEntity(me, following, following.walkingQueue().faceDirection);
	}
	
}