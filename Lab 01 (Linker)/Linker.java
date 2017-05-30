import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Linker {
	
	public static ArrayList symbolNames = new ArrayList();
	public static ArrayList symbolValues = new ArrayList();
	public static ArrayList symbolValues_verified = new ArrayList();
	public static ArrayList symbolUsage = new ArrayList();
	
	public static ArrayList moduleStarts = new ArrayList();
	public static ArrayList numProgramTexts = new ArrayList();
	
	public static ArrayList errors = new ArrayList();
	
	public static File fileName;
	
	public static int numModules;
	public static int moduleStart = 0;
	public static int machineSize = 0;
	
	public static int numDefinitions;
	public static int numSymbolsUsed;
	public static int numProgramText;
	
	public static String symbol;	
	public static int symbolUseLocation = 0;
	
	public static String wordType;
	public static int wordAddress;

	
	static void firstPass() {
		try {
			Scanner fileInput = new Scanner(fileName);
			
			numModules = fileInput.nextInt();
			
			
			for (int module = 0; module < numModules; module++) {
				
				moduleStarts.add(moduleStart); //Add module start to array list
				
				// Add definitions to symbol table
				numDefinitions = fileInput.nextInt();
					for (int i = 0; i < numDefinitions; i++) {
						
						String symbolName = fileInput.next();
						int symbolValue = fileInput.nextInt();
						
						if (symbolNames.contains(symbolName)) {
							errors.add("MULTIPLY DEFINED VARIABLE (" + symbolName + "): Initial value used.");
						} else {
							symbolNames.add(symbolName);
							symbolValues.add(symbolValue + moduleStart);
							symbolValues_verified.add(false); //Unverified by default
							symbolUsage.add(false); //Unused by default
						}

					}
				
				// Jump through usage list
				numSymbolsUsed = fileInput.nextInt();
					for (int i = 0; i < numSymbolsUsed; i++) {
						symbol = fileInput.next();
						
						do {
							symbolUseLocation = fileInput.nextInt();
						} while (symbolUseLocation != -1);
						
					}
				
				
				// Get the amount of program texts in the module
				numProgramText = fileInput.nextInt();
				
				
				// Jump through program text, track starting position for next module
				numProgramTexts.add(numProgramText); //Add # program text to arraylist
				moduleStart += numProgramText; //Sets start location for the next module
					for (int i = 0; i < numProgramText; i++) {
						wordType = fileInput.next();
						wordAddress = fileInput.nextInt();
					}
				
			}
				fileInput.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	
	static void secondPass() {
				
		try {
			Scanner fileInput = new Scanner(fileName);
			
			numModules = fileInput.nextInt();
						
			for (int module = 0; module < numModules; module++) {
				
				//Display Module Start
				System.out.println("+" + moduleStarts.get(module));
						
				// Skip Symbol Table Definitions (already known)
				numDefinitions = fileInput.nextInt();
					for (int i = 0; i < numDefinitions; i++) {
						fileInput.next();
						fileInput.nextInt();
					}
				
				// Generate array for current module
				// Values (default 0) will be added in programText stage
				numProgramText = (int)numProgramTexts.get(module);
				int[] curProgramText = new int[numProgramText];
				
				for (int i = 0; i < numProgramText; i++) {
					curProgramText[i] = 0;
				}
				
				// Determine values to be added in program text
				numSymbolsUsed = fileInput.nextInt();
				
				for (int i = 0; i < numSymbolsUsed; i++) {
					symbol = fileInput.next();
					
					do {
						symbolUseLocation = fileInput.nextInt();
						
						if (symbolUseLocation > curProgramText.length) {
							errors.add("IMPROPER USAGE: " + symbol + " use in module " + i+ 1 + " at address outside of range of module. Use ignored.");
						} else if (symbolUseLocation != -1) {
							
							if (symbolNames.indexOf(symbol) == -1) {
								errors.add("IMPROPER VARIABLE USE: " + symbol + " is used but not defined. Zero used.");
							} else {
								
								if (curProgramText[symbolUseLocation] == 0) {
									curProgramText[symbolUseLocation] = (int) symbolValues.get(symbolNames.indexOf(symbol));
									symbolUsage.set(symbolNames.indexOf(symbol), true);
								} else {
									errors.add("MULTIPLE VARIABLES IN INSTRUCTION: Only the first will be used.");
								}

							}
							
						}
						
						
					} while (symbolUseLocation != -1);
					
				}
					
									
				// Update relative addresses/external references
				numProgramText = fileInput.nextInt();
				
				
				for (int i = 0; i < numProgramText; i++) {
					wordType = fileInput.next();
					wordAddress = fileInput.nextInt();
					
					System.out.print("\t" + i + ":\t" + wordType + " " + wordAddress);
					
					if (wordType.equalsIgnoreCase("R")) {
						
						if ((wordAddress % 1000) > numProgramText) {
							errors.add("IMMPROPER RELATIVE ADDRESS: Relative address (" + (wordAddress % 1000) + ") greater than module size (" + numProgramText + "). Zero used.");
							wordAddress -= (wordAddress % 1000);
							System.out.println("\t  ERROR\t\t" + wordAddress);
						} else {
							wordAddress += moduleStart;
							System.out.println(" + " + moduleStart + "\t\t" + wordAddress);
						}
						
					} else if (wordType.equalsIgnoreCase("E")) {
						wordAddress -= wordAddress % 1000;
						wordAddress += (int) curProgramText[i];
						
						if (symbolValues.indexOf(curProgramText[i]) != -1) {
							System.out.println(" -> " + symbolNames.get(symbolValues.indexOf(curProgramText[i])) + "\t\t" + wordAddress);
						} else {
							System.out.println(" -> " + "ERROR\t\t" + wordAddress);
						}
					} else if (wordType.equalsIgnoreCase("A")) {
						if ((wordAddress % 1000) > machineSize) {
							errors.add("IMPROPER ABSOLUTE ADDRESS: Absolute address (" + (wordAddress % 1000) + ") greater than machine size (" + machineSize + "). Zero used.");
							wordAddress -= (wordAddress % 1000);
							System.out.println("\t  ERROR\t\t" + wordAddress);
						} else {
							System.out.println("\t\t\t" + wordAddress);
						}
						
					} else {
						System.out.println("\t\t\t" + wordAddress);
					}
				}
				
				moduleStart += numProgramText; //Sets start location for the next module
				
			}
				fileInput.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}

	
	public static void main(String[] args) {
		
		try {
			
			// GET FILE NAME
			Scanner userInput = new Scanner(System.in);
			
			if (args.length == 0) {
				System.out.println("Enter file name:");
				fileName = new File(userInput.nextLine());
				System.out.println();
			} else {
				fileName = new File(args[0]);
			}
			
			//fileName = new File("/Users/JustinMason/Desktop/test.txt");
			
			userInput.close();
			
			// FIRST PASS
			firstPass();
			
			// DISPLAY SYMBOL TABLE
			System.out.println("=====SYMBOL TABLE=====");
			for (int i = 0; i < symbolNames.size(); i++) {
				System.out.println(symbolNames.get(i) + " = " + symbolValues.get(i));
			}
			System.out.println();
				
			// SECOND PASS
			System.out.println("=====MEMORY MAP=====");
			machineSize = moduleStart;
			moduleStart = 0;
			wordAddress = 0;
			secondPass();
			System.out.println();
			
			// DETERMINE UNUSED VARIABLE ERROR
			for (int i = 0; i < symbolUsage.size(); i++) {
				if (symbolUsage.get(i).equals(false)) {
					errors.add("UNUSED VARIABLE: " + symbolNames.get(i) + " is defined but unused.");
				}
			}
			
			// DISPLAY DISCOVERED ERRORS
			if (errors.size() != 0) {
				System.out.println("=====ERRORS/ISSUES=====");
				for (int i = 0; i < errors.size(); i++) {
					System.out.println(errors.get(i));
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
