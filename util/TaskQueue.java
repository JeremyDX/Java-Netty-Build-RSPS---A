/*
* @ Author - Digistr
* @ Objective - Perfect For Executing Tasks' We only need once in awhile.
*   Do not use as a replacement for tasks that go every cycle it's slower!!!
*/
package com.util;

import java.util.ArrayDeque;
import java.util.Deque;

public class TaskQueue {

	private static final Deque<Task> QUEUED_TASKS = new ArrayDeque<Task>();

	public static void revive(Task tc)
	{
		if (!tc.isDead())
			return;
		boolean listed = tc.listed();
		tc.reset();
		if (!listed)
		{
			System.out.println("[TASK - REVIVE - DEAD TASK / REMOVED TASK] - " + tc.toString());
			QUEUED_TASKS.add(tc);
		}
	}

	public static void add(Task tc) 
	{
		System.out.println("[TASK - QUEUE - ADDED] - " + tc.toString());
		QUEUED_TASKS.add(tc);
	}

	public static void queue() 
	{
		Task tc = null;
		int size = QUEUED_TASKS.size();

		for (int i = 0; i < size; i++) 
		{
			tc = QUEUED_TASKS.poll();
			if (tc.isDead()) {
				tc.curCycle = 0;
				System.out.println("TASK = DEAD");
				continue;
			}
			if (tc.canExecute()) 
			{
				tc.execute();
				System.out.println("TASK = EXECUTED");
				if (tc.REPEAT && !tc.isDead()) 
				{
					QUEUED_TASKS.addLast(tc);
					tc.reset();
				}
			} else {
				System.out.println("Cycle Not Done Yet... " + size);
				QUEUED_TASKS.addLast(tc);
			}
		}

		if (QUEUED_TASKS.size() > 1)
		{
			System.out.println("Task Size: " + QUEUED_TASKS.size() + " Old Size: " + size);
		}
	}
}