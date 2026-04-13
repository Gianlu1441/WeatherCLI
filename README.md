# WeatherCLI
A little project to explore AI prompting and AI "quirks"  for a Generation Italy course.
-------------------------------------------------------------------------------
                            WEATHER CLI - README
-------------------------------------------------------------------------------

OVERVIEW
........

This is a Spring Boot-based Command Line Interface (CLI) application designed
to provide weather forecasts with a robust offline-first approach.
It communicates with external APIs using reactive programming while maintaining
a local data safety net for when internet access is unavailable.


MAIN TECHNOLOGIES USED
......................

* Spring Boot 4.1.0-SNAPSHOT:
  The core framework for the application lifecycle and project parent.

* Spring WebFlux (WebClient):
  Utilizes WebClient for non-blocking, asynchronous HTTP requests to the
  Open-Meteo API.

* Java 17:
  The target runtime environment for the application.

* Jackson (JSON):
  Handles the transformation of API data into Java JsonNode objects and
  manages the local weather_cache.json file.

* JUnit & Mockito:
  Used in the test suite to simulate API responses and verify application
  logic under various network conditions.

* Maven Wrapper:
  Ensures the project builds with a consistent Maven version across
  different environments.


CORE FUNCTIONS
..............

* Geocoding Search:
  Retrieves precise coordinates (latitude and longitude) for a city name
  via the Open-Meteo Geocoding API.

* Weather Forecasting:
  Fetches a 4-day forecast including temperature, humidity, wind speed,
  and precipitation.

* Offline Support:
  Automatically saves successful requests to weather_cache.json. If the
  service is unreachable, it pulls historical data from this cache.

* Resilient Error Handling:
  The app handles 400/500 errors and network timeouts (5-second limit)
  gracefully without crashing, informing the user through the CLI.


QUICK START GUIDE
.................

Ensure you have Java 17 installed before beginning.

1. Build the Project
   Navigate to the project root and run the Maven Wrapper to compile:
   - Windows:      .\mvnw.cmd clean install
   - Linux/macOS:  ./mvnw clean install

2. Run the Application
   Start the CLI using the Spring Boot plugin:
   - Windows:      .\mvnw.cmd spring-boot:run
   - Linux/macOS:  ./mvnw spring-boot:run

3. Usage
   - Enter a city name (e.g., "Napoli") when prompted.
   - The app displays the forecast in a formatted ASCII table.
   - Type "exit" to close the application.

-------------------------------------------------------------------------------

      |\_/|
      | @ @   Woof!
      |   <>
      |  _/\------____
      |               \
      |      ____      |
      \____/    \____/

                           made by Gian
-------------------------------------------------------------------------------