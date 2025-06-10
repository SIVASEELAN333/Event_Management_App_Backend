package com.example.eventapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.internet.MimeMessage;


@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/send-ticket")
    public ResponseEntity<String> sendTicket(
            @RequestParam("email") String to,
            @RequestParam("eventTitle") String eventTitle,
            @RequestParam("pdf") MultipartFile pdfFile
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("🎟️ Ticket - " + eventTitle);
            helper.setText("Hi,\n\nAttached is your ticket for " + eventTitle + ".\n\nThanks,\nEvent Team");

            helper.addAttachment("ticket.pdf", pdfFile);
            mailSender.send(message);

            return ResponseEntity.ok("Email sent with ticket.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        }
    }
}
