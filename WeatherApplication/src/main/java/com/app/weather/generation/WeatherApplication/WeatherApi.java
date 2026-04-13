package com.app.weather.generation.WeatherApplication;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;

 @Component
public class WeatherApi {

    private final WebClient webClient = WebClient.create();


    /**
     * Searches for a city and gets its coordinates (Latitude/Longitude).
     * * HOW IT HANDLES ERRORS:
     * 1. If the server says "Error" (400 or 500), it prints a message and stops.
     * 2. If the server takes more than 5 seconds, it gives up (Timeout).
     * 3. In any error case, it returns NULL instead of crashing your app.
     *
     * @param city The name of the city (e.g., "Napoli")
     * @return The data as a JsonNode, or null if something went wrong.
     */
    public JsonNode getCoordinates(String city) {
        return webClient.get()
                .uri("https://geocoding-api.open-meteo.com/v1/search?name={city}&count=1&language=it", city)
                .retrieve()
                // Handle HTTP 400/500 errors
                .onStatus(status -> status.isError(), response -> {
                    System.out.println("Error connection to the API: Server returned " + response.statusCode());
                    return Mono.error(new RuntimeException("API Error"));
                })
                .bodyToMono(JsonNode.class)
                .timeout(Duration.ofSeconds(5)) // Handle Timeout
                .onErrorResume(e -> {
                    // Catch timeouts or the RuntimeException from above
                    System.out.println("Error connection to the API: " + e.getMessage());
                    return Mono.empty(); // Return empty so .block() returns null
                })
                .block(); // Finally block to return JsonNode
    }

    public JsonNode getWeather(double lat, double lon) {
        return webClient.get()
                .uri("https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}" +
                        "&daily=temperature_2m_max,relative_humidity_2m_max,wind_speed_10m_max,precipitation_sum" +
                        "&forecast_days=4&timezone=auto", lat, lon)
                .retrieve()
                .onStatus(status -> status.isError(), response -> {
                    System.out.println("Error connection to the API: Weather service error.");
                    return Mono.error(new RuntimeException("API Error"));
                })
                .bodyToMono(JsonNode.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> {
                    System.out.println("Error connection to the API: " + e.getMessage());
                    return Mono.empty();
                })
                .block();
    }
}
