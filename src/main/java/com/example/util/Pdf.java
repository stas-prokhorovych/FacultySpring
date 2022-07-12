package com.example.util;

import com.example.model.User;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Used to generate pdf report
 */
public class Pdf {
    public static ByteArrayInputStream export(List<User> userToPrint, String role) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            fontTitle.setSize(18);
            Paragraph paragraph = new Paragraph(role + "s", fontTitle);
            paragraph.setAlignment(Paragraph.ALIGN_CENTER);


            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{6.0f, 11.0f, 5.0f, 5.0f, 3.0f});

            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            PdfPCell cell = new PdfPCell();

            // table headers
            cell.setPhrase(new Phrase("Login", font));
            table.addCell(cell);
            cell.setPhrase(new Phrase("Email", font));
            table.addCell(cell);
            cell.setPhrase(new Phrase("First name", font));
            table.addCell(cell);
            cell.setPhrase(new Phrase("Last name", font));
            table.addCell(cell);
            cell.setPhrase(new Phrase("Access", font));
            table.addCell(cell);

            for (User user : userToPrint) {
                table.addCell(user.getLogin());
                table.addCell(user.getEmail());
                table.addCell(user.getFirstName());
                table.addCell(user.getLastName());
                table.addCell(String.valueOf(user.isUserAccess()));
            }

            PdfWriter.getInstance(document, out);
            document.open();

            document.add(paragraph);
            document.add(Chunk.NEWLINE);
            document.add(table);

            document.close();

        } catch (DocumentException ex) {

        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}
