package com.example.eventapp.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;



import com.itextpdf.text.pdf.PdfWriter;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    //@Async
    public void sendReminderEmail(String to, String eventTitle, String date, String time) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject("⏰ Reminder: " + eventTitle + " starts in 24 hours!");

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 15px;'>"
                    + "<h2 style='color: #e67e22;'>Upcoming Event: <span style='color: #2980b9;'>" + eventTitle + "</span></h2>"
                    + "<p style='font-size: 16px;'>This is a reminder that your event is starting soon.</p>"
                    + "<ul style='font-size: 16px;'>"
                    + "<li><strong>📅 Date:</strong> " + date + "</li>"
                    + "<li><strong>⏰ Time:</strong> " + time + "</li>"
                    + "</ul>"
                    + "<p style='margin-top: 20px;'>See you there!<br><em>— Event Team</em></p>"
                    + "</div>";

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            System.out.println("✅ HTML reminder email sent to: " + to);
        } catch (MessagingException e) {
            System.out.println("❌ Failed to send reminder email: " + e.getMessage());
        }
    }

    @Async
    public void sendRegistrationEmailWithTicket(
            String to,
            String title,
            String name,
            String date,
            String time,
            String venue,
            String organizer) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("🎉 Registration Confirmed: " + title);
        helper.setFrom("your-email@example.com"); // Your verified sender email

        // Create the HTML body for the email
        String htmlMsg = "<div style='font-family: Arial, sans-serif; padding: 15px;'>"
                + "<h2 style='color: #2c3e50;'>You have registered for <span style='color: #27ae60;'>" + title + "</span>!</h2>"
                + "<p style='font-size: 16px;'>Here are the event details:</p>"
                + "<ul style='font-size: 16px;'>"
                + "<li><strong>📍 Venue:</strong> " + venue + "</li>"
                + "<li><strong>📅 Date:</strong> " + date + "</li>"
                + "<li><strong>⏰ Time:</strong> " + time + "</li>"
                + "</ul>"
                + "<p>We look forward to seeing you there!<br><em>— Event Team</em></p>"
                + "</div>";

        helper.setText(htmlMsg, true);

        // Generate PDF ticket in memory using iText or similar library
        // ✅ NEW line:
        byte[] pdfBytes = generateTicketPdf(title, date, time, venue, organizer, to,name);


        InputStreamSource attachment = new ByteArrayResource(pdfBytes);
        helper.addAttachment("ticket_" + title.replaceAll("\\s+", "_") + ".pdf", attachment);

        mailSender.send(message);
    }


    private byte[] generateTicketPdf(String title, String date, String time, String venue, String organizer, String email,String name) throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Fonts
        Font fontTitle = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);
        Font fontLabel = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font fontValue = new Font(Font.FontFamily.HELVETICA, 12);

        // 🎟️ Title
        Paragraph ticketTitle = new Paragraph("🎟️ Event Registration Ticket", fontTitle);
        ticketTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(ticketTitle);

        document.add(new Paragraph(" ")); // Spacer

        // ⬛ Ticket Container (Table with Background Header)
        PdfPTable ticketTable = new PdfPTable(2);
        ticketTable.setWidthPercentage(100);
        ticketTable.setWidths(new float[]{1, 2});

        // Add cells (label + value)
        ticketTable.addCell(getStyledLabelCell("Name:", fontLabel));
        ticketTable.addCell(getStyledValueCell(name, fontValue));

        ticketTable.addCell(getStyledLabelCell("Email:", fontLabel));
        ticketTable.addCell(getStyledValueCell(email, fontValue));

        ticketTable.addCell(getStyledLabelCell("Event:", fontLabel));
        ticketTable.addCell(getStyledValueCell(title, fontValue));


        ticketTable.addCell(getStyledLabelCell("Date:", fontLabel));
        ticketTable.addCell(getStyledValueCell(date, fontValue));

        ticketTable.addCell(getStyledLabelCell("Time:", fontLabel));
        ticketTable.addCell(getStyledValueCell(time, fontValue));

        ticketTable.addCell(getStyledLabelCell("Venue:", fontLabel));
        ticketTable.addCell(getStyledValueCell(venue, fontValue));

        ticketTable.addCell(getStyledLabelCell("Organizer:", fontLabel));
        ticketTable.addCell(getStyledValueCell(organizer, fontValue));

        document.add(ticketTable);
        document.add(new Paragraph(" ")); // Spacer

        // 🖋️ Signature Section
        try {
            InputStream signatureStream = getClass().getResourceAsStream("/static/images/sivaseelan_signature.jpg");
            if (signatureStream != null) {
                Image signature = Image.getInstance(signatureStream.readAllBytes());
                signature.scaleToFit(120, 60);

                // Create table with one column for alignment
                PdfPTable signatureTable = new PdfPTable(1);
                signatureTable.setWidthPercentage(30); // width of the signature block
                signatureTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

                PdfPCell imageCell = new PdfPCell(signature, false);
                imageCell.setBorder(Rectangle.NO_BORDER);
                imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                imageCell.setPaddingBottom(5);

                PdfPCell labelCell = new PdfPCell(new Phrase("Authorized Signature", fontLabel));
                labelCell.setBorder(Rectangle.NO_BORDER);
                labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                signatureTable.addCell(imageCell);
                signatureTable.addCell(labelCell);

                document.add(new Paragraph(" ")); // Spacer
                document.add(signatureTable);

            } else {
                document.add(new Paragraph("Authorized Signature: [Missing Image]", fontLabel));
            }
        } catch (Exception e) {
            document.add(new Paragraph("⚠️ Signature load failed", fontLabel));
        }


        document.close();
        return baos.toByteArray();
    }

    // Helper to style cells
    private com.itextpdf.text.pdf.PdfPCell getStyledCell(String text, com.itextpdf.text.Font font) {
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text, font));
        cell.setPadding(8);
        cell.setBorderColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell getStyledLabelCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        cell.setBackgroundColor(new BaseColor(230, 230, 250)); // Light lavender
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell getStyledValueCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    @Async
    public void sendWinnerCertificate(String to, String winnerName, String eventTitle) throws Exception {
        byte[] pdfBytes = generateWinnerCertificatePdf(winnerName, eventTitle);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("🏆 Winner Certificate - " + eventTitle);
        helper.setText("Dear " + winnerName + ",\n\nCongratulations! You are the winner of " + eventTitle + ". Please find your certificate attached.");

        helper.addAttachment("WinnerCertificate.pdf", new ByteArrayDataSource(pdfBytes, "application/pdf"));

        mailSender.send(message);
    }




    private byte[] generateWinnerCertificatePdf(String winnerName, String eventTitle) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 100, 70);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // Colors & Fonts
        BaseColor headerColor = new BaseColor(101, 67, 33); // Elegant brown
        Font companyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD, headerColor);
        Font nameFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, new BaseColor(44, 62, 80)); // Blue-gray
        Font textFont = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL, BaseColor.DARK_GRAY);
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
        Font signatureFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.GRAY);

        // Draw modern border
        PdfContentByte canvas = writer.getDirectContent();
        Rectangle outer = new Rectangle(40, 40, 555, 802);
        outer.setBorder(Rectangle.BOX);
        outer.setBorderWidth(3f);
        outer.setBorderColor(new BaseColor(44, 62, 80)); // Outer dark blue-gray
        canvas.rectangle(outer);

        Rectangle inner = new Rectangle(50, 50, 545, 792);
        inner.setBorder(Rectangle.BOX);
        inner.setBorderWidth(1f);
        inner.setBorderColor(new BaseColor(200, 200, 200)); // Light inner gray
        canvas.rectangle(inner);

        // Header - company info
        Paragraph company = new Paragraph("Aries Media Technologies", labelFont);
        company.setAlignment(Element.ALIGN_CENTER);

        Paragraph address = new Paragraph(
                "70 Bowman Street, South Windsor, CT 06074\n" +
                        "\uD83D\uDCDE (124) 456-0467    |    ✉️ aries@email.com\n" +
                        "\uD83C\uDF10 www.ariesmedia.com",
                companyFont
        );
        address.setAlignment(Element.ALIGN_CENTER);
        address.setSpacingAfter(20);

        document.add(company);
        document.add(address);

        // Title
        Paragraph certTitle = new Paragraph("CERTIFICATE OF COMPLETION", titleFont);
        certTitle.setAlignment(Element.ALIGN_CENTER);
        certTitle.setSpacingAfter(30);
        document.add(certTitle);

        // Body
        Paragraph body1 = new Paragraph("This certificate is proudly presented to", textFont);
        body1.setAlignment(Element.ALIGN_CENTER);
        body1.setSpacingAfter(10);
        document.add(body1);

        Paragraph name = new Paragraph(winnerName, nameFont);
        name.setAlignment(Element.ALIGN_CENTER);
        name.setSpacingAfter(10);
        document.add(name);

        Paragraph body2 = new Paragraph("In recognition of outstanding performance and successful completion of", textFont);
        body2.setAlignment(Element.ALIGN_CENTER);
        body2.setSpacingAfter(10);
        document.add(body2);

        Paragraph project = new Paragraph(eventTitle.toUpperCase() + " PROJECT", labelFont);
        project.setAlignment(Element.ALIGN_CENTER);
        project.setSpacingAfter(10);
        document.add(project);

        Paragraph details = new Paragraph(
                "Organized by ARIES Training Center and awarded on: " + LocalDate.now(), textFont
        );
        details.setAlignment(Element.ALIGN_CENTER);
        details.setSpacingAfter(40);
        document.add(details);

        // Signature
        try {
            InputStream sigStream = getClass().getResourceAsStream("/static/images/sivaseelan_signature.jpg");
            if (sigStream != null) {
                Image signature = Image.getInstance(sigStream.readAllBytes());
                signature.scaleToFit(100, 50);

                PdfPTable sigTable = new PdfPTable(1);
                sigTable.setWidthPercentage(30);
                sigTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

                PdfPCell imageCell = new PdfPCell(signature, false);
                imageCell.setBorder(Rectangle.NO_BORDER);
                imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell labelCell = new PdfPCell(new Phrase("SIVASEELAN\nCoordinator", signatureFont));
                labelCell.setBorder(Rectangle.NO_BORDER);
                labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                sigTable.addCell(imageCell);
                sigTable.addCell(labelCell);
                document.add(sigTable);
            } else {
                Paragraph fallback = new Paragraph("Signature Missing", textFont);
                fallback.setAlignment(Element.ALIGN_RIGHT);
                document.add(fallback);
            }
        } catch (Exception e) {
            Paragraph error = new Paragraph("⚠ Could not load signature", textFont);
            error.setAlignment(Element.ALIGN_RIGHT);
            document.add(error);
        }

        document.close();
        return baos.toByteArray();
    }




}
