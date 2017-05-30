
public class Frame {
	
	int id;
	boolean free;
	int firstUse;
	int mostRecentUse;
	Process processStored;
	int pageStored;
	
	// Constructor (empty)
	public Frame() {
		id = -1;
		free = true;
		firstUse = -1;
		mostRecentUse = -1;
		processStored = null;
		pageStored = -1;
	}
	
	// Constructor (ID provided)
	public Frame(int id) {
		this.id = id;
		free = true;
		firstUse = -1;
		mostRecentUse = -1;
		processStored = null;
		pageStored = -1;
	}
	
	// Insert a process into the free frame
	public void insertProcess(Process process, int cycle) {
		free = false;
		firstUse = cycle;
		mostRecentUse = cycle;
		processStored = process;
		pageStored = process.curPage;
	}
	
	// Replace the process in the frame
	public void replaceProcess(Process newProcess, int cycle) {
				
		processStored.curResEnd = cycle;
		//processStored.curResTime = processStored.curResEnd - processStored.curResStart; //delete?
		processStored.totalResTime += processStored.curResTime;
		processStored.curResTime = 0;
		processStored.numEvictions += 1;
		
		free = false;
		firstUse = cycle;
		mostRecentUse = cycle;
		processStored = newProcess;
		pageStored = newProcess.curPage;
		
		newProcess.curResStart = cycle;
	}

}
