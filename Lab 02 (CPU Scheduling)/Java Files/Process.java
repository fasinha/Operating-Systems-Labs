
public class Process {
	
	public int a;
	public int b;
	public int c;
	public int io;
	
	
	public int remainingCPU;
	public int remainingCPUBurst;
	public int remainingIOBurst;
	public boolean running;
	public boolean blocked;
	public int curWaitingTime;
	
	public int finishingTime;
	public int turnaroundTime;
	public int ioTime;
	public int waitingTime;
	public int processID;
	
	public String status;
	
	public Process() {
		a = 0;
		b = 0;
		c = 0;
		io = 0;
		
		remainingCPU = c;
		remainingCPUBurst = 0;
		remainingIOBurst = 0;
		running = false;
		blocked = false;
		curWaitingTime = 0;
		
		finishingTime = 0;
		turnaroundTime = 0;
		ioTime = 0;
		waitingTime = 0;
		
		
		processID = 0;
		status = "unstarted";
	}
	
	public Process(int arrivalTime, int maxCPUBurst, int cpuTime, int maxIOBurst) {
		a = arrivalTime;
		b = maxCPUBurst;
		c = cpuTime;
		io = maxIOBurst;
		processID = 0;
		
		remainingCPU = c;
		remainingCPUBurst = 0;
		remainingIOBurst = 0;
		running = false;
		blocked = false;
		curWaitingTime = 0;
		
		finishingTime = 0;
		turnaroundTime = 0;
		ioTime = 0;
		waitingTime = 0;
		
		status = "unstarted";
	}
	
	public int getA() { return(a); }
	public int getB() { return(b); }
	public int getC() { return(c); }
	public int getIO() { return(io); }
	
	public int getFTime() { return(finishingTime); }
	public int getTTime() { return(turnaroundTime); }
	public int getIOTime() { return(ioTime); }
	public int getWTime() { return(waitingTime); }
	
	public void setA(int newA) { a = newA; }
	public void setB(int newB) { b = newB; }
	public void setC(int newC) { c = newC; }
	public void setIO(int newIO) { io = newIO; }
	
	public void setFTime(int newF) { finishingTime = newF; }
	public void setTTime(int newT) { turnaroundTime = newT; }
	public void setIOTime(int newIO) { ioTime = newIO; }
	public void setWTime(int newW) { waitingTime = newW; }

	public void checkProcessComplete(int cyclesCompleted) {
		
		if (c <= cyclesCompleted) {
			status = "terminated";
		}
		
	}
	
}
