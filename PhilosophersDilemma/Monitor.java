/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca  
 */
public class Monitor  
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */

	private boolean[] chopsticks; 	// chopsticks used to eat, one for ea. philosopher.
	private int[] eaten;			// which philosopher has eaten how many times.
	private boolean[] talking;		// tracker for who's currently talking.


	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		chopsticks = new boolean[piNumberOfPhilosophers];
		eaten = new int[piNumberOfPhilosophers];
		talking = new boolean[piNumberOfPhilosophers];

		for (int i = 0; i < piNumberOfPhilosophers; i++){
				eaten[i] = 0;
				chopsticks[i] = false;
				talking[i] = false;
		}
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID){
		int left,right, lEat, rEat;
		/*
		** the following sets the left and right chopsticks,
		** and the left and right philosphers to compare eating counts. 
		*/
		if (piTID == 1) { 					// if it's the first philosopher.
			left = 0;
			right = piTID;
			lEat =  chopsticks.length - 1;
			rEat =  piTID;
		}
		else if (piTID == chopsticks.length) { // if it's the last philosopher.
			left = piTID - 1;
			right = 0;
			lEat =  piTID - 2;
			rEat =  0;
		}
		else{					// if its any philospher between first and last.
			left = piTID - 1;
			right = piTID;
			lEat = piTID - 2;
			rEat = piTID;
		}

		while(chopsticks[left] || chopsticks[right]){ // if either one is not available, wait. 
													  // this cures deadlock.
			try{
				wait();
			}
			catch(Exception e){
				System.out.println("something went wrong with the 1st wait in pickUp");
			}
		}
		
		/*
		** the following makes sure I havent eaten more than my neighbours, curing starvation.
		*/
		if(eaten[lEat]<=eaten[piTID-1] || eaten[rEat]<=eaten[piTID-1]){ 
			if (!chopsticks[left] && !chopsticks[right]) {		// BOTH chopsticks must be present.
				//pickup chopsticks to eat
				chopsticks[left] = true;
				chopsticks[right] = true;
				//talking[piTID-1] = true; // this philocannot talk.
			}
			else{												// if not BOTH are available, wait.
				try{
				wait();
				}
				catch(Exception e){
					System.out.println("something went wrong with the 3rd wait in pickUp");
				}
			}
		}
		notify(); // tell the next person in line that i wasn't able to eat.
		return;
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID){
		int left,right;
		
		// the following sets the left and right chopsticks,
		if (piTID == 1) {
			left = 0;
			right = piTID;			
		}
		else if (piTID == chopsticks.length) {
			left = piTID - 1;
			right = 0;			
		}
		else{
			left = piTID - 1;
			right = piTID;
		}
		
		// reseet all stats, so that the next philospher can eat, possibly talk.
		chopsticks[left] = false;
		chopsticks[right] = false;
		eaten[piTID-1]++;
		talking[piTID-1] = false;
		notifyAll(); // tell everyone that I'm done eating.
		return;
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk(final int piTID)
	{
		for(int i = 0; i < talking.length; i++){ // look at all the philosophers
			if (talking[i]) {					// if any of them are talking we wait.
				try{
					wait();
			}
			catch(Exception e){
				System.out.println("something went wrong with the wait in requestTalk");
			}
		}
		talking[piTID-1] = true; // we grant the request to talk.
		return; // we've either finished the loop or the wait was signaled
	}
}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(final int piTID){
		talking[piTID-1] = false;
		notifyAll(); // when I'm done talking, notify everyone that everything is available again.
					// like chopsticks, and the ability to talk.
		return;
	}
}

// EOF
