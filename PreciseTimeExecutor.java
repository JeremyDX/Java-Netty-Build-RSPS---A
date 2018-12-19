/*
* @ Author - Digistr.
* @ Objective - Gives Almost Perfect 600 MS Tick Every Cycle. Excluding Delay's Higher Then 600ms.
* @ moreinfo - Reason i'm not using a ScheduledExecutorService/ExecutorService is it's unreliable in certain issues.
*/

package com;

import java.lang.InterruptedException;
import com.model.World;
import com.util.GroundItemManager;

public class PreciseTimeExecutor extends Thread {

	protected PreciseTimeExecutor() {
		Thread executor = new Thread(this);
		executor.setPriority(Thread.MAX_PRIORITY);
		executor.start();
	}

      /*
      * Server Tick = 600MS, Do not store this variable keep it in the code as is - Digistr.
      */
	public void run() {
		long t1 = System.nanoTime();
		int sleep = 0;
		long start = System.nanoTime();
		while (true) {
		      /*
		      * Start Of Task's
		      */
			try {
				GroundItemManager.execute();
				//t1 = System.nanoTime();
				World.tick();
			} catch (Exception e){ 
				e.printStackTrace();
			}
		      /*
		      * End Of Task's
		      */
			long end = System.nanoTime();
			sleep = (int)((end - start) * 0.000001) - (600 - sleep);
			start = System.nanoTime();
			//System.out.println("Time To Handle World Updates: " + (end - t1) + " World Size: " + World.curIndex);
			if (sleep > 600)
				sleep = 600;
			try {
				Thread.sleep(600 - sleep);
			} catch (InterruptedException ie){ 
				
			}
		}
	}
}