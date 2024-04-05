#include<iostream>
#include<random>
#include<cmath>
#include<algorithm>
#include<vector>
#include<thread>
#include<mutex>
#include<numeric>
#include<chrono>
#include<set> // Include set for unique sorting

using namespace std;

mutex mtx; // Mutex for synchronizing access to shared resources

// For Generating temperature readings
int getRandomValue() {
    thread_local static random_device rd; // Obtain a random number from hardware
    thread_local static mt19937 gen(rd()); // Seed the generator
    uniform_int_distribution<> distr(-100, 70); // Define the range
    
    return distr(gen); // Generate and return the random number
}

void generateReading(int& reading) {
    int temp = getRandomValue();
    lock_guard<mutex> guard(mtx); // Lock the mutex for the scope of this object
    reading = temp;
}

class hourlyReport {
private:
    mutex mtx_lowest, mtx_highest, mtx_interval; // Fine-grained mutexes
    int current_readings[8] = {0};
    set<int> all_readings; // Store all readings to determine unique top/bottom 5 temperatures

public:
    int top_five_lowest[5];
    int top_five_highest[5];

    int highest_interval_val = -100; 
    int lowest_interval_val = 70; 
    int total_interval_distance = 0;
    int current_highest_interval = 0;

    
    hourlyReport() {}

    void generateReport() {
        int interval = 0;
        vector<int> interval_max(6, -101), interval_min(6, 71);

        for(int minute = 0; minute < 60; ++minute) {
            vector<thread> threads(8);
            for(int i = 0; i < 8; ++i) {
                threads[i] = thread(generateReading, ref(current_readings[i]));
            }

            for(auto& th : threads) {
                if(th.joinable()) {
                    th.join();
                }
            }

            cout << "Minute " << minute+1 << " Readings: ";
            for(int i = 0; i < 8; ++i) {
                cout << current_readings[i] << " ";
                all_readings.insert(current_readings[i]); // Insert reading into set
            }
            cout << endl;

            interval_max[interval] = max(interval_max[interval], *max_element(current_readings, current_readings + 8));
            interval_min[interval] = min(interval_min[interval], *min_element(current_readings, current_readings + 8));

            if((minute + 1) % 10 == 0) {
                cout << "End of interval " << interval + 1 << ": Max Temp = " << interval_max[interval]
                     << ", Min Temp = " << interval_min[interval] << endl;
                interval++;
            }
        }

        for(int i = 0; i < 6; ++i) {
            int interval_difference = interval_max[i] - interval_min[i];
            if(interval_difference > total_interval_distance) {
                total_interval_distance = interval_difference;
                current_highest_interval = i;
                highest_interval_val = interval_max[i];
                lowest_interval_val = interval_min[i];
            }
        }

        //extract the top 5 highest and lowest unique temperatures
        auto it = all_readings.begin();
        for(int i = 0; i < 5 && it != all_readings.end(); ++i, ++it) {
            top_five_lowest[i] = *it;
        }
        
        it = all_readings.end();
        for(int i = 0; i < 5 && it != all_readings.begin(); ) {
            --it; // Move iterator back
            top_five_highest[i] = *it;
            ++i; // Only increment i after setting the value
        }
    }
};

int main() {
    cout << "Hourly Report" << endl; 
    hourlyReport report;
    auto start = std::chrono::high_resolution_clock::now();
    report.generateReport();
    auto end = std::chrono::high_resolution_clock::now();
    auto duration = std::chrono::duration<double, std::milli>(end - start);
    
    std::cout << "Finished in " << duration.count() << "ms" << std::endl;

    cout << "Top Five Highest Temperatures: ";
    for(int temp : report.top_five_highest) {
        cout << temp << " ";
    }
    cout << endl;

    cout << "Top Five Lowest Temperatures: ";
    for(int temp : report.top_five_lowest) {
        cout << temp << " ";
    }
    cout << endl;

    cout << "Interval with the highest variation: Interval " << report.current_highest_interval + 1
         << " with a variation of " << report.total_interval_distance << " degrees." << endl;
}
