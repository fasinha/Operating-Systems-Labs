import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class ResourceManager {
	
	static boolean detailedOutput; //Display detailed output if second command line argument is given
	
	static int numTasks;
	public static int numResourceTypes;
	static int[] resources;
	
	static Task[] tasks;
	static ArrayList<Task> activeTasks = new ArrayList<Task>();
	static ArrayList<Task> blockedTasks = new ArrayList<Task>();
	static ArrayList<Task> nonblockedTasks = new ArrayList<Task>();
	
	static int[] pendingResourceGain = new int[numResourceTypes];
	
	// Perform initiation procedure
	public static void initiate(Task task, String managerType) {
		
		int resource = task.activity.resource;
		int claim = task.activity.value;
										
		// Check if delay is present
		if (task.curDelay > 0) {
		
			task.delayed = true;
			task.curDelay -= 1;
			
			if (task.curDelay == 0) { task.delayed = false; }
			if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " delayed (" + task.curDelay + " cycle(s) left)"); }
			nonblockedTasks.add(task);
			
		// Run initiate procedure if no delay is present
		} else {
			
			// Update the task's resource claims
			task.updateClaim(resource, claim);
			
			// Check if claim is higher than resources available; abort if so
			if (managerType.equals("banker")) {
				for (int i = 0; i < numResourceTypes; i++) {
					if (task.resourcesNeeded[i] > resources[i]) {
						task.aborted = true;
					}
				}
			}
			
			// Finish initiate procedure if abortion is unnecessary
			if (task.aborted == false) {
				task.nextActivity();
				task.curDelay = task.activity.delay;
				nonblockedTasks.add(task);
				if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " initiated."); }
			} else {
				if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " aborted (claim > # units)."); }
			}
			
		}
				
	}
	
	// (can only be called by the banker request procedure)
	// Simulates the current status of the simulation to see if completion is possible (safe state).
	// Returns true if safe state preserved, false if unsafe state found
	public static boolean checkSafe(int idRequesting, int resourceRequested, int resourceAmountRequested) {
		
		Task taskRequesting = new Task();
		
		// Create copy of active tasks (ensure no accidental modificiation of original data structure)
			ArrayList<Task> tasks = new ArrayList<Task>();
			for (int i = 0; i < activeTasks.size(); i++) {
				tasks.add(new Task());
				tasks.get(i).id = activeTasks.get(i).id;
				tasks.get(i).resourcesNeeded = activeTasks.get(i).resourcesNeeded;
				tasks.get(i).resourcesOwned = activeTasks.get(i).resourcesOwned;
			}
			
		// Tag the active task that is requesting the resource
			for (int i = 0; i < activeTasks.size(); i++) {
				if (activeTasks.get(i).id == idRequesting) {
					taskRequesting.id = activeTasks.get(i).id;
					taskRequesting.resourcesNeeded = activeTasks.get(i).resourcesNeeded;
					taskRequesting.resourcesOwned = activeTasks.get(i).resourcesOwned;
				}
			}
			
		// Create copy of resources (ensure no modification of original data structure)
			int[] availableResources = new int[numResourceTypes];
			for (int i = 0; i < numResourceTypes; i++) {
				availableResources[i] = resources[i];
			}
			
		// "Grant" resource request (does not actually grant, but will be used to check if granting the request can maintain a safe state
			availableResources[resourceRequested] -= resourceAmountRequested;
			taskRequesting.resourcesNeeded[resourceRequested] -= resourceAmountRequested;
			taskRequesting.resourcesOwned[resourceRequested] += resourceAmountRequested;
			
		// Check if the resource request leads to a safe (return true) or unsafe state (return false)
			boolean safeStatePossible = true; //evaluates if a safe state is possible
			boolean completable = false; //evaluates if a certain task is completable
			
			// If the while loop is completed and tasks.size() > 0, then we have an unsafe state
			while (safeStatePossible == true) {
				safeStatePossible = false; //Unsafe state until proven otherwise
				
				for (int i = 0; i < tasks.size(); i++) {
					completable = true; // Task is completable until proven otherwise
					
					//Check if the current task is completable
					for (int j = 0; j < numResourceTypes; j++) {
						if (tasks.get(i).resourcesNeeded[j] > availableResources[j]) {
							completable = false;
						}
					}
					
					//If at least one task is completable, the simulation could be in a safe state
					if (completable == true) {
						
						//Report potential safe state
						safeStatePossible = true;
						
						//Give resources back (simulate resource return)
						for (int j = 0; j < numResourceTypes; j++) {
							availableResources[j] += tasks.get(i).resourcesOwned[j];
						}
												
						//Remove "finished" task from the arraylist
						tasks.remove(tasks.get(i));
						
					}
					
				}
				
				//Check is the tasks arraylist is empty; if so, the simulation state is safe
				if (tasks.size() == 0) { return(true); }
			}
			
			// If this line is reached, then safeStatePossible=false and tasks.size() > 0. UNSAFE!
			return(false);
	}
	
	// Perform request procedure (optimistic manager)
	public static void optimisticRequest(Task task) {
		
		int resource = task.activity.resource;
		int unitsRequested = task.activity.value;
		int unitsAvailable = resources[resource];
						
		// Check if delay is present
		if (task.curDelay > 0) {
		
		task.delayed = true;
		task.curDelay -= 1;
		
		if (task.curDelay == 0) { task.delayed = false; }
		if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " delayed (" + task.curDelay + " cycle(s) left)"); }
		nonblockedTasks.add(task);
		
		// Run request procedure if no delay is present
		} else {
			
			// Request units
				// Grant request
				if (unitsAvailable >= unitsRequested) {
					resources[resource] -= unitsRequested;
					task.receiveUnits(resource, unitsRequested);
					task.nextActivity();
					task.curDelay = task.activity.delay;
					if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " granted " + unitsRequested + " R" + (resource+1) + "."); }
					nonblockedTasks.add(task);
				// Reject request
				} else {
					if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " not granted " + unitsRequested + " R" + (resource+1) + "."); }
					task.waitTime += 1;
					blockedTasks.add(task);
				}
			
		}		
		
	}
	
	// Perform request procedure (banker manager)
	public static void bankerRequest(Task task) {
		
		int resource = task.activity.resource;
		int unitsRequested = task.activity.value;
		int unitsAvailable = resources[resource];
		int maxSafeUnits = -1;
						
		// Check if delay is present
		if (task.curDelay > 0) {
		
		task.delayed = true;
		task.curDelay -= 1;
		
		if (task.curDelay == 0) { task.delayed = false; }
		if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " delayed (" + task.curDelay + " cycle(s) left)"); }
		nonblockedTasks.add(task);
		
		// Run request procedure if no delay is present
		} else {
			
			// Check if the request is illegal (would make units owned higher than units claimed)
			// If request is illegal, task should be aborted
			if (unitsRequested > task.resourcesNeeded[resource]) {
				task.aborted = true;
			}
			
			// Check if the request is unsafe
			int unitsNeeded = task.resourcesNeeded[resource];
			int unitsOwned = task.resourcesOwned[resource];
			boolean isSafeRequest = checkSafe(task.id, resource, unitsRequested);
			task.resourcesNeeded[resource] = unitsNeeded;
			task.resourcesOwned[resource] = unitsOwned;
								
			// Request units
				if (task.aborted == false) {
					// Grant request
					if ((unitsAvailable >= unitsRequested) && (isSafeRequest == true)) {
						resources[resource] -= unitsRequested;
						task.receiveUnits(resource, unitsRequested);
						task.nextActivity();
						task.curDelay = task.activity.delay;
						if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " granted " + unitsRequested + " R" + (resource+1) + "."); }
						nonblockedTasks.add(task);
					// Reject request (unsafe)
					} else if ((unitsAvailable >= unitsRequested) && (isSafeRequest == false)) {
						if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " not granted " + unitsRequested + " R" + (resource+1) + " (unsafe)."); }
						task.waitTime += 1;
						blockedTasks.add(task);
					// Reject request (unavailable)
					} else {
						if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " not granted " + unitsRequested + " R" + (resource+1) + "."); }
						task.waitTime += 1;
						blockedTasks.add(task);
					}
				} else {
					task.aborted = true;
					task.terminate = true;
					blockedTasks.add(task);
					if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " aborted (request > claim)."); }
				}
			
		}		
		
	}
	
	// Perform release procedure
	public static void release(Task task) {
		
		int delay = task.activity.delay;
		int resource = task.activity.resource;
		int unitsToSend = task.activity.value;
		int unitsOwned = task.resourcesOwned[resource];
						
		// Check if delay is present
		if (task.curDelay > 0) {
			
			task.delayed = true;
			task.curDelay -= 1;
			
			if (task.curDelay == 0) { task.delayed = false; }
			if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " delayed (" + task.curDelay + " cycle(s) left)"); }
			nonblockedTasks.add(task);
			
		// Run release procedure if no delay is present
		} else {
			
			// Request units
				// Accept release
				if (unitsOwned >= unitsToSend) {
					pendingResourceGain[resource] += unitsToSend;
					task.releaseUnits(resource, unitsToSend);
					task.nextActivity();
					task.curDelay = task.activity.delay;
					if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " releases " + unitsToSend + " R" + (resource+1) + "."); }
					nonblockedTasks.add(task);
				// Reject release
				} else {
					if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " cannot release " + unitsToSend + " R" + (resource+1) + " (more than owned)."); }
					task.waitTime += 1;
					blockedTasks.add(task);
				}
			
		}
		
	}

	// Perform terminate procedure
	public static void terminate(Task task, int endCycle) {
		
		
		// Check if delay is present
		if (task.curDelay > 0) {
			
			task.delayed = true;
			task.curDelay -= 1;
			
			if (task.curDelay == 0) { task.delayed = false; }
			if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " delayed (" + task.curDelay + " cycle(s) left)"); }
			nonblockedTasks.add(task);
		// Run terminate procedure if no delay is present
		} else {
			
			task.terminate = true;
			task.timeTaken = endCycle;
			if (detailedOutput) { System.out.printf("%-37s", "Task #" + task.id + " has been terminated."); }
			
		}
	}
	
	// Resolve discovered deadlock
	public static void resolveDeadlock() {
		
		int numAbortions = blockedTasks.size() - 1;
		int lowestID = 100000; //Arbitrary number to guarantee all found ID's are lower
		
		// Abort all tasks but the highest (highest ID)
		for (int i = 0; i < numAbortions; i++) {
			
			lowestID = 100000;
			
			// Determine the lowest ID within the blocked tasks
			for (int j = 0; j < blockedTasks.size(); j++) {
				if (blockedTasks.get(j).id < lowestID) {
					lowestID = blockedTasks.get(j).id;
				}
			}
			
			// Abort the task that possesses the lowest ID
			for (int j = 0; j < blockedTasks.size(); j++) {
				if (blockedTasks.get(j).id == lowestID) {
					if (detailedOutput) { System.out.println("\tDEADLOCK! Task #" + blockedTasks.get(j).id + " has been aborted."); }
					
					blockedTasks.get(j).aborted = true;
					blockedTasks.get(j).complete = true;
					blockedTasks.get(j).terminate = true;
					
					//Release all resources
					for (int k = 0; k < numResourceTypes; k++) {
						pendingResourceGain[k] += blockedTasks.get(j).resourcesOwned[k];
						blockedTasks.get(j).resourcesOwned[k] = 0;
					}
					
					blockedTasks.remove(j);
					break;
				}
			}
		}
		
		
	}
	
	// Run the banker simulation
	public static void bankerManager() {
			
		boolean simulationComplete = false;
		Task curTask = new Task();
		
		// Initialize all tasks as active
		for (int i = 0; i < tasks.length; i++) {
			activeTasks.add(tasks[i]);
		}
		
		// Perform simulation
		int curCycle = 0;
		
		if (detailedOutput) { System.out.println("================================BANKER RESOURCE MANAGER SIMULATION================================\n"); }
		
		while (activeTasks.size() > 0) {
			
			// Show resource availability
			if (detailedOutput) {
				System.out.print("  AVAILABLE: ");
				for (int j = 0; j < numResourceTypes; j++) {
					System.out.print("[R" + (j+1) + ": ");
					System.out.print(resources[j] + "]  ");
				}
				System.out.println("\n  -----------------------------------------");
			}
			
			// Perform a cycle
				for (int i = 0; i < activeTasks.size(); i++) {
					
					curTask = activeTasks.get(i);
					
					// Display current cycle
						if (detailedOutput) { System.out.printf(" %2d:  ", curCycle); }
						
					// Perform appropriate activity procedure
						switch (curTask.activity.type) {
							case "initiate": initiate(curTask, "banker"); break;
							case "request": bankerRequest(curTask); break;
							case "release": release(curTask); break;
							case "terminate": terminate(curTask, curCycle); break;
							default: break;
						}
					
					// Show task needs
						if (detailedOutput) {
							for (int j = 0; j < activeTasks.size(); j++) {
								if (activeTasks.get(j).terminate == false) {
									System.out.print("#" + activeTasks.get(j).id + " needs [");
									
									for (int k = 0; k < numResourceTypes; k++) {
										System.out.print(activeTasks.get(j).resourcesNeeded[k] + " ");
									}
									System.out.print("]\t");
								}
							}
							if (detailedOutput) { System.out.println(); }
						}
				}
				
				// Resolve deadlock if present
				if ((blockedTasks.size() > 0) && (nonblockedTasks.size() == 0)) {
					
					// Check if a task has already been aborted first
					boolean taskAlreadyAborted = false;
					for (int j = 0; j < blockedTasks.size(); j++) {
						if (blockedTasks.get(j).aborted == true) {
							taskAlreadyAborted = true;
							
							//Return resources
							for (int k = 0; k < numResourceTypes; k++) {
								resources[k] += blockedTasks.get(j).resourcesOwned[k];
							}
							
							blockedTasks.remove(blockedTasks.get(j));
							break;
						}
					}
					
					// Resolve deadlock if no task has been aborted
					if (taskAlreadyAborted == false) {
						if (detailedOutput) { System.out.println(); }
						resolveDeadlock();
					}
				}
				
				// Process pending resource gains
				for (int i = 0; i < numResourceTypes; i++) {
					resources[i] += pendingResourceGain[i];
					pendingResourceGain[i] = 0;
				}
				
				// Arrange activeTasks order for next cycle
				activeTasks.clear();
				activeTasks.addAll(blockedTasks);
				activeTasks.addAll(nonblockedTasks);
				blockedTasks.clear();
				nonblockedTasks.clear();
				
				// Increment the cycle
				curCycle += 1;
				if (detailedOutput) { System.out.println(); }
							
		}
		
		if (detailedOutput) { System.out.println("==================================================================================================\n"); }
															
	}

	// Run the optimistic manager simulation
	public static void optimisticManager() {
		
		boolean simulationComplete = false;
		Task curTask = new Task();
		
		// Initialize all tasks as active
		for (int i = 0; i < tasks.length; i++) {
			activeTasks.add(tasks[i]);
		}
		
		// Perform simulation
		int curCycle = 0;
		
		if (detailedOutput) { System.out.println("==============================OPTIMISTIC RESOURCE MANAGER SIMULATION==============================\n"); }
		
		while (activeTasks.size() > 0) {
			
			// Show resource availability
				if (detailedOutput) {
					System.out.print("  AVAILABLE: ");
					for (int j = 0; j < numResourceTypes; j++) {
						System.out.print("[R" + (j+1) + ": ");
						System.out.print(resources[j] + "]  ");
					}
					System.out.println("\n  -----------------------------------------");
				}
			
			// Perform a cycle
				for (int i = 0; i < activeTasks.size(); i++) {
					
					curTask = activeTasks.get(i);
					
					// Display current cycle
						if (detailedOutput) { System.out.printf(" %2d:  ", curCycle); }
						
					// Perform appropriate activity procedure
						switch (curTask.activity.type) {
							case "initiate": initiate(curTask, "optimistic"); break;
							case "request": optimisticRequest(curTask); break;
							case "release": release(curTask); break;
							case "terminate": terminate(curTask, curCycle); break;
							default: break;
						}
						
					if (detailedOutput) { System.out.println(); }
				}
				
				// Resolve deadlock if present
				if ((blockedTasks.size() > 0) && (nonblockedTasks.size() == 0)) {
					if (detailedOutput) { System.out.println(); }
					resolveDeadlock();
				}
				
				// Process pending resource gains
				for (int i = 0; i < numResourceTypes; i++) {
					resources[i] += pendingResourceGain[i];
					pendingResourceGain[i] = 0;
				}
				
				// Arrange activeTasks order for next cycle
				activeTasks.clear();
				activeTasks.addAll(blockedTasks);
				activeTasks.addAll(nonblockedTasks);
				blockedTasks.clear();
				nonblockedTasks.clear();
				
				// Increment the cycle
				curCycle += 1;
				if (detailedOutput) { System.out.println(); }
			
		}
		
		if (detailedOutput) { System.out.println("==================================================================================================\n"); }
														
	}
	
	
	// Read the input file, evaluating values for appropriate variables/objects.
	private static void readInput(String fileName) {
		
		File file = new File(fileName);
				
		String activityType = "";
		int taskNum = 0;
		int delay = 0;
		int resourceType = 0;
		int value = 0;
		
		try {
			
			Scanner fileInput = new Scanner(file);
			
			// Evaluate the number of tasks/resource types in the simulation
			numTasks = fileInput.nextInt();
			numResourceTypes = fileInput.nextInt();
			
			//Create an integer array of resource types
			resources = new int[numResourceTypes];
			pendingResourceGain = new int[numResourceTypes];
			
			//Evaluate the # of units each resource type has based on the input.
			//Initialize pending resource gain array as array of zeroes
			for (int i = 0; i < resources.length; i++) {
				resources[i] = fileInput.nextInt();
				pendingResourceGain[i] = 0;
			}
			
			//Initialize the array of tasks.
				tasks = new Task[numTasks];
				
				for (int i = 0; i < tasks.length; i++) {
					tasks[i] = new Task((i+1),numResourceTypes);
				}
			
			
			// Fill the tasks with its activities from the input				
			while (fileInput.hasNext()) {
				
				// Retrieve activity information from input
					activityType = fileInput.next();
					taskNum = fileInput.nextInt();
					delay = fileInput.nextInt();
					resourceType = fileInput.nextInt();
					value = fileInput.nextInt();
				
				// Add activity to the appropriate task
					tasks[taskNum-1].addActivity(activityType,delay,(resourceType-1),value);
			}
			
			//Set current activity (start activity) for the tasks
			for (int i = 0; i < tasks.length; i++) {
				tasks[i].activity = tasks[i].activities.get(0);
				tasks[i].curDelay = tasks[i].activity.delay;
			}
			
			fileInput.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	// Main method
	public static void main(String[] args) {
		
		String fileName = "";
		
		// Obtain file name from command line argument; report errors if necessary.
			if (args.length == 0) {
				System.out.println("ERROR: Filename not given. No input file can be read.");
			} else if (args.length == 1) {
				fileName = args[0];
				detailedOutput = false;
				System.out.println("OUTPUT VERSION: Results Only\n");
			} else if (args.length == 2) {
				fileName = args[0];
				detailedOutput = true;
				System.out.println("OUTPUT VERSION: Detailed Output\n");
			} else if (args.length > 2) {
				fileName = args[0];
				detailedOutput = true;
				System.out.println("WARNING: Multiple arguments given. First argument will be interpreted as file name. Second argument will be interpreted as desire to see detailed output. Other arguments will be ignored.");
				System.out.println("OUTPUT VERSION: Detailed Output\n");
			} else {
				fileName = args[0];
			}
			
		// Run the optimistic manager simulation
			if (!fileName.equals("")) { readInput(fileName); }
			optimisticManager();
			
		// Compute optimistic manager stats
			int totalTime = 0;
			int totalWaitTime = 0;
			double avgWaitPercent = 0.0;
			int completedTasks = 0;
			
			System.out.println("=================OPTIMISTIC RESOURCE MANAGER RESULTS=================");
			
			for (int i = 0; i < numTasks; i++) {
				
				tasks[i].showStats();
				
				if (tasks[i].aborted == false) {
					completedTasks += 1;
					totalTime += tasks[i].timeTaken;
					totalWaitTime += tasks[i].waitTime;
				}
				
				
			}
			avgWaitPercent = ((double)totalWaitTime / (double)totalTime) * 100;
			
		// Display optimistic manager stats
			System.out.printf("\t%-8s: ", "Total");
			System.out.printf("Took %d cycle(s); Waited %d cycle(s) (%.0f%%)\n", totalTime, totalWaitTime, avgWaitPercent);
			System.out.println("=====================================================================");
			System.out.println("\n");
						
		// Run the banker manager simulation
			if (!fileName.equals("")) { readInput(fileName); }
			bankerManager();

		// Compute banker manager stats
			totalTime = 0;
			totalWaitTime = 0;
			avgWaitPercent = 0.0;
			completedTasks = 0;
			
			System.out.println("===================BANKER RESOURCE MANAGER RESULTS===================");
			
			for (int i = 0; i < numTasks; i++) {
				
				tasks[i].showStats();
				
				if (tasks[i].aborted == false) {
					completedTasks += 1;
					totalTime += tasks[i].timeTaken;
					totalWaitTime += tasks[i].waitTime;
				}
				
				
			}
			avgWaitPercent = ((double)totalWaitTime / (double)totalTime) * 100;
			
		// Display banker manager stats
			System.out.printf("\t%-8s: ", "Total");
			System.out.printf("Took %d cycle(s); Waited %d cycle(s) (%.0f%%)\n", totalTime, totalWaitTime, avgWaitPercent);
			System.out.println("=====================================================================");

	}

}
