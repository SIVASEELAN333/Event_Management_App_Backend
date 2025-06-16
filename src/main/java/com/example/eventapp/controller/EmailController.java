package com.example.eventapp.controller;

import com.example.eventapp.model.EventDocument;
import com.example.eventapp.model.WinnerEmailDTO;
import com.example.eventapp.repository.EventRepository;
import com.example.eventapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.internet.MimeMessage;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;


@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EventRepository eventRepository;

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

    @PostMapping("/winner")
    public ResponseEntity<?> sendWinnerCertificate(@RequestBody WinnerEmailDTO dto) {
        try {
            emailService.sendWinnerCertificate(dto.getTo(), dto.getWinnerName(), dto.getEventTitle());
            return ResponseEntity.ok("Winner certificate sent");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send certificate");
        }
    }



}
