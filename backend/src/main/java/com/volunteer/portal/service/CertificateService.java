package com.volunteer.portal.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.volunteer.portal.entity.Certificate;
import com.volunteer.portal.entity.Event;
import com.volunteer.portal.entity.Proof;
import com.volunteer.portal.entity.User;
import com.volunteer.portal.repository.CertificateRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    private static final String CERTIFICATES_DIR = "certificates/";
    private static final String QR_DIR = "certificates/qr/";

    /* =====================================================
       PUBLIC API – GENERATE CERTIFICATE
    ===================================================== */
    public Certificate generateCertificate(User user, Event event, Proof proof)
            throws IOException, DocumentException, WriterException {

        String certificateId =
                "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Files.createDirectories(Paths.get(CERTIFICATES_DIR));
        Files.createDirectories(Paths.get(QR_DIR));

        String qrFileName = generateQRCode(certificateId);
        String pdfFileName = generatePDFCertificate(
                user, event, proof, certificateId, qrFileName
        );

        Certificate certificate = new Certificate(
                certificateId,
                user,
                event,
                proof,
                "/certificates/" + pdfFileName
        );

        certificate.setQrCodeUrl("/certificates/qr/" + qrFileName);
        return certificateRepository.save(certificate);
    }

    /* =====================================================
       QR CODE GENERATION
    ===================================================== */
    private String generateQRCode(String certificateId)
            throws WriterException, IOException {

        String data = "/api/certificates/verify/" + certificateId;
        String fileName = certificateId + "_qr.png";
        Path path = Paths.get(QR_DIR + fileName);

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(
                data, BarcodeFormat.QR_CODE, 200, 200
        );

        MatrixToImageWriter.writeToPath(matrix, "PNG", path);
        return fileName;
    }

    /* =====================================================
       PDF CERTIFICATE (ACADEMIC DESIGN)
    ===================================================== */
    private String generatePDFCertificate(
            User user,
            Event event,
            Proof proof,
            String certificateId,
            String qrCodePath
    ) throws DocumentException, IOException {

        String fileName = certificateId + ".pdf";
        String filePath = CERTIFICATES_DIR + fileName;

        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(
                document, new FileOutputStream(filePath)
        );
        document.open();

        PdfContentByte canvas = writer.getDirectContentUnder();

        /* ---------------- BACKGROUND ---------------- */
        Rectangle bg = new Rectangle(
                0, 0,
                PageSize.A4.getWidth(),
                PageSize.A4.getHeight()
        );
        bg.setBackgroundColor(new Color(10, 45, 90)); // navy blue
        canvas.rectangle(bg);

        /* ---------------- WHITE BORDER ---------------- */
        Rectangle border = new Rectangle(
                30, 30,
                PageSize.A4.getWidth() - 30,
                PageSize.A4.getHeight() - 30
        );
        border.setBorder(Rectangle.BOX);
        border.setBorderWidth(6);
        border.setBorderColor(Color.WHITE);
        writer.getDirectContent().rectangle(border);

        /* ---------------- TITLE ---------------- */
        Font titleFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                26,
                new Color(212, 175, 55)
        );
        Paragraph title = new Paragraph(
                "CERTIFICATE OF\nVOLUNTEER SERVICE",
                titleFont
        );
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(80);
        title.setSpacingAfter(20);
        document.add(title);

        /* ---------------- ORGANIZATION ---------------- */
        Font orgFont = FontFactory.getFont(
                FontFactory.HELVETICA,
                14,
                Color.WHITE
        );
        Paragraph org = new Paragraph(
                "VOLUNTEER MANAGEMENT WING – I",
                orgFont
        );
        org.setAlignment(Element.ALIGN_CENTER);
        org.setSpacingAfter(40);
        document.add(org);

        /* ---------------- BODY ---------------- */
        Font bodyFont = FontFactory.getFont(
                FontFactory.HELVETICA,
                13,
                Color.WHITE
        );

        Paragraph certify = new Paragraph(
                "THIS CERTIFIES THAT",
                bodyFont
        );
        certify.setAlignment(Element.ALIGN_CENTER);
        certify.setSpacingAfter(15);
        document.add(certify);

        /* ---------------- NAME ---------------- */
        Font nameFont = FontFactory.getFont(
                FontFactory.TIMES_BOLDITALIC,
                26,
                new Color(212, 175, 55)
        );
        Paragraph name = new Paragraph(user.getName(), nameFont);
        name.setAlignment(Element.ALIGN_CENTER);
        name.setSpacingAfter(20);
        document.add(name);

        /* ---------------- EVENT ---------------- */
        Paragraph eventText = new Paragraph(
                "has successfully completed the volunteer event\n\n"
                        + event.getTitle(),
                bodyFont
        );
        eventText.setAlignment(Element.ALIGN_CENTER);
        eventText.setSpacingAfter(40);
        document.add(eventText);

        /* ---------------- DETAILS ---------------- */
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("MMMM dd, yyyy");

        Font detailFont = FontFactory.getFont(
                FontFactory.HELVETICA,
                11,
                Color.WHITE
        );

        Paragraph details = new Paragraph(
        "Points Awarded: " + proof.getPointsAwarded() + "\n"
        + "Issued Date: " + proof.getReviewedAt().format(fmt) + "\n"
        + "Certificate ID: " + certificateId,
        detailFont
);
        details.setAlignment(Element.ALIGN_CENTER);
        details.setSpacingAfter(50);
        document.add(details);

        /* ---------------- SIGNATURE & SEAL ---------------- */
        PdfPTable signTable = new PdfPTable(2);
        signTable.setWidthPercentage(70);
        signTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        Image signature = Image.getInstance(
                "src/main/resources/signature1.png"
        );
        signature.scaleToFit(120, 60);

        Image seal = Image.getInstance(
                "src/main/resources/seal1.png"
        );
        seal.scaleToFit(80, 80);

        PdfPCell signCell = new PdfPCell(signature);
        signCell.setBorder(Rectangle.NO_BORDER);
        signCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell sealCell = new PdfPCell(seal);
        sealCell.setBorder(Rectangle.NO_BORDER);
        sealCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        signTable.addCell(signCell);
        signTable.addCell(sealCell);

        document.add(signTable);

        /* ---------------- QR CODE ---------------- */
        Image qr = Image.getInstance(QR_DIR + qrCodePath);
        qr.scaleToFit(80, 80);
        qr.setAbsolutePosition(470, 60);
        document.add(qr);

        /* ---------------- FOOTER ---------------- */
        Font footerFont = FontFactory.getFont(
                FontFactory.HELVETICA_OBLIQUE,
                9,
                Color.LIGHT_GRAY
        );
        Paragraph footer = new Paragraph(
                "This certificate is system generated and can be verified online.",
                footerFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(40);
        document.add(footer);

        document.close();
        return fileName;
    }

    /* =====================================================
       FETCH METHODS
    ===================================================== */
    public Certificate getCertificateById(String certificateId) {
        return certificateRepository.findByCertificateId(certificateId)
                .orElseThrow(() ->
                        new RuntimeException("Certificate not found"));
    }

    public Certificate getCertificateByProofId(Long proofId) {
        return certificateRepository.findByProofId(proofId).orElse(null);
    }

    public byte[] getCertificateFile(String certificateId) throws IOException {
        Certificate cert = getCertificateById(certificateId);
        Path path = Paths.get("." + cert.getCertificateUrl());
        return Files.readAllBytes(path);
    }
}
