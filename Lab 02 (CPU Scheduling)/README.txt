Justin Mason
Operating Systems
Professor Allan Gottlieb
New York University
Spring 2017

=====ASSIGNMENT===========
This lab “simulate[s] scheduling in order to see how the time required depends on the scheduling algorithm and the request patterns.”

(For the full assignment description see file “Lab 02 Description.pdf”)

=====JAVA FILES===========
The following Java files are involved in the lab:
	Process.java, Scheduler.java

=====“RANDOM” NUMBERS=====
Professor Gottlieb’s lab assignments provide students with inputs to check and verify appropriate values. For this reason, random values cannot be truly random or results are unverifiable. The static list of random numbers that the program will come up with is stored in the file “random-numbers.txt”.

=====NECESSARY INPUT======
This program requires a one command line argument in order to run. This value (file name) allows the program to search for a file with the provided name that contains the processes to simulate. Examples of such files are stored in the folder “sample_files”.

At program initialization, the user will be prompted to decide which algorithm to perform. If the provided input is outside the bounds of acceptable input (stated in the prompt), the program will run the first come, first served (FCFS) algorithm by default.

The program will only perform one scheduling simulation before termination. To test various inputs/scheduling algorithms, simply re-run the program.

=====OUTPUT VERSIONS======
There are multiple versions of output produced by this lab. A simplified output that only produces the simulation results, and a detailed output that is produced when multiple command line arguments are provided (the first will be interpreted as the filename.