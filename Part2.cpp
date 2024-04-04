#include<iostream>
#include<random>
#include<cmath>
#include<algorithm>
#include<vector>
#include<thread>
#include<mutex>
#include<numeric>

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
    int current_readings[8] = {0};

    void updateTopFive(int newVal, int* topFive, bool isLowest = true) {
        lock_guard<mutex> guard(mtx); // Ensure exclusive access to topFive arrays
        vector<int> values(topFive, topFive + 5);

        // Remove if already exists to ensure uniqueness
        values.erase(remove(values.begin(), values.end(), newVal), values.end());

        // Add new value
        values.push_back(newVal);

        // Sort based on whether we're looking for highest or lowest
        if (isLowest) {
            sort(values.begin(), values.end()); // Ascending for lowest
        } else {
            sort(values.rbegin(), values.rend()); // Descending for highest
        }

        // Keep only unique top 5
        values.erase(unique(values.begin(), values.end()), values.end());
        if (values.size() > 5) {
            values.resize(5);
        }

        // Copy back
        copy(values.begin(), values.end(), topFive);
    }

public:
    int top_five_lowest[5];
    int top_five_highest[5];

    int highest_interval_val = -101;
    int lowest_interval_val = 71;
    int total_interval_distance = 0;
    int current_highest_interval = 0;

    // Class constructor to initialize arrays with distinct values
    hourlyReport() {
        iota(begin(top_five_lowest), end(top_five_lowest), 66); // Initializes with ascending unique dummy values
        iota(rbegin(top_five_highest), rend(top_five_highest), -104); // Initializes with descending unique dummy values
    }

    void generateReport() {
        int interval = 0;
        //one iteration = one minute
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

            for(int i = 0; i < 8; ++i) {
                updateTopFive(current_readings[i], top_five_lowest);
                updateTopFive(current_readings[i], top_five_highest, false);
            }

            int minVal = *min_element(current_readings, current_readings + 8);
            int maxVal = *max_element(current_readings, current_readings + 8);
            if(maxVal - minVal > total_interval_distance) {
                total_interval_distance = maxVal - minVal;
                current_highest_interval = interval;
                highest_interval_val = maxVal;
                lowest_interval_val = minVal;
            }

            if((minute + 1) % 10 == 0) {
                interval++;
            }
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


    cout << "Interval with the highest variation: " << report.current_highest_interval
         << " with a variation of " << report.total_interval_distance << endl;
}
