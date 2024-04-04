import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;


class Node<T> {
   T item;
   int key;
   Node<T> next;
   Lock lock = new ReentrantLock();

    // Node constructor
   Node(T item) {
      this.item = item;
      this.key = (item != null) ? item.hashCode() : 0; 
   }

   void lock() {
      lock.lock();
   }

   void unlock() {
      lock.unlock();
   }
}


//Functions inspired by logic from CH8 of the textbook
//The art of multiprocess programming MAurice Herlihy and Nir Shavit

class LinkedList<T> {
   private Node<T> head;

   public LinkedList() {
      // Initialize the list with a dummy head and dummy tail
      head = new Node<>(null); // Dummy head
      head.key = Integer.MIN_VALUE;

      // Create a dummy tail node with max value
      Node<T> tail = new Node<>(null); // Dummy tail
      tail.key = Integer.MAX_VALUE;

      head.next = tail; // Connect head to tail
   }

   public boolean add(T item) {
      int key = (item != null) ? item.hashCode() : 0;
      head.lock();
      Node<T> pred = head;
      try {
         Node<T> curr = pred.next;
         curr.lock();
         try {
            while (curr.key < key) {
               pred.unlock();
               pred = curr;
               curr = curr.next;
               curr.lock();
            }
            if (curr.key == key) {
               return false;
            }
            Node<T> newNode = new Node<>(item);
            newNode.next = curr;
            pred.next = newNode;
            return true;
         } finally {
            curr.unlock();
         }
      } finally {
         pred.unlock();
      }
   }

   public boolean remove(T item) {
      int key = (item != null) ? item.hashCode() : 0;
      head.lock();
      Node<T> pred = head;
      try {
         Node<T> curr = pred.next;
         curr.lock();
         try {
            while (curr.key < key) {
               pred.unlock();
               pred = curr;
               curr = curr.next;
               curr.lock();
            }
            if (curr.key == key) {
               pred.next = curr.next;
               return true;
            }
            return false;
         } finally {
            curr.unlock();
         }
      } finally {
         pred.unlock();
      }
   }

   //Verifies value
   public boolean validate(Node<T> pred, Node<T> curr) {
      Node<T> node = head;
      while (node.key <= pred.key) {
          if (node == pred)
              return pred.next == curr;
          node = node.next;
      }
      return false;
  }

  //Traverses using a temporary node set to the head then it enters a loop to traverse a list
   public boolean contains(T item) {
      int key = item.hashCode();
      head.lock();
      Node<T> pred = head; 
      try {
          Node<T> curr = pred.next;
          curr.lock();
          try {
              while (curr.key < key) {
                  pred.unlock();
                  pred = curr;
                  curr = curr.next;
                  curr.lock();
              }
              return curr.key == key;
          } finally { // always unlock
              curr.unlock();
          }
      } finally {
          pred.unlock();
      }
  }
  




