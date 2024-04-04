# COP_4520_Assignment3
*Compilation Instructions
-Part1
-javac Main.java --Compile
-java Main

-Part2
-g++ Part2.cpp
-./a.out

#Program 1 Evaluation
-Proof of Correctness
  *Give that their where 500,000 nodes/presents:
  * if each present where to be deleted once indicating a thank you was sent
    that would be 500,000 delete operations
  *If each present where to be inserted once that would be 500,000
  *Theoretically there could be any number of find operations depending on how much the
   minotaur wants a servant to find a present.
  *I used a uniform distribution so each operation has roughly an equal chance of occuring
  *Meaning that there is maximally around 1.5 million operations that could occur.
  *In my TaskRunner class I print out each operation that each thread does into an output.txt file
  *On average each of these text files produces 375,000 Insert delete and find operations as shown
    in the total number of lines in the files
  *Adding the total lines together for each thread we get around 1.2 million operations
  *Additionally we can verify that each value added and inserted is unique by looking at the files.
  *We can also verify that the linked list at the end of the operation is empty meaning all presents where removed
   and letters sent
-Efficency
  *Fine-Grained Locking
    The program uses fine-grained locking, where each node in the linked list has its own lock. This approach significantly reduces contention compared to coarse-grained locking (where a single lock is used for the entire data structure), allowing multiple threads to operate on different parts of the list concurrently. 
  *Atomic Operations
The AtomicBoolean and AtomicInteger classes are used for thread-safe operations without the need for synchronization blocks or locks, further reducing contention and overhead. 
  *Thread Task Divsion
  The program divides the work evenly among multiple threads, each responsible for a specific range of values. This division ensures that each thread can operate independently on different sections of the list, reducing the likelihood of contention and increasing the overall throughput of the system. By carefully allocating tasks, the program leverages parallelism to speed up processing.
-Experimental Evaluation
*The average run time for running this program on my laptop was around 2.5 minutes I have a Ryzen 7 5000 series CPU
*I ran my program on my friends computer desktop which has an intel i7 6700k CPU 4 cores 8 threads overclocked to 4.5 GHZ
where I got an average runtime of around 22 seconds.

*I also ran this program with 1 thread and noticed it was faster I assum this was because of less overhead being needed for 
allocating threads and processes associated with them.

*I ran the same program on limits that where 4,0000 40,0000 and 40,0000 respectively and examined their outputs to make
sure ranges where being spliced and handled correctly.

#Program 2 Evaluation
*Statements and proof of correctness 
Evaluation
The program utilizes thread-safe mechanisms (mutex and lock_guard) to ensure that updates to shared resources (temperature readings, top five lists, and interval calculations) are correctly synchronized across multiple threads.
The generateReading function updates the reading for each sensor in a thread-safe manner.
The updateTopFive function, called for each reading, correctly maintains a list of the top five unique temperatures, both highest and lowest, ensuring correctness in determining extreme values.
The logic to calculate the interval with the highest temperature variation  identifies the maximum difference between the highest and lowest readings in any given interval.

I Manually printed out the generated values and cross checked with an iterative program I knew did the same thing.

Thread synchronization mechanisms prevent race conditions and ensure that each reading is correctly recorded and processed.
The algorithm for maintaining the top five lists and calculating the interval with the highest variation does the same as the one in the iterative version I made which I know works.
*Efficency 
My program utilizes multi-threading to simulate concurrent temperature readings from eight different sensors. This parallelization reflects a real-world scenario where multiple data points are collected simultaneously, significantly reducing the total time required for data collection compared to a sequential approach. The efficient use of threads to perform concurrent operations demonstrates an effective reduction in the program's overall execution time as well as accurate simulation.

*Avg runtime for generating a report for one hour is about 48ms on my machine.



