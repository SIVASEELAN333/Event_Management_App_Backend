// src/main/java/com/example/eventapp/utils/TicketGenerator.java
package com.example.eventapp.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;

public class TicketGenerator {

    public static byte[] generateTicket(String title, String date, String time, String venue, String email, String organizer) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            document.add(new Paragraph("Event Registration Ticket\n\n"));
            document.add(new Paragraph("Event: " + title));
            document.add(new Paragraph("Date: " + date));
            document.add(new Paragraph("Time: " + time));
            document.add(new Paragraph("Venue: " + venue));
            document.add(new Paragraph("Registered By: " + email));
            document.add(new Paragraph("Organizer: " + organizer));
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }
}
