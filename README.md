# COP_4520_Assignment3

## Compilation Instructions

### Part 1
- Compile with: `javac Main.java`
- Run with: `java Main`

### Part 2
- Compile with: `g++ Part2.cpp`
- Run with: `./a.out`

## Program 1 Evaluation

### Proof of Correctness
- Given that there were 500,000 nodes/presents:
  - If each present were to be deleted once (indicating a thank you was sent), that would be 500,000 delete operations.
  - If each present were to be inserted once, that would be 500,000 insertions.
  - Theoretically, there could be any number of find operations depending on the minotaur's requests.
  - A uniform distribution ensures each operation has roughly an equal chance of occurring, leading to around 1.5 million operations potentially.
  - The `TaskRunner` class outputs each operation that each thread does into an `output.txt` file. On average, each file contains 375,000 insert, delete, and find operations.
  - Summing the total lines for each thread results in around 1.2 million operations.
  - The uniqueness of added and inserted values can be verified by examining the files, and the emptiness of the linked list at the end confirms that all presents were removed and letters sent.

### Efficiency
- **Fine-Grained Locking**: Each node in the linked list has its own lock, significantly reducing contention and allowing multiple threads to operate concurrently on different list parts.
- **Atomic Operations**: Uses `AtomicBoolean` and `AtomicInteger` for thread-safe operations without additional synchronization blocks or locks.
- **Thread Task Division**: Work is evenly divided among threads, each responsible for a specific value range, which reduces contention and increases throughput by leveraging parallelism.

### Experimental Evaluation
- **Runtime**: Average runtime on a Ryzen 7 5000 series CPU was around 2.5 minutes. On an Intel i7 6700k CPU (4 cores, 8 threads, overclocked to 4.5 GHz), the average runtime was about 22 seconds.
- **Single Thread Performance**: Running the program with 1 thread was faster, likely due to reduced overhead from managing fewer threads.
- **Range Handling**: Tests with limits of 4,000; 40,000; and 400,000 were conducted to ensure correct range splicing and handling.

## Program 2 Evaluation

### Statements and Proof of Correctness
- **Thread Safety**: Utilizes mutexes to protect shared resources, ensuring safe concurrent modifications with `lock_guard` for automatic unlocking.
- **Accurate Temperature Collection**: Simulates concurrent temperature measurements across multiple threads, reflecting a real-world multi-sensor scenario.
- **Data Aggregation**: Utilizes a `set` for temperature readings to ensure uniqueness in the top and bottom 5 temperatures.
- **Interval Calculation**: Calculates the 10-minute interval with the highest temperature difference by comparing max and min temperatures within each interval.

### Efficiency
- **Concurrency**: Uses 8 threads to simulate sensor readings, offering faster data processing compared to a single-threaded approach.
- **Minimized Lock Contention**: A single global mutex reduces contention, with a short critical section for variable assignment reducing potential bottlenecks.
- **Optimized Data Structures**: Efficient operations with a `set` for unique temperatures and vectors for interval calculations.

### Experimental Evaluation
- **Setup**: Tested on a multicore CPU with 8 cores, simulating an hour of temperature readings.
- **Metrics**: Focused on execution time, CPU utilization, and memory usage.
- **Results**:
  - **Execution Time**: Completed the simulation in an average of 40ms, indicating high efficiency.
  - **CPU Utilization**: Achieved optimal distribution across all cores, highlighting effective concurrency use.
  - **Memory Usage**: Maintained stable memory consumption, efficiently managing data storage and processing.
