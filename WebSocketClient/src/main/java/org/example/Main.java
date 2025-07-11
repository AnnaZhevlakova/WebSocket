package org.example;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.MessageModelRequest;
import org.example.model.MessageModelResponse;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Properties;
import java.util.Scanner;
public class Main {
    private static final Logger _logger = LogManager.getLogger(Main.class);
    private static final String WEBSOCKET_URL;
    static {
        // Load properties file
        Properties props = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
                // Set system property if not already set
                if (System.getProperty("websocket.url") == null) {
                    System.setProperty("websocket.url", props.getProperty("websocket.url", "ws://localhost:8080/ws"));
                }
            }
        } catch (IOException e) {
            _logger.error("Failed to load application.properties", e);
        }
        WEBSOCKET_URL = System.getProperty("websocket.url", "ws://localhost:8080/ws");
    }
    private static final String SUBSCRIBE_TOPIC = "/topic/messages";
    private static final String SEND_DESTINATION = "/app/chat";

    private static String _login;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите логин: ");
        _login = scanner.nextLine();

        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new StringMessageConverter());

        try {
            StompSession session = stompClient.connect(WEBSOCKET_URL, new StompSessionHandlerAdapter() {})
                    .get();

            // Subscribe to the topic
            session.subscribe(SUBSCRIBE_TOPIC, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return String.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload)  {
                    var json = payload.toString();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());

                    try {
                        _logger.info(json);
                        MessageModelResponse messageModel = mapper.readValue(json, MessageModelResponse.class);
                        System.out.println(String.format("%s %s %s", messageModel.getDateTime(), messageModel.getLogin(), messageModel.getMessage()));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                }
            });

            System.out.println("Connected to WebSocket server. Type messages to send (type 'exit' to quit):");

            while (true) {
                String messageText = scanner.nextLine();
                if ("exit".equalsIgnoreCase(messageText)) {
                    break;
                }
                MessageModelRequest message = new MessageModelRequest();
                message.setLogin(_login);
                message.setMessage(messageText);
                // Create ObjectMapper instance
                ObjectMapper mapper = new ObjectMapper();

                // Serialize object to JSON string
                String json = mapper.writeValueAsString(message);
                session.send(SEND_DESTINATION, json);
            }

            scanner.close();
            session.disconnect();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (stompClient.isRunning()) {
                stompClient.stop();
            }
        }
    }
}