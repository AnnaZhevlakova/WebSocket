package org.example.model;

import java.time.Instant;

public class MessageModelResponse {
    private String _login;
    private String _message;
    private Instant _dateTime;

    public String getLogin() {
        return _login;
    }
    public void setLogin(String login) {
        _login = login;
    }

    public String getMessage() {
        return _message;
    }
    public void setMessage(String message) {
        _message = message;
    }

    public Instant getDateTime() {
        return _dateTime;
    }
    public void setDateTime(Instant dateTime) {
        _dateTime = dateTime;
    }
}
