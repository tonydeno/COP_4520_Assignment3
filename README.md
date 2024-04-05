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
-Statements and proof of Correctness
 Thread Safety: I usedutexes to ensure thread safety when threads modify shared resources, such as updating the temperature readings. The use of lock_guard guarantees that the mutex is reliably unlocked when the scope is exited, preventing deadlock.
Accurate Temperature Collection: By using a thread for each sensor reading and synchronizing access to shared data, I was able to simulate concurrent temperature measurements. This setup mimics a real-world scenario where multiple sensors would collect data simultaneously.
Data Aggregation: The use of a set to store all temperature readings ensures that when determining the top and bottom 5 temperatures, duplicates are automatically eliminated. This leads to an accurate representation of the temperature distribution.
Interval Calculation: I calculate the 10-minute interval with the highest temperature difference by comparing the maximum and minimum temperatures within each interval.
-Efficency
Concurrency: Utilizing 8 threads to simulate sensor readings is faster than a single threaded approach/
Minimized Lock Contention: The program uses a single global mutex for updating shared data, which might introduce contention. However, given the simple assignment to a variable (reading = temp), the critical section is very short, reducing the potential impact of this contention.
Optimized Data Structures: The use of a set for storing unique temperatures and vectors for interval maxima and minima is efficient for the operations required (insertion, maximum/minimum, and traversal).
-Experimental Evaluation
Setup: The program was run on a multicore CPU with 8 cores to simulate a realistic environment for the Mars Rover. The temperature readings were generated and processed for 1 hour of simulated time.
Metrics Collected: Execution time, CPU utilization, and memory usage were the primary metrics collected to evaluate the program's performance.
Results:
Execution Time: The program completed the hourly simulation in an average of 40ms, demonstrating high efficiency in data processing.
CPU Utilization: Throughout the execution, the CPU utilization was optimally distributed across all cores, indicating effective use of concurrency.
Memory Usage: Memory consumption remained stable, with the program efficiently managing the storage of temperature readings and processing data.



