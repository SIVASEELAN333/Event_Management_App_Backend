package com.example.eventapp.model;

public class RegistrationResponse {
    private String message;
    private Long userId;
    private String eventId;
    private String timestamp;
    private Long registrationId;

    public RegistrationResponse(String message, Long userId, String eventId, String timestamp, Long registrationId) {
        this.message = message;
        this.userId = userId;
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.registrationId = registrationId;
    }

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }
}
