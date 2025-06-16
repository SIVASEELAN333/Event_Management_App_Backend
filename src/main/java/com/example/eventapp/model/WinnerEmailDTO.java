package com.example.eventapp.model;

public class WinnerEmailDTO {
    private String to;
    private String winnerName;
    private String eventTitle;

    public WinnerEmailDTO() {}

    public WinnerEmailDTO(String to, String winnerName, String eventTitle) {
        this.to = to;
        this.winnerName = winnerName;
        this.eventTitle = eventTitle;
    }

    // Getters and Setters
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }
}
