import java.util.ArrayList;
import java.util.Scanner;

public class FrameTable {
	
	Frame[] frames;
	Frame lruFrame;
	Frame oldestFrame;
	boolean full;
	
	// Constructor (empty)
	public FrameTable() {
		frames = new Frame[Paging.machineSize / Paging.pageSize];
		lruFrame = null;
		oldestFrame = null;
		full = false;
		
		// Set frame IDs
		for (int i = 0; i < frames.length; i++) { frames[i] = new Frame(i); }
	}
	
	// Check if a process & page is contained in the frame table
	public int findHit(Process process) {
		
		// Report hit if found
		for (int i = 0; i < frames.length; i++) {
			if ((frames[i].processStored == process) && (frames[i].pageStored == process.curPage)) {
				return(frames[i].id);
			}
		}
		
		// Report miss
		return(-1);
		
	}
	
	// Find and return the least recently used frame
	public Frame lruFrame() {
		
		ArrayList<Integer> mostRecentUses = new ArrayList<Integer>(frames.length);
		int leastRecentlyUsed;
		
		// Obtain list of frames' most recent uses
		for (int i = 0; i < frames.length; i++) {
			mostRecentUses.add(frames[i].mostRecentUse);
		}
		
		// Sort list of frames' most recent uses, get least recently used value (first in list)
		mostRecentUses.sort(null);
		leastRecentlyUsed = mostRecentUses.get(0);
		
		// Find the least recently used frame
		for (int i = 0; i < frames.length; i++) {
			if (frames[i].mostRecentUse == leastRecentlyUsed) {
				lruFrame = frames[i];
			}
		}
		
		// Return the least recently used frame
		return(lruFrame);
		
	}
	
	// Find and return the oldest frame
	public Frame oldestFrame() {
		
		ArrayList<Integer> firstUses = new ArrayList<Integer>(frames.length);
		int oldest;
		
		// Obtain list of frames' first uses
		for (int i = 0; i < frames.length; i++) {
			firstUses.add(frames[i].firstUse);
		}
		
		// Sort list of frames' first uses, get "oldest" value (first in list)
		firstUses.sort(null);
		oldest = firstUses.get(0);
		
		// Find the "oldest" frame
		for (int i = 0; i < frames.length; i++) {
			if (frames[i].firstUse == oldest) {
				oldestFrame = frames[i];
			}
		}
		
		// Return the "oldest" frame
		return(oldestFrame);
		
	}

	// Return a random frame
	public Frame randomFrame(Scanner r) {
		return(frames[r.nextInt() % frames.length]);
	}
	
	// Increment the residencies of processes currently in a frame
	public void incResidencies() {
		for (int i = 0; i < frames.length; i++) {
			if (frames[i].free == false) {
				frames[i].processStored.curResTime += 1;
			}
		}
	}
	
	// Find and return free fame ready for insertion (return null if none exist)
	public Frame freeFrame() {
		
		ArrayList<Frame> freeFrames = new ArrayList<Frame>();
		
		// Fill list of free frames
		for (int i = frames.length-1; i >= 0; i--) {
			if (frames[i].free == true) {
				freeFrames.add(frames[i]);
			}
		}
		
		// If free frame, return frame of highest id
		// If no free frame, return null
		if (freeFrames.size() == 0 ) {
			return(null);
		} else {
			return(freeFrames.get(0));
		}
	}
	
	// Show frame status
	public void showFrameStatus() {
		for (int i = 0; i < frames.length; i++) {
			if (frames[i].free == false) {
				System.out.printf("%d(%d.%d)\t", frames[i].id, frames[i].processStored.id, frames[i].pageStored);
			}
		}
		System.out.println();
		System.out.println();
	}
	
}
