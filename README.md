# Weather CLI
A project developed to explore AI prompting and potential during the Generation Italy course.

---

## Overview
**Weather CLI** is a **Spring Boot** application designed to provide weather forecasts with an *offline-first* approach. The app communicates with external APIs using reactive programming while maintaining a local "safety net" for data in case internet access is unavailable.

## Main Technologies
* **Spring Boot 4.1.0-SNAPSHOT**: Core framework for the application lifecycle.
* **Spring WebFlux (WebClient)**: Used for asynchronous and non-blocking HTTP requests to the Open-Meteo API.
* **Java 17**: Target runtime environment.
* **Jackson (JSON)**: Handles the transformation of API data into Java `JsonNode` objects and manages the `weather_cache.json` file.
* **JUnit & Mockito**: Used to simulate API responses and verify logic under various network conditions.
* **Maven Wrapper**: Ensures consistent builds across different environments.

## Core Functions
* **Geocoding Search**: Retrieves precise coordinates (latitude and longitude) via the Open-Meteo API.
* **Weather Forecasts**: Provides a 4-day forecast including temperature, humidity, wind speed, and precipitation.
* **Offline Support**: Automatically saves successful requests to the `weather_cache.json` file. If the service is unreachable, it retrieves historical data from the cache.
* **Resilient Error Handling**: Manages 400/500 errors and timeouts (5-second limit), informing the user without interrupting the application.

## Quick Start Guide
Ensure you have **Java 17** installed.

### 1. Build the Project
Navigate to the root folder and run the Maven Wrapper:
* **Windows**: `.\mvnw.cmd clean install`
* **Linux/macOS**: `./mvnw clean install`

### 2. Execution
Launch the CLI via the Spring Boot plugin:
* **Windows**: `.\mvnw.cmd spring-boot:run`
* **Linux/macOS**: `./mvnw spring-boot:run`

### 3. Usage
* Enter the name of a city (e.g., "Naples") when prompted.
* View the weather in a formatted ASCII table.
* Type `exit` to close the application.

---

```text
      |\_/|
      | @ @   Woof!
      |  <>
      |  _/\------____
      |               \
      |      ____      |
      \____/    \____/

               made by Gian