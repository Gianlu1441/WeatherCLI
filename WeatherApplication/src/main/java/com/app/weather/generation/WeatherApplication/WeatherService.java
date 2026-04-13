package com.app.weather.generation.WeatherApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

@Service
public class WeatherService implements CommandLineRunner {

    private final WeatherApi weatherApi;
    private final ApplicationContext context;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File cacheFile = new File("weather_cache.json");

    public WeatherService(WeatherApi weatherApi, ApplicationContext context) {
        this.weatherApi = weatherApi;
        this.context = context;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Spring Boot Weather CLI (with Auto-Update Cache) ===");

        while (true) {
            System.out.print("\nEnter city name (or 'exit'): ");
            String input = scanner.nextLine().trim();

            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Shutting down...");
                break; // Breaks the loop
            }
            if (input.isEmpty()) continue;

            processWeatherRequest(input);
        }

        // 4. Force Spring to exit after the loop finishes
        System.exit(SpringApplication.exit(context, () -> 0));
    }


    private void processWeatherRequest(String city) {
        String cacheKey = city.toLowerCase();
        JsonNode cachedData = getFromCache(cacheKey);

        // --- VALIDATION LOGIC ---
        if (cachedData != null) {
            // Get the first date string from the cached data (e.g., "2026-04-12")
            String cachedDateStr = cachedData.get("daily").get("time").get(0).asText();
            LocalDate cachedDate = LocalDate.parse(cachedDateStr);
            LocalDate today = LocalDate.now();

            if (cachedDate.isBefore(today)) {
                System.out.println("🔄 [CACHE EXPIRED] Data is from " + cachedDateStr + ". Fetching updates...");
                // We don't return here, so it falls through to the API call
            } else {
                System.out.println("📦 [CACHE HIT] Data is up to date.");
                printFormattedTable(city, cachedData, true);
                return;
            }
        }

        // 2. Fetch fresh data if cache was missing OR expired
        JsonNode geoData = weatherApi.getCoordinates(city);
        if (geoData == null || !geoData.has("results")) {
            System.out.println("❌ City not found.");
            return;
        }

        JsonNode location = geoData.get("results").get(0);
        double lat = location.get("latitude").asDouble();
        double lon = location.get("longitude").asDouble();
        String cityName = location.get("name").asText();

        System.out.printf("🌐 [API CALL] Fetching fresh forecast for %s...%n", cityName);
        JsonNode weatherData = weatherApi.getWeather(lat, lon);

        if (weatherData != null) {
            saveToCache(cacheKey, weatherData);
            printFormattedTable(cityName, weatherData, false);
        }
    }

    // saveToCache and getFromCache remain the same as your previous version
    private void saveToCache(String city, JsonNode data) {
        try {
            ObjectNode root = (cacheFile.exists() && cacheFile.length() > 0)
                    ? (ObjectNode) objectMapper.readTree(cacheFile)
                    : objectMapper.createObjectNode();

            root.set(city, data);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(cacheFile, root);
        } catch (Exception e) {
            System.out.println("⚠️ Cache Save Error: " + e.getMessage());
        }
    }

    private JsonNode getFromCache(String city) {
        try {
            if (!cacheFile.exists() || cacheFile.length() == 0) return null;
            return objectMapper.readTree(cacheFile).get(city);
        } catch (Exception e) {
            return null;
        }
    }

    private void printFormattedTable(String cityName, JsonNode data, boolean isFromCache) {
        JsonNode daily = data.get("daily");
        JsonNode dates = daily.get("time");

        String separator = "---------------------------------------------------------------------------------------";
        System.out.println("\n" + separator);

        System.out.printf("%-15s |", cityName.toUpperCase());
        for (int i = 0; i < dates.size(); i++) {
            String dateLabel = dates.get(i).asText().substring(5);
            if (!isFromCache && i == 0) dateLabel = "Today";
            System.out.printf(" %-12s |", dateLabel);
        }
        System.out.println("\n" + separator);

        printRow("Temp (Max)", daily.get("temperature_2m_max"), "°C");
        printRow("Humidity", daily.get("relative_humidity_2m_max"), "%");
        printRow("Precipitation", daily.get("precipitation_sum"), "mm");
        printRow("Wind Speed", daily.get("wind_speed_10m_max"), "km/h");
        System.out.println(separator);
    }

    private void printRow(String label, JsonNode array, String unit) {
        System.out.printf("%-15s |", label);
        for (JsonNode node : array) {
            System.out.printf(" %-12s |", node.asText() + unit);
        }
        System.out.println();
    }
}
