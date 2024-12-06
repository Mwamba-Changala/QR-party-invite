package com.qr.code.generator.party.Service;


import com.google.zxing.WriterException;
import com.qr.code.generator.party.Helper.CSVExtractor;
import com.qr.code.generator.party.Modal.StaffMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FileUpload {


    @Autowired
    CSVExtractor csvExtractor;

    @Autowired
    QRGenerator    qrGenerator;

   public String uploadAndGenerateInvites(MultipartFile file) throws IOException, WriterException {

        List<StaffMember> staffMemberList = CSVExtractor.staffMemberList(file.getInputStream());

        return qrGenerator.generateQR(staffMemberList);

    }

}
