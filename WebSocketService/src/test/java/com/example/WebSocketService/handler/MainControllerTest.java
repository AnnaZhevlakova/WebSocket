package com.example.WebSocketService.handler;

import com.example.WebSocketService.models.MessageModelRequest;
import com.example.WebSocketService.models.MessageModelResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MainControllerTest {

    @InjectMocks
    private MainController mainController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void sendMessage_ValidJson_ReturnsResponseJson() throws Exception {
        // Arrange
        MessageModelRequest request = new MessageModelRequest();
        request.setLogin("testUser");
        request.setMessage("Hello, World!");
        String inputJson = objectMapper.writeValueAsString(request);

        // Act
        String responseJson = mainController.sendMessage(inputJson);

        // Assert
        MessageModelResponse response = objectMapper.readValue(responseJson, MessageModelResponse.class);
        assertEquals("testUser", response.getLogin());
        assertEquals("Hello, World!", response.getMessage());
        assertNotNull(response.getDateTime());
        assertTrue(response.getDateTime().isBefore(Instant.now().plusSeconds(1)));
    }

    @Test
    void sendMessage_InvalidJson_ThrowsException() {
        // Arrange
        String invalidJson = "{invalid}";

        // Act & Assert
        assertThrows(Exception.class, () -> mainController.sendMessage(invalidJson));
    }

    @Test
    void sendMessage_EmptyJson_ReturnsResponseWithEmptyFields() throws Exception {
        // Arrange
        MessageModelRequest request = new MessageModelRequest();
        request.setLogin("");
        request.setMessage("");
        String inputJson = objectMapper.writeValueAsString(request);

        // Act
        String responseJson = mainController.sendMessage(inputJson);

        // Assert
        MessageModelResponse response = objectMapper.readValue(responseJson, MessageModelResponse.class);
        assertEquals("", response.getLogin());
        assertEquals("", response.getMessage());
        assertNotNull(response.getDateTime());
    }
}