package com.qr.code.generator.party.Controller;


import com.google.zxing.WriterException;
import com.lowagie.text.DocumentException;
import com.qr.code.generator.party.Helper.CSVExtractor;
import com.qr.code.generator.party.Modal.StaffMember;
import com.qr.code.generator.party.Service.FileUpload;
import com.qr.code.generator.party.Service.QRGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("party-invites/")
public class QrGeneratorController {

    @Autowired
    QRGenerator qrGenerator;

    @Autowired
    FileUpload fileUpload;

    @PostMapping("/")
    public ResponseEntity<?> test(){

        return ResponseEntity.ok( "Yes we can");
    }

    @PostMapping("/guest-list")
    public ResponseEntity<?> uploadGuestList(@RequestParam("file") MultipartFile file) throws DocumentException, IOException, WriterException {


        return ResponseEntity.ok(fileUpload.uploadAndGenerateInvites(file));

    }



    @PostMapping("/guest")
    public ResponseEntity<?> generateQr(@RequestBody StaffMember staffMember) throws DocumentException, IOException, WriterException {

        qrGenerator.generateQRCodePDFWithoutBackground(staffMember);

        return ResponseEntity.ok( "Invites generated");
    }


}
