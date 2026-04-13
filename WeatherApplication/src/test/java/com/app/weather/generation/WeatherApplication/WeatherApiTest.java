package com.app.weather.generation.WeatherApplication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherApiTest {

    @Mock
    private WebClient webClientMock;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @InjectMocks
    private WeatherApi weatherApi;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // This force-injects our mock into the private final field 'webClient'
        ReflectionTestUtils.setField(weatherApi, "webClient", webClientMock);

        // Setup the common fluent chain behavior
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
    }

    // --- getCoordinates Tests ---
    /**
     * Tests the successful retrieval of city coordinates.
     * <p>
     * This test simulates a standard API response where a city search returns valid data.
     * It verifies that the {@link WebClient} chain is correctly traversed and that
     * the resulting {@link JsonNode} contains the expected city name.
     * </p>
     * * @throws Exception if JSON parsing or mock configuration fails.
     */
    @Test
    void getCoordinates_ShouldReturnJson_WhenCityIsFound() throws Exception {
        // Arrange
        JsonNode mockNode = mapper.readTree("{\"results\":[{\"name\":\"Napoli\"}]}");
        when(responseSpecMock.bodyToMono(JsonNode.class)).thenReturn(Mono.just(mockNode));

        // Act
        JsonNode result = weatherApi.getCoordinates("Napoli");

        // Assert
        assertNotNull(result);
        assertEquals("Napoli", result.get("results").get(0).get("name").asText());
    }

/**
 * Tests the error handling logic of the geocoding request.
 * <p>
 * This test simulates a scenario where the API call fails (e.g., network error or
 * server exception). It ensures that the {@code .onErrorResume()} logic properly
 * catches the exception and returns {@code null} instead of propagating the
 * error or crashing the application.
 * </p>
    @Test
    void getCoordinates_ShouldReturnNull_WhenApiErrors() {
        // Arrange: Simulate an error that triggers onErrorResume
        when(responseSpecMock.bodyToMono(JsonNode.class)).thenReturn(Mono.error(new RuntimeException("API Down")));

        // Act
        JsonNode result = weatherApi.getCoordinates("Napoli");

        // Assert
        assertNull(result, "Should return null on API error as per your documentation");
    }

    // --- getWeather Tests ---

    @Test
    void getWeather_ShouldReturnDailyData_WhenCoordinatesAreValid() throws Exception {
        // Arrange
        JsonNode mockWeather = mapper.readTree("{\"daily\":{\"temperature_2m_max\":[20.5]}}");
        when(responseSpecMock.bodyToMono(JsonNode.class)).thenReturn(Mono.just(mockWeather));

        // Act
        JsonNode result = weatherApi.getWeather(40.85, 14.26);

        // Assert
        assertNotNull(result);
        assertTrue(result.has("daily"));
        assertEquals(20.5, result.get("daily").get("temperature_2m_max").get(0).asDouble());
    }

    @Test
    void getWeather_ShouldReturnNull_OnTimeout() {
        // Arrange: Mono.never() never emits, which will trigger your .timeout(5s)
        when(responseSpecMock.bodyToMono(JsonNode.class)).thenReturn(Mono.never());

        // Act
        // Note: This test waits for the 5s timeout in your code
        JsonNode result = weatherApi.getWeather(0.0, 0.0);

        // Assert
        assertNull(result);
    }
}
