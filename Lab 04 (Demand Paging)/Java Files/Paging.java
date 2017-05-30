import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Paging {
	
	// Variables for provided values
	static int machineSize;
	static int pageSize;
	static int processSize;
	static int jobMix;
	static int rpp;
	static String algorithm;
	static int outputType;
	
	static FrameTable frames;
	static Process[] processes;
	static ArrayList<Process> processQueue;
	
	static Scanner randNums;
	
	// Run the simulation
	public static void runSimulation() {
		
		Process curProcess;
		
		// Initialize the process queue
			processQueue = new ArrayList<Process>();
			for (int i = 0; i < processes.length; i++) {
				processQueue.add(processes[i]);
			}
		
		// Run the simulation
			int curCycle = 1;
			Frame frameToReplace = null;
			
			while (processQueue.size() > 0) {
				
				// Give each process a turn (3 cycles)
				for (int i = 0; i < processQueue.size(); i++) {
					
					curProcess = processQueue.get(i);
					
					for (int j = 0; j < 3; j++) {
						if (curProcess.references > 0) {
							curProcess.makeReference(curCycle, randNums);
							if (frames.findHit(curProcess) != -1 ) {
								if (outputType != 0) { System.out.printf("Hit in frame %d.\n", frames.findHit(curProcess)); }

								frames.incResidencies();
								
								
							} else {
								curProcess.numFaults += 1;
								
								if (frames.freeFrame() == null) {								
									if (algorithm.equalsIgnoreCase("FIFO")) {
										frameToReplace = frames.oldestFrame();
										if (outputType != 0) { System.out.printf("Fault, evicting from frame %d.\n", frameToReplace.id); }
										frameToReplace.replaceProcess(curProcess, curCycle);
									} else if (algorithm.equalsIgnoreCase("LRU")) {
										frameToReplace = frames.lruFrame();
										if (outputType != 0) { System.out.printf("Fault, evicting from frame %d.\n", frameToReplace.id); }
										
										frameToReplace.replaceProcess(curProcess, curCycle);
										
									} else if (algorithm.equalsIgnoreCase("RANDOM")) {
										frameToReplace = frames.randomFrame(randNums);
										if (outputType != 0) { System.out.printf("Fault, evicting from frame %d.\n", frameToReplace.id); }
										frameToReplace.replaceProcess(curProcess, curCycle);
									}
								} else {
									frameToReplace = frames.freeFrame();
									if (outputType != 0) { System.out.printf("Fault, using free frame %d.\n", frameToReplace.id); }
									frameToReplace.insertProcess(curProcess, curCycle);
								}
								frames.incResidencies();
								
							}
							//frames.showFrameStatus(); //temporary delete
							curCycle += 1;
							curProcess.nextWord(randNums);
						}
					}
				}
				
				// Remove finished processes from process queue
				for (int i = 0; i < processes.length; i++) {
					if (processes[i].references == 0) {
						if (processQueue.contains(processes[i])) {
							processQueue.remove(processes[i]);
						}
					}
				}
				
			}
		
	}
	
	// MAIN Method
	public static void main(String[] args) {
		
		// Evaluate the variable values to what is provided by the command line arguments
		if (args.length != 7) {
			System.out.println("ERROR! Improper number of arguments!");
			System.exit(0);
		} else {
			machineSize = Integer.parseInt(args[0]);
			pageSize = Integer.parseInt(args[1]);
			processSize = Integer.parseInt(args[2]);
			jobMix = Integer.parseInt(args[3]);
			rpp = Integer.parseInt(args[4]);
			algorithm = args[5];
			outputType = Integer.parseInt(args[6]);
		}
				
		// Load random numbers from file
		try {
			File randomNumsFile = new File("random-numbers.txt");
			randNums = new Scanner(randomNumsFile);
		} catch (FileNotFoundException e) { e.printStackTrace(); }
					
		// Output provided input values
		System.out.println("=====PROVIDED INFORMATION=========================");
		System.out.println("The machine size is " + machineSize + ".");
		System.out.println("The page size is " + pageSize + ".");
		System.out.println("The process size is " + processSize + ".");
		System.out.println("The job mix number is " + jobMix + ".");
		System.out.println("The number of references per process is " + rpp + ".");
		System.out.println("The replacement algorithm is " + algorithm + ".");
		System.out.println("The level of debugging output is " + outputType + ".");
		System.out.println();
			
		// Determine the number of processes, initialize array of processes
		int numProcesses = 0;
		if (jobMix == 1) { numProcesses = 1; } else { numProcesses = 4; }
		
		// Create processes array
		processes = new Process[numProcesses];
		
		// Set process IDs
		for (int i = 0; i < processes.length; i++) {
			processes[i] = new Process(i+1);
		}
				
		// Create frame table
		frames = new FrameTable();
				
		// Run the simulator
		if (outputType != 0) {
			System.out.println("=====SIMULATION===================================");
			runSimulation();
			System.out.println();
		} else {
			runSimulation();
		}
		
		// Display results
		int totalFaults = 0;
		int totalResidence = 0;
		int totalEvictions = 0;
		double avgResidency = 0;
		
		System.out.println("=====RESULTS======================================");
		
		//Individual results
		for (int i = 0; i < processes.length; i++) {
			processes[i].showResults();
			totalFaults += processes[i].numFaults;
			totalResidence += processes[i].totalResTime;
			totalEvictions += processes[i].numEvictions;
		}
		
		//Total results		
		if (totalEvictions > 0) {
			avgResidency = (double)totalResidence / (double)totalEvictions;
			System.out.printf("The total number of faults is %d and the overall average residency is %.2f.\n", totalFaults, avgResidency);
		} else {
			System.out.printf("The total number of faults is %d and the overall average residency is undefined.\n", totalFaults);
		}
		
	}

}
