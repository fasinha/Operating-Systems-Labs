import java.util.Scanner;

public class Process {
	
	// Process variables
	int id;
	int curWord;
	int curPage;
	int references;
	
	// Variables to track results
	int totalResTime, curResTime, curResStart, curResEnd;
	int numFaults;
	int numEvictions;
	double avgResidency;
	
	// Constructor (end)
	public Process() {
		id = -1;
		curWord = -1;
		curPage = -1;
		references = -1;
		
		totalResTime = -1;
		curResTime = -1;
		curResStart = -1;
		curResEnd = -1;
		numFaults = 0;
		numEvictions = 0;
		avgResidency = 0;
	}
	
	// Constructor (ID provided)
	public Process(int id) {
		this.id = id;
		curWord = (111 * id) % Paging.processSize;
		curPage = curWord / Paging.pageSize;
		references = Paging.rpp;
	}
	
	// Get the next word/page
	public void nextWord(Scanner r) {
		
		// Get values for A,B,C
		double A=0,B=0,C=0;
		if (Paging.jobMix == 1) {
			A=1; B=0; C=0;
		} else if (Paging.jobMix == 2) {
			A=1; B=0; C=0;
		} else if (Paging.jobMix == 3) {
			A=0; B=0; C=0;
		} else if (Paging.jobMix == 4) {
			if (id == 1) {
				A=.75; B=.25; C=0;
			} else if (id == 2) {
				A=.75; B=0; C=.25;
			} else if (id == 3) {
				A=.75; B=.125; C=.125;
			} else if (id == 4) {
				A=.5; B=.125; C=.125;
			}
		}

		// Determine current word and page
		int randNum = r.nextInt();
		double y = randNum / (Integer.MAX_VALUE + 1d);
		
		if (y < A) {
			curWord = (curWord + 1) % Paging.processSize;
		} else if (y < (A+B)) {
			curWord = (curWord - 5 + Paging.processSize) % Paging.processSize;
		} else if (y < (A+B+C)) {
			curWord = (curWord + 4) % Paging.processSize;
		} else {
			curWord = r.nextInt() % Paging.processSize;
		}
		
		curPage = curWord / Paging.pageSize;
		
	}

	// Report reference and decrement remaining references
	public void makeReference(int cycle, Scanner r) {
		if (Paging.outputType != 0) { System.out.printf("%d references word %d (page %d) at time %d: ", id, curWord, curPage, cycle); }
		if (Paging.outputType == 2) { System.out.printf("%d uses random number: %d\n", id, r.nextInt()); }
		references -=1 ;
	}
	
	// Display final individual results
	public void showResults() {
				
		if (numEvictions == 0) {
			System.out.printf("Process %d had %d faults and an undefined average residency.\n", id, numFaults);
		} else {
			avgResidency = (double)totalResTime / (double)numEvictions;
			System.out.printf("Process %d had %d faults and (%d:%d) %.2f average residency.\n", id, numFaults, totalResTime, numEvictions, avgResidency);
		}
		
	}

}
