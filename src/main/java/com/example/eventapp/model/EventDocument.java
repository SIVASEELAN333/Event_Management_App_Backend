package com.example.eventapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.example.eventapp.model.WinnerDTO;


import java.util.List;

@Document(collection = "events")
public class EventDocument {

    @Id
    private String id;

    private String title;
    private String description;
    private String date;        // Format: yyyy-mm-dd
    private String time;        // Format: HH:mm or any
    private String venue;
    private String organizer;
    private String imageUrl;
    private Integer capacity;

    private WinnerDTO winner;


    private List<String> participants;         // userIds (as string)
    private List<String> allowedParticipants;  // userIds allowed to register (optional)

    // Constructors
    public EventDocument() {}

    public EventDocument(String title, String description, String date, String time, String venue,
                         String organizer, String imageUrl,Integer capacity, List<String> participants,
                         List<String> allowedParticipants) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.organizer = organizer;
        this.imageUrl = imageUrl;
        this.capacity=capacity;
        this.participants = participants;
        this.allowedParticipants = allowedParticipants;
    }

    // Getters and Setters
    public WinnerDTO getWinner() {
        return winner;
    }
    public void setWinner(WinnerDTO winner) {
        this.winner = winner;
    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }
    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getOrganizer() {
        return organizer;
    }
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public Integer getCapacity() {
        return capacity;
    }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }


    public List<String> getParticipants() {
        return participants;
    }
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public List<String> getAllowedParticipants() {
        return allowedParticipants;
    }
    public void setAllowedParticipants(List<String> allowedParticipants) {
        this.allowedParticipants = allowedParticipants;
    }
}