   public void printList() {
      Node<T> temp = head.next; // Start with the first real node, skipping the dummy head
      while (temp.key != Integer.MAX_VALUE) { // Stop before the dummy tail node
         System.out.print(temp.item + " -> ");
         temp = temp.next;
      }
      System.out.println("null");
   }
}



  class TaskRunner {

    private final LinkedList<Integer> list;

    //Defining Thread Range of operation
    private final int startRange; 
    private final int endRange;


    private final Random random = new Random();

    //Boolean to check if all values within scope have been removed (Letters sent)
    private final AtomicBoolean allRemoved = new AtomicBoolean(false);


    //Keeps track of the amount of inserts and deletions in a range
    private static AtomicInteger totalInserts = new AtomicInteger(0);
    private static AtomicInteger totalDeletes = new AtomicInteger(0);
    
    //For printing each threads output
    private PrintWriter printWriter;

    public TaskRunner(LinkedList<Integer> list, int start, int end, AtomicInteger totalInserts, AtomicInteger totalDeletes, String outputFileName) throws FileNotFoundException {
        this.list = list;
        this.startRange = start;
        this.endRange = end;
        TaskRunner.totalInserts = totalInserts;
        TaskRunner.totalDeletes = totalDeletes;
        this.printWriter = new PrintWriter(new File(outputFileName));
    }


    //Task for Servant to complete 
    public void runTask() {
        //defining threads operable range
        int startChunk = startRange / 1000;
        int endChunk = endRange / 1000;

        HashSet<Integer> addedValues = new HashSet<>();
        HashSet<Integer> removedValues = new HashSet<>();

        //Moving in intervals
        for (int chunk = startChunk; chunk <= endChunk; chunk++) {
            int startValue = chunk * 1000 + 1;
            int endValue = (chunk + 1) * 1000;

            //Randomly chooses to add delete or find a value
            //Simulates alternating operations
            while (addedValues.size() < 1000 || removedValues.size() < 1000) {
                int operation = random.nextInt(3) + 1;
                int value = startValue + random.nextInt(endValue - startValue + 1);

                switch (operation) {
                    case 1: // Add a present 
                        if (addedValues.add(value)) {
                            list.add(value);
                            printWriter.println("Added value: " + value);
                            totalInserts.incrementAndGet();
                        }
                        break;
                    case 2: // Remove a present /send a thank you
                        if (addedValues.contains(value) && list.remove(value)) {
                            removedValues.add(value);
                            printWriter.println("Removed value: " + value);
                            totalDeletes.incrementAndGet();
                        }
                        break;
                    case 3: // Search for a present as per minotaurs request
                        if (list.contains(value)) {
                            printWriter.println("List contains: " + value);
                        }
                        break;
                }
            }

            addedValues.clear();
            removedValues.clear();
        }

        allRemoved.set(true);
        closeWriter();
    }

    public void closeWriter() {
        if (printWriter != null) {
            printWriter.close();
        }
    }
}
public class Main {
    private static final AtomicInteger totalInserts = new AtomicInteger(0);
    private static final AtomicInteger totalDeletes = new AtomicInteger(0);
    private static final int limit = 500000; // Total number of presents
    private static final int NUM_THREADS = 4; // Number of threads
    private static final int milestone = limit / 5; // Milestone for progress reporting
    private static volatile int nextMilestone = milestone; // Track the next milestone

    public static void main(String[] args) throws FileNotFoundException  {
        LinkedList<Integer> sharedList = new LinkedList<>();
        int sectionSize = limit / NUM_THREADS; // Divide work among threads
        long startTime = System.currentTimeMillis();
        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < threads.length; i++) {
            int start = i * sectionSize;
            int end = (i + 1) * sectionSize - 1;
            if (i == NUM_THREADS - 1) {
                end = limit - 1; // Adjust for the last section
            }
            TaskRunner runner = new TaskRunner(sharedList, start, end, totalInserts, totalDeletes, "output" + i + ".txt");
            threads[i] = new Thread(runner::runTask);
            threads[i].start();
        }

        // Progress checking and reporting loop
        new Thread(() -> {
            while (nextMilestone <= limit) {
                int combinedTotal = totalInserts.get() + totalDeletes.get();
                if (combinedTotal >= nextMilestone) {
                    System.out.println("Progress: Reached " + nextMilestone + " operations.");

                    nextMilestone += milestone;
                }
                try {
                    Thread.sleep(100); // Check every 100 milliseconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();

        // Wait for all threads to complete
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.err.println("A thread didn't finish correctly.");
            }
        }

        // Final reporting
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed Time is " + elapsedTime + " milliseconds");
        System.out.println("All tasks completed. Total Presents Added: " + totalInserts.get() + ", Total Letters Sent: " + totalDeletes.get());
        System.out.println("LinkedList Contents(should be empty:)");
        sharedList.printList(); // Should be empty
    }
}
