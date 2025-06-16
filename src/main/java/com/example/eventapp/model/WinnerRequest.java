package com.example.eventapp.model;

import jdk.jfr.DataAmount;
import lombok.Data;

@Data
public class WinnerRequest {
    private String winnerEmail;
    private String winnerName;
    private String eventTitle;
}
