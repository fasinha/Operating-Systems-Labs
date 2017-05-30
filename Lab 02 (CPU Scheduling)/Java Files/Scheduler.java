/*
 * Justin Mason
 * Operating Systems
 * Professor Alan Gottlieb
 * Lab 2: Scheduler
 * Completed March __, 2017
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Scheduler {
	
	
	public static Scanner randNums;
	public static Process[] processes;
	public static int finishingTime;
	public static int cyclesWithBlock = 0;
	public static boolean detailedOutput;
	
	//ROUND ROBIN ALGORITHM
	public static void rr() {
		
		ArrayList<Process> readyProcesses = new ArrayList<Process>();
		boolean finished = false;
		boolean processRunning = false;
		
		Process processToRun = new Process();
		
		// OUTPUT ALGORITHM USED
			System.out.println("The scheduling algorithm used was Round Robin");
			System.out.println();
		
		// INITIALIZE SCHEDULE CYCLE AND PROCESS ORDER
			int curCycle = 0;
			
			
		// BEGIN SCHEDULING PROCESS
			while (finished == false) {
				
				
				// UPDATE STATUSES
										
					for (int i = 0; i < processes.length; i++) {
						
						if (processes[i].status.equals("blocked")) {
							
							processes[i].remainingIOBurst -= 1;
							
							if (processes[i].remainingIOBurst <= 0) {
								processes[i].status = "ready";
								processes[i].blocked = false;
							} else {
								//processes[i].ioTime += 1;
								processes[i].blocked = true;
							}
						}
						
							
						if (processes[i].status.equals("running")) {
							
							processes[i].running = true;
							
							processes[i].remainingCPU -= 1;
							processes[i].remainingCPUBurst -= 1;
							
							if (processes[i].remainingCPU <= 0 ) {
								processes[i].status = "terminated";
								processes[i].running = false;
								processes[i].finishingTime = curCycle;
								processes[i].turnaroundTime = curCycle - processes[i].a;
								
							} else if (processes[i].remainingCPUBurst <= 0) {
								processes[i].status = "blocked";
								processes[i].remainingIOBurst = randomOS(processes[i].io);
								processes[i].running = false;
								processes[i].blocked = true;
								//processes[i].ioTime += 1;
							}
						}
						
						
						if (processes[i].status.equals("unstarted")) {
							
							if (processes[i].a <= curCycle) {
								processes[i].status = "ready";
							}
							
						}
						
						if (processes[i].status.equals("ready")) {
							
							if (readyProcesses.contains(processes[i]) == false) {
								readyProcesses.add(processes[i]);
							}
							
						}
						
					}
					
					// DETERMINE IF CYCLE CONTAINS A BLOCK
						for (int j = 0; j < processes.length; j++) {
							if (processes[j].blocked == true) {
								cyclesWithBlock += 1;
								break;
							}
						}
									
					// DETERMINE IF PROCESS NEEDS TO BE SELECTED TO RUN
						processRunning = false;
						for (int j = 0; j < processes.length; j++) {
							if (processes[j].running == true) {
								processRunning = true;
							}
						}
					
					// SELECT PROCESS TO RUN IF NECESSARY
						if (processRunning == false) {
							
													
							if (readyProcesses.size() != 0) {
	
								processToRun = readyProcesses.get(0); // Default
								readyProcesses.remove(0);
								
								
								// INCREMENT WAITING TIME FOR PROCESSES NOT SELECTED
									for (int i = 0; i < readyProcesses.size(); i++) {
										if (readyProcesses.get(i).processID != processToRun.processID) {
											//readyProcesses.get(i).waitingTime += 1;
											readyProcesses.get(i).curWaitingTime += 1;
										}
									}
								
								// RUN PROCESS
									processToRun.status = "running";
									processToRun.curWaitingTime = 0;
									processToRun.remainingCPUBurst = randomOS(processToRun.b);
									processToRun.running = true; //THIS MOTHERFUCKER
						}
						
						
					}
				
				
				// CHECK IF ALL PROCESSES ARE COMPLETE
					finished = true;
					for (int i = 0; i < processes.length; i++) {
						if (!processes[i].status.equals("terminated")) {
							finished = false;
						}
					}
				
					
				// DISPLAY CYCLE STATUS
					if (detailedOutput == true) {
						System.out.print(curCycle + ":\t"); //CHANGE BACK
						for (int i = 0; i < processes.length; i++) {
							
							//UPDATE IO/WAITING TIME
							if (processes[i].status.equals("blocked")) {
								processes[i].ioTime += 1;
							} else if (processes[i].status.equals("ready")) {
								processes[i].waitingTime += 1;
							}
							
							//DISPLAY STATUS
							System.out.print(processes[i].processID + ": " + processes[i].status + "\t");
						}
						System.out.println();
					}
				
				// INCREMENT CYCLE
					curCycle += 1;
				
			}
			
			finishingTime = curCycle - 1;
			System.out.println();
		
		
	}
	
	//SHORTEST REMAINING TIME NEXT ALGORITHM
	public static void srtn() {
		
		ArrayList<Process> readyProcesses = new ArrayList<Process>();
		boolean finished = false;
		boolean processRunning = false;
		
		Process processToRun = new Process();
		
		// OUTPUT ALGORITHM USED
			System.out.println("The scheduling algorithm used was Shortest Remaining Time Next");
			System.out.println();
		
		// INITIALIZE SCHEDULE CYCLE AND PROCESS ORDER
			int curCycle = 0;
			
			
		// BEGIN SCHEDULING PROCESS
			while (finished == false) {
				
				
				// UPDATE STATUSES
					
					readyProcesses.clear();
					
					for (int i = 0; i < processes.length; i++) {
						
						if (processes[i].status.equals("blocked")) {
							
							processes[i].remainingIOBurst -= 1;
							
							if (processes[i].remainingIOBurst <= 0) {
								processes[i].status = "ready";
								processes[i].blocked = false;
							} else {
								//processes[i].ioTime += 1;
								processes[i].blocked = true;
							}
						}
						
							
						if (processes[i].status.equals("running")) {
							
							processes[i].running = true;
							
							processes[i].remainingCPU -= 1;
							processes[i].remainingCPUBurst -= 1;
							
							if (processes[i].remainingCPU <= 0 ) {
								processes[i].status = "terminated";
								processes[i].running = false;
								processes[i].finishingTime = curCycle;
								processes[i].turnaroundTime = curCycle - processes[i].a;
								
							} else if (processes[i].remainingCPUBurst <= 0) {
								processes[i].status = "blocked";
								processes[i].remainingIOBurst = randomOS(processes[i].io);
								processes[i].running = false;
								processes[i].blocked = true;
								//processes[i].ioTime += 1;
							}
						}
						
						
						if (processes[i].status.equals("unstarted")) {
							
							if (processes[i].a <= curCycle) {
								processes[i].status = "ready";
							}
							
						}
						
						if (processes[i].status.equals("ready")) {
							readyProcesses.add(processes[i]);
						}
						
					}
					
					// DETERMINE IF CYCLE CONTAINS A BLOCK
						for (int j = 0; j < processes.length; j++) {
							if (processes[j].blocked == true) {
								cyclesWithBlock += 1;
								break;
							}
						}
									
					// DETERMINE IF PROCESS NEEDS TO BE SELECTED TO RUN
						processRunning = false;
						for (int j = 0; j < processes.length; j++) {
							if (processes[j].running == true) {
								processRunning = true;
							}
						}
					
					// SELECT PROCESS TO RUN IF NECESSARY
						if (processRunning == false) {
							
													
							if (readyProcesses.size() != 0) {
	
								processToRun = readyProcesses.get(0); // Default
								
								for (int j = 0; j < readyProcesses.size(); j++) {
									
									if (readyProcesses.get(j).remainingCPU < processToRun.remainingCPU) {
										processToRun = readyProcesses.get(j);
									}
									
									/*if (readyProcesses.get(j).curWaitingTime > processToRun.curWaitingTime) {
										processToRun = readyProcesses.get(j);
									} else if (readyProcesses.get(j).a < processToRun.a) {
										processToRun = readyProcesses.get(j);
									} else if (readyProcesses.get(j).a == processToRun.a) {
										if (readyProcesses.get(j).processID < processToRun.processID) {
											processToRun = readyProcesses.get(j);
										}
									}*/
								}
								
								// INCREMENT WAITING TIME FOR PROCESSES NOT SELECTED
									for (int i = 0; i < readyProcesses.size(); i++) {
										if (readyProcesses.get(i).processID != processToRun.processID) {
											//readyProcesses.get(i).waitingTime += 1;
											readyProcesses.get(i).curWaitingTime += 1;
										}
									}
								
								// RUN PROCESS
									processToRun.status = "running";
									processToRun.curWaitingTime = 0;
									processToRun.remainingCPUBurst = randomOS(processToRun.b);
									processToRun.running = true; //THIS MOTHERFUCKER
						}
						
						
					}
				
				
				// CHECK IF ALL PROCESSES ARE COMPLETE
					finished = true;
					for (int i = 0; i < processes.length; i++) {
						if (!processes[i].status.equals("terminated")) {
							finished = false;
						}
					}
				
					
				// DISPLAY CYCLE STATUS
					if (detailedOutput == true) {
						System.out.print(curCycle + ":\t"); //CHANGE BACK
						for (int i = 0; i < processes.length; i++) {
							
							//UPDATE IO/WAITING TIME
							if (processes[i].status.equals("blocked")) {
								processes[i].ioTime += 1;
							} else if (processes[i].status.equals("ready")) {
								processes[i].waitingTime += 1;
							}
							
							//DISPLAY STATUS
							System.out.print(processes[i].processID + ": " + processes[i].status + "\t");
						}
						System.out.println();
					}
				
				// INCREMENT CYCLE
					curCycle += 1;
				
			}
			
			finishingTime = curCycle - 1;
			System.out.println();
		
		
	}
	
	//LAST COME FIRST SERVED ALGORITHM
	public static void lcfs() {
		
		ArrayList<Process> readyProcesses = new ArrayList<Process>();
		boolean finished = false;
		boolean processRunning = false;
		
		Process processToRun = new Process();
		
		// OUTPUT ALGORITHM USED
			System.out.println("The scheduling algorithm used was Last Come First Served");
			System.out.println();
		
		// INITIALIZE SCHEDULE CYCLE AND PROCESS ORDER
			int curCycle = 0;
			
			
		// BEGIN SCHEDULING PROCESS
			while (finished == false) {
				
				
				// UPDATE STATUSES
					
					readyProcesses.clear();
					
					for (int i = 0; i < processes.length; i++) {
						
						if (processes[i].status.equals("blocked")) {
							
							processes[i].remainingIOBurst -= 1;
							
							if (processes[i].remainingIOBurst <= 0) {
								processes[i].status = "ready";
								processes[i].blocked = false;
							} else {
								//processes[i].ioTime += 1;
								processes[i].blocked = true;
							}
						}
						
							
						if (processes[i].status.equals("running")) {
							
							processes[i].running = true;
							
							processes[i].remainingCPU -= 1;
							processes[i].remainingCPUBurst -= 1;
							
							if (processes[i].remainingCPU <= 0 ) {
								processes[i].status = "terminated";
								processes[i].running = false;
								processes[i].finishingTime = curCycle;
								processes[i].turnaroundTime = curCycle - processes[i].a;
								
							} else if (processes[i].remainingCPUBurst <= 0) {
								processes[i].status = "blocked";
								processes[i].remainingIOBurst = randomOS(processes[i].io);
								processes[i].running = false;
								processes[i].blocked = true;
								//processes[i].ioTime += 1;
							}
						}
						
						
						if (processes[i].status.equals("unstarted")) {
							
							if (processes[i].a <= curCycle) {
								processes[i].status = "ready";
							}
							
						}
						
						if (processes[i].status.equals("ready")) {
							readyProcesses.add(processes[i]);
						}
						
					}
					
					// DETERMINE IF CYCLE CONTAINS A BLOCK
						for (int j = 0; j < processes.length; j++) {
							if (processes[j].blocked == true) {
								cyclesWithBlock += 1;
								break;
							}
						}
									
					// DETERMINE IF PROCESS NEEDS TO BE SELECTED TO RUN
						processRunning = false;
						for (int j = 0; j < processes.length; j++) {
							if (processes[j].running == true) {
								processRunning = true;
							}
						}
					
					// SELECT PROCESS TO RUN IF NECESSARY
						if (processRunning == false) {
							
													
							if (readyProcesses.size() != 0) {
	
								processToRun = readyProcesses.get(0); // Default
								
								for (int j = 0; j < readyProcesses.size(); j++) {
									
									if (readyProcesses.get(j).curWaitingTime < processToRun.curWaitingTime) {
										processToRun = readyProcesses.get(j);
									} else if (readyProcesses.get(j).a < processToRun.a) {
										processToRun = readyProcesses.get(j);
									} else if (readyProcesses.get(j).a == processToRun.a) {
										if (readyProcesses.get(j).processID < processToRun.processID) {
											processToRun = readyProcesses.get(j);
										}
									}
								}
								
								// INCREMENT WAITING TIME FOR PROCESSES NOT SELECTED
									for (int i = 0; i < readyProcesses.size(); i++) {
										if (readyProcesses.get(i).processID != processToRun.processID) {
											//readyProcesses.get(i).waitingTime += 1;
											readyProcesses.get(i).curWaitingTime += 1;
										}
									}
								
								// RUN PROCESS
									processToRun.status = "running";
									processToRun.curWaitingTime = 0;
									processToRun.remainingCPUBurst = randomOS(processToRun.b);
									processToRun.running = true; //THIS MOTHERFUCKER
						}
						
						
					}
				
				
				// CHECK IF ALL PROCESSES ARE COMPLETE
					finished = true;
					for (int i = 0; i < processes.length; i++) {
						if (!processes[i].status.equals("terminated")) {
							finished = false;
						}
					}
				
					
				// DISPLAY CYCLE STATUS
					if (detailedOutput == true) {
						System.out.print(curCycle + ":\t"); //CHANGE BACK
						for (int i = 0; i < processes.length; i++) {
							
							//UPDATE IO/WAITING TIME
							if (processes[i].status.equals("blocked")) {
								processes[i].ioTime += 1;
							} else if (processes[i].status.equals("ready")) {
								processes[i].waitingTime += 1;
							}
							
							//DISPLAY STATUS
							System.out.print(processes[i].processID + ": " + processes[i].status + "\t");
						}
						System.out.println();
					}
				
				// INCREMENT CYCLE
					curCycle += 1;
				
			}
			
			finishingTime = curCycle - 1;
			System.out.println();
		
		
	}

	//FIRST COME FIRST SERVED ALGORITHM
	public static void fcfs() {
		
		ArrayList<Process> readyProcesses = new ArrayList<Process>();
		boolean finished = false;
		boolean processRunning = false;
		
		Process processToRun = new Process();
		
		// OUTPUT ALGORITHM USED
			System.out.println("The scheduling algorithm used was First Come First Served");
			System.out.println();
		
		// INITIALIZE SCHEDULE CYCLE AND PROCESS ORDER
			int curCycle = 0;
			
			
		// BEGIN SCHEDULING PROCESS
			while (finished == false) {
				
				
				// UPDATE STATUSES
					
					readyProcesses.clear();
					
					for (int i = 0; i < processes.length; i++) {
						
						if (processes[i].status.equals("blocked")) {
							
							processes[i].remainingIOBurst -= 1;
							
							if (processes[i].remainingIOBurst <= 0) {
								processes[i].status = "ready";
								processes[i].blocked = false;
							} else {
								//processes[i].ioTime += 1;
								processes[i].blocked = true;
							}
						}
						
							
						if (processes[i].status.equals("running")) {
							
							processes[i].running = true;
							
							processes[i].remainingCPU -= 1;
							processes[i].remainingCPUBurst -= 1;
							
							if (processes[i].remainingCPU <= 0 ) {
								processes[i].status = "terminated";
								processes[i].running = false;
								processes[i].finishingTime = curCycle;
								processes[i].turnaroundTime = curCycle - processes[i].a;
								
							} else if (processes[i].remainingCPUBurst <= 0) {
								processes[i].status = "blocked";
								processes[i].remainingIOBurst = randomOS(processes[i].io);
								processes[i].running = false;
								processes[i].blocked = true;
								//processes[i].ioTime += 1;
							}
						}
						
						
						if (processes[i].status.equals("unstarted")) {
							
							if (processes[i].a <= curCycle) {
								processes[i].status = "ready";
							}
							
						}
						
						if (processes[i].status.equals("ready")) {
							readyProcesses.add(processes[i]);
						}
						
					}
					
					// DETERMINE IF CYCLE CONTAINS A BLOCK
						for (int j = 0; j < processes.length; j++) {
							if (processes[j].blocked == true) {
								cyclesWithBlock += 1;
								break;
							}
						}
									
					// DETERMINE IF PROCESS NEEDS TO BE SELECTED TO RUN
						processRunning = false;
						for (int j = 0; j < processes.length; j++) {
							if (processes[j].running == true) {
								processRunning = true;
							}
						}
					
					// SELECT PROCESS TO RUN IF NECESSARY
						if (processRunning == false) {
							
													
							if (readyProcesses.size() != 0) {
	
								processToRun = readyProcesses.get(0); // Default
								
								for (int j = 0; j < readyProcesses.size(); j++) {
									
									if (readyProcesses.get(j).curWaitingTime > processToRun.curWaitingTime) {
										processToRun = readyProcesses.get(j);
									} else if (readyProcesses.get(j).a < processToRun.a) {
										processToRun = readyProcesses.get(j);
									} else if (readyProcesses.get(j).a == processToRun.a) {
										if (readyProcesses.get(j).processID < processToRun.processID) {
											processToRun = readyProcesses.get(j);
										}
									}
								}
								
								// INCREMENT WAITING TIME FOR PROCESSES NOT SELECTED
									for (int i = 0; i < readyProcesses.size(); i++) {
										if (readyProcesses.get(i).processID != processToRun.processID) {
											//readyProcesses.get(i).waitingTime += 1;
											readyProcesses.get(i).curWaitingTime += 1;
										}
									}
								
								// RUN PROCESS
									processToRun.status = "running";
									processToRun.curWaitingTime = 0;
									processToRun.remainingCPUBurst = randomOS(processToRun.b);
									processToRun.running = true; //THIS MOTHERFUCKER
						}
						
						
					}
				
				
				// CHECK IF ALL PROCESSES ARE COMPLETE
					finished = true;
					for (int i = 0; i < processes.length; i++) {
						if (!processes[i].status.equals("terminated")) {
							finished = false;
						}
					}
				
					
				// DISPLAY CYCLE STATUS IF APPROPRIATE
					if (detailedOutput == true) {
						System.out.print(curCycle + ":\t"); //CHANGE BACK
						for (int i = 0; i < processes.length; i++) {
							
							//UPDATE IO/WAITING TIME
							if (processes[i].status.equals("blocked")) {
								processes[i].ioTime += 1;
							} else if (processes[i].status.equals("ready")) {
								processes[i].waitingTime += 1;
							}
							
							//DISPLAY STATUS
							System.out.print(processes[i].processID + ": " + processes[i].status + "\t");
						}
						System.out.println();
					}
				
				// INCREMENT CYCLE
					curCycle += 1;
				
			}
			
			finishingTime = curCycle - 1;
			System.out.println();
		
		
	}
	
	//"RANDOM" NUMBER GENERATOR
	public static int randomOS(int u) {
		return(1+(randNums.nextInt() % u));
	}
	
	//MAIN METHOD
	public static void main(String[] args) {
		
		Scanner fileInput;
		int numProcesses;
		
		try {
			
			
			// GET THE RANDOM NUMBERS FILE
				File randomNumsFile = new File("random-numbers.txt");
				randNums = new Scanner(randomNumsFile);
			
			
			// GET DETAILED OUTPUT CHOICE & INPUT FILE NAME
				
				File fileName = new File(args[0]);
				
				if (args.length == 1) {
					detailedOutput = false;
				} else if (args.length == 2) {
					fileName = new File(args[1]);
					detailedOutput = true;
				} else {
					System.out.println("ERROR: INVALID NUMBER OF COMMAND LINE ARGUMENTS (ACCEPTABLE RANGE 1-2)");
				}
				
				
			
			// DISPLAY ORIGINAL INPUT
				fileInput = new Scanner(fileName);
					System.out.print("The original input was:\t");
					
					numProcesses = fileInput.nextInt();
					
					ArrayList<Integer> appropriateA = new ArrayList<Integer>(numProcesses);
						/*
						 * An array list is used here to store arrival times of the processes.
						 * The benefit of using an array list is its built-in sort() function
						 * This array list can then be used to sort processes appropriately.
						 * 		Processes will be inserted in the first index of the upcoming array that matches its arrival time
						 */
					
					System.out.print(numProcesses + "\t");
					for (int i = 0; i < numProcesses; i++) {
						appropriateA.add(fileInput.nextInt());
						System.out.print(appropriateA.get(i) + " " + fileInput.nextInt() + " " + fileInput.nextInt() + " " + fileInput.nextInt() + "\t");
					}
					
					appropriateA.sort(null); //Sort the list of process arrival times
					
					System.out.println();
					
				fileInput.close();
				
			// FORM ARRAY OF PROCESSES
				fileInput = new Scanner(fileName);
				
				numProcesses = fileInput.nextInt();
				
				processes = new Process[numProcesses];
				
				for (int i = 0; i < numProcesses; i++) {
					Process curProcess = new Process(fileInput.nextInt(),fileInput.nextInt(),fileInput.nextInt(),fileInput.nextInt());
					processes[appropriateA.indexOf(curProcess.a)] = curProcess; //Insert process in first index of matching arrival time
					appropriateA.set(appropriateA.indexOf(curProcess.a), -1); //Ensure index will not be overwritten
				}
				
				
				fileInput.close();
			
			// DISPLAY SORTED INPUT
				System.out.print("The (sorted) input is:\t");
				System.out.print(numProcesses + "\t");
				for (int i = 0; i < numProcesses; i++) {
					processes[i].processID = i;
					System.out.print(processes[i].getA() + " " + processes[i].getB() + " " + processes[i].getC() + " " + processes[i].getIO() + "\t");
				}
				System.out.println();
				System.out.println();
				
			// RETRIEVE DESIRED ALGORITHM CHOICE
				Scanner input = new Scanner(System.in);
				System.out.print("Choose an algorithm [fcfs/rr/lcfs/srtn]: ");
					String userChoice = input.nextLine();
				System.out.println();
					
			// TRIGGER SELECTED ALGORITHM
				switch (userChoice) {
					case "fcfs": fcfs(); break;
					case "lcfs": lcfs(); break;
					case "srtn": srtn(); break;
					case "rr": rr(); break;
					default: System.out.println("Invalid choice. FCFS chosen by default."); fcfs(); break;
				}
				
				input.close();
				
			// DISPLAY INDIVIDUAL PROCESS RESULTS
				for (int i = 0; i < numProcesses; i++) {
					
					System.out.println("PROCESS " + i + ":");
						System.out.print("\t" + "(A,B,C,IO) = \t (");
							System.out.print(processes[i].getA() + ",");
							System.out.print(processes[i].getB() + ",");
							System.out.print(processes[i].getC() + ",");
							System.out.println(processes[i].getIO() + ")");
						System.out.println("\t" + "Finishing Time:\t " + processes[i].getFTime());
						System.out.println("\t" + "Turnaround Time: " + processes[i].getTTime());
						System.out.println("\t" + "I/O Time:\t " + processes[i].getIOTime());
						System.out.println("\t" + "Waiting Time:\t " + processes[i].getWTime());
					
					System.out.println();
					
				}
			
			// CALCULATE SUMMARY DATA
				
				double cpuUtilization = 0;
				double ioUtilization = 0.0;
				double throughput;
				double avgTurnaround = 0;
				double avgWaiting = 0;
				
				for (int i = 0; i < processes.length; i++) {
					cpuUtilization += processes[i].c;
					avgTurnaround += processes[i].turnaroundTime;
					avgWaiting += processes[i].waitingTime;
				}
				
				
				cpuUtilization /= finishingTime;
				ioUtilization = (double)cyclesWithBlock / (double)finishingTime;
				throughput = (100.0 / (double)finishingTime) * (processes.length);
				avgTurnaround /= processes.length;
				avgWaiting /= processes.length;
				
			// DISPLAY SUMMARY DATA
				System.out.println("SUMMARY DATA: ");
					System.out.println("\t" + "Finishing Time:\t\t\t " + finishingTime);
					System.out.println("\t" + "CPU Utilization:\t\t " + cpuUtilization);
					System.out.println("\t" + "I/O Utilization:\t\t " + ioUtilization);
					System.out.println("\t" + "Throughput:\t\t\t " + throughput + " processes per 100 cycles.");
					System.out.println("\t" + "Average turnaround time:\t " + avgTurnaround);
					System.out.println("\t" + "Average waiting time:\t\t " + avgWaiting);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
