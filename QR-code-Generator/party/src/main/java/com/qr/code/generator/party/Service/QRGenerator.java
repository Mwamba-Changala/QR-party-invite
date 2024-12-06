package com.qr.code.generator.party.Service;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import java.awt.Color;
import com.lowagie.text.pdf.PdfWriter;
import com.qr.code.generator.party.Modal.StaffMember;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;


import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class QRGenerator {

    public byte[] generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    public void generateQRCodePDF(HttpServletResponse response) throws WriterException, DocumentException, IOException  {

        byte[] pngImageData = generateQRCodeImage("Ndiwe kembo mwaiche", 250, 250);

        Document document = new Document();
//        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Image qrCodeImage = Image.getInstance(pngImageData);
        document.add(qrCodeImage);
        document.close();


    }

    public void generateQRCodePDFSaveToFolder(StaffMember staffMemberList) throws WriterException, DocumentException, IOException  {

        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 12);


//        for (StaffMember student: staffMemberList) {
            // Generate the QR code as a byte array
            byte[] pngImageData = generateQRCodeImage(staffMemberList.getFNumber(), 250, 250);

//            // Define the directory where you want to save the PDF
//            String outputDirectory = "C:\\Users\\user\\IdeaProjects\\DirectoryCreationAndFileUpload-v0.1\\src\\main\\resources\\uploads\\";
//            String filePath = outputDirectory + "Test" + ".pdf";
//
//            // Create a Document and write to a FileOutputStream instead of HttpServletResponse
//            Document document = new Document();
////        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
//            PdfWriter.getInstance(document, new FileOutputStream("generatedInvites/_"+staffMemberList.getFNumber()+".pdf"));

        // Get the current working directory
        String baseDirectory = System.getProperty("user.dir");

        // Define the folder to be created in the root directory
        String uploadsFolder = baseDirectory + File.separator + "generatedInvites";

        // Ensure the folder exists
        File directory = new File(uploadsFolder);
        if (!directory.exists()) {
            boolean created = directory.mkdir();
            if (!created) {
                throw new RuntimeException("Failed to create directory: " + uploadsFolder);
            }
        }

        // Define the file path
        String filePath = uploadsFolder + File.separator+staffMemberList.getFNumber()+".pdf";

        Document document = new Document();

        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        PdfWriter.getInstance(document, fileOutputStream);

            // Add content to the PDF
            document.open();
            Image qrCodeImage = Image.getInstance(pngImageData);
            qrCodeImage.setAlignment(Element.ALIGN_CENTER);
            document.add(qrCodeImage);

            Paragraph idParagraph = new Paragraph();
            idParagraph.add(new Chunk("ID: ", boldFont));
            idParagraph.add(new Chunk(String.valueOf(staffMemberList.getFNumber()), regularFont));
            idParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(idParagraph);

            Paragraph nameParagraph = new Paragraph();
            nameParagraph.add(new Chunk("Name: ", boldFont));
            nameParagraph.add(new Chunk(staffMemberList.getName(), regularFont));
            nameParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(nameParagraph);

            Paragraph emailParagraph = new Paragraph();
            emailParagraph.add(new Chunk("Email: ", boldFont));
            emailParagraph.add(new Chunk(staffMemberList.getEmailAddress(), regularFont));
            emailParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(emailParagraph);
            document.close();

//            Paragraph contactParagraph = new Paragraph();
//            contactParagraph.add(new Chunk("Contact: ", boldFont));
//            contactParagraph.add(new Chunk(student.getEmail(), regularFont));
//            contactParagraph.setAlignment(Element.ALIGN_CENTER);
//            document.add(contactParagraph);
//            document.close();

//

//        fileOutputStream.close();

            System.out.println("PDF saved to: " + filePath);

//        }
    }

    public String generateQR(List<StaffMember> staffMemberList) throws WriterException, DocumentException, IOException {
        // Define the directory for the generated PDFs
        String baseDirectory = System.getProperty("user.dir");
        String uploadsFolder = baseDirectory + File.separator + "generatedInvites";

        // Ensure the folder exists
        File directory = new File(uploadsFolder);
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                throw new RuntimeException("Failed to create directory: " + uploadsFolder);
            }
        }

        AtomicInteger count = new AtomicInteger();

        staffMemberList.forEach(staffMember->{
            // Define the file path
            String filePath = uploadsFolder + File.separator + staffMember.getFNumber() + ".pdf";

            // Path to the background image
            String backgroundImagePath = baseDirectory + File.separator + "background" + File.separator + "sample.jpg";

            // Generate the QR code
            byte[] qrCodeData = null;
            try {
                qrCodeData = generateQRCodeImage(staffMember.getFNumber(), 150, 150);
            } catch (WriterException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Document document = new Document();

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(filePath);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            PdfWriter writer = PdfWriter.getInstance(document, fileOutputStream);

            document.open();

            // Add background image with adjusted scaling
            Image backgroundImage = null;
            try {
                backgroundImage = Image.getInstance(backgroundImagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            float scaledWidth = PageSize.A4.getWidth();
            float scaledHeight = PageSize.A4.getHeight() - 40; // Reduce height to avoid overlap
            backgroundImage.scaleToFit(scaledWidth, scaledHeight);
            backgroundImage.setAbsolutePosition(
                    (PageSize.A4.getWidth() - backgroundImage.getScaledWidth()) / 2,
                    (PageSize.A4.getHeight() - backgroundImage.getScaledHeight()) / 2
            );
            document.add(backgroundImage);

            // Add QR code image (centered)
            Image qrCodeImage = null;
            try {
                qrCodeImage = Image.getInstance(qrCodeData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            qrCodeImage.scaleToFit(200, 200); // Adjust QR code size as needed
            qrCodeImage.setAbsolutePosition(
                    (PageSize.A4.getWidth() - qrCodeImage.getScaledWidth()) / 2,    // Center horizontally
                    ( (PageSize.A4.getHeight() - qrCodeImage.getScaledHeight()) / 2) - 100  // Center vertically
            );
            document.add(qrCodeImage);

            document.close();
            System.out.println("PDF saved to: " + filePath);
            count.getAndIncrement();
        });

        System.out.println(count+ ": invites Generated");

        return count+ ": invites Generated";
    }



    // Generate PDFs with QR Codes and text below them
    public void generateQRCodePDFWithoutBackground(StaffMember staffMember)
            throws WriterException, DocumentException, IOException {

        // Fonts for the text
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Color.BLACK);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 18, Color.BLACK);
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);

        // Output directory
        String outputDirectory = System.getProperty("user.dir") + "/generatedInvites/";
        File outputDir = new File(outputDirectory);
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                throw new IOException("Failed to create output directory: " + outputDirectory);
            }
        }

        // Generate the QR code
        byte[] qrCodeData = generateQRCodeImage(staffMember.getFNumber(), 150, 150);

        // Define the PDF file path
        String pdfFilePath = outputDirectory + "Invite_" + staffMember.getFNumber() + ".pdf";

        // Create a PDF document
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
        document.open();

        // Add the QR code to the center
        Image qrCodeImage = Image.getInstance(qrCodeData);
        qrCodeImage.scaleAbsolute(150, 150); // QR Code size
        qrCodeImage.setAbsolutePosition(
                (PageSize.A4.getWidth() - qrCodeImage.getScaledWidth()) / 2,  // Horizontally centered
                (PageSize.A4.getHeight() / 2) + 50                           // Adjust vertically slightly above center
        );
        document.add(qrCodeImage);

        // Add text below the QR code
        float textStartY = (PageSize.A4.getHeight() / 2) - 120; // Position for text below QR code

        // Title text
        Paragraph title = new Paragraph("End of Year Party", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(20f);
        document.add(title);

        // Subtitle text (Name)
        Paragraph subtitle = new Paragraph("Name: " + staffMember.getName(), subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingBefore(10f); // Closer to the title
        document.add(subtitle);

        // Footer text (ID)
        Paragraph footer = new Paragraph("ID: " + staffMember.getFNumber(), footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10f); // Closer to the subtitle
        document.add(footer);

        // Close the document
        document.close();
        System.out.println("PDF saved at: " + pdfFilePath);
    }

//    }

}
