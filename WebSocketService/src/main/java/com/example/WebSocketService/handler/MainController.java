package com.example.WebSocketService.handler;

import com.example.WebSocketService.models.MessageModelRequest;
import com.example.WebSocketService.models.MessageModelResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class MainController {
    private static final Logger logger = LogManager.getLogger(MainController.class);
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public String sendMessage(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        MessageModelRequest messageModel = mapper.readValue(json, MessageModelRequest.class);
        var result = new MessageModelResponse();
        result.setLogin(messageModel.getLogin());
        result.setMessage(messageModel.getMessage());
        result.setDateTime(Instant.now());
        String responseJson = mapper.writeValueAsString(result);
        logger.info("Received message: {}", responseJson);

        return responseJson;
    }
}
