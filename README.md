# CrossBrowserTest
This project is designed to run cross-browser tests using Selenium WebDriver with support for Chrome and Firefox browsers. The tests are executed on a Selenium Grid running locally, and the project includes features like logging, taking screenshots, and handling Docker logs.

## Features
Cross-Browser Support: The test can be executed on Chrome and Firefox browsers using Selenium WebDriver.
Logging: Captures browser logs and saves them to a file.
Screenshot Capture: Takes screenshots at different points of the test and stores them in a folder.
Docker Log Retrieval: Captures Docker container logs for the Selenium Grid running in containers.
Google Search Test: Performs a Google search and verifies the page title contains "Apple".

## Prerequisites
Java 11 or later
Maven for dependency management
Selenium Grid running locally or in Docker containers
Docker (optional, for retrieving logs from Docker containers)
### Setup
1. Clone the Repository
Clone this repository to your local machine:
git clone https://github.com/your-username/CrossBrowserTest.git

2. Install Dependencies
This project uses Maven for dependency management. Ensure Maven is installed on your system and run:
mvn install

4. Start Selenium Grid
Ensure that you have Selenium Grid set up and running. You can use Docker to run Selenium Hub and Nodes (Chrome and Firefox):
docker-compose up -d
Alternatively, you can manually start Selenium Hub and Node containers.

5. Configure Browser for Testing
You can specify the browser to be used in the test by passing a parameter during test execution. This can be done using Maven or by modifying the configuration.
Example Maven Command
To run the test on Chrome:
mvn test -Dbrowser=chrome

To run the test on Firefox:
mvn test -Dbrowser=firefox

5. Running Tests
You can run the tests with Maven using the following command:
mvn test

7. Viewing Logs and Screenshots
Logs: The browser logs are saved in the logs/ directory, with each browser having its own log file (e.g., chrome.log or firefox.log).
Screenshots: Screenshots are saved in the screenshots/ directory. Files are named according to the browser and test case, for example, chrome_google_search1.png.

9. Docker Log Retrieval
If you're using Docker to run the Selenium Grid, the test will capture Docker logs of the running containers. The following containers' logs will be retrieved:

selenium-chrome
selenium-firefox
You can replace these container names in the code if they differ in your setup.

Test Case - Google Search
The test case (testGoogleSearch) simulates a Google search:

Navigates to https://www.google.com.
Accepts cookies (if the "Accept all" button appears).
Performs a search for "Apple".
Verifies the page title contains the word "Apple".
Takes screenshots during various stages of the test.
Code Explanation
1. setUp Method
The setUp method is executed before any tests. It initializes the WebDriver based on the specified browser (Chrome or Firefox) and sets up logging capabilities for both browsers.

2. captureBrowserLogs Method
After each test, the captureBrowserLogs method is called to capture browser logs and save them to a file for further analysis.

3. captureDockerLogs Method
This method retrieves logs from the Docker containers running the Selenium Grid.

4. testGoogleSearch Method
This is the main test case, which navigates to Google, searches for "Apple," and verifies the title. It also takes screenshots during the execution.

5. takeScreenshot Method
This method captures screenshots of the browser during the test execution and saves them to the screenshots/ directory.

6. tearDown Method
After the tests, the tearDown method quits the WebDriver and captures Docker logs.

