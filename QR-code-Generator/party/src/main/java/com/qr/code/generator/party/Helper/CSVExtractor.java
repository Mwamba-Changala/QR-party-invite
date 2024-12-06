package com.qr.code.generator.party.Helper;

import com.qr.code.generator.party.Modal.StaffMember;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CSVExtractor {

    public static String TYPE = "text/csv";
    static String[] HEADERs = { "Id", "Title", "Description", "Published" };

    public static boolean hasCSVFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }


    public static List<StaffMember> staffMemberList(InputStream is){

        try(BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            CSVParser csvParser = new CSVParser(fileReader,
                    CSVFormat.DEFAULT.withFirstRecordAsHeader()))
        {
                List<StaffMember> staffMemberList = new ArrayList<>();

                Iterable< CSVRecord> csvRecords = csvParser.getRecords();

                csvRecords.forEach(records->{
                    StaffMember staffMember = new StaffMember(

                            records.get(0),
                            records.get(1),
                            records.get(7)

                    );

                    staffMemberList.add(staffMember);
                });

                return  staffMemberList;
            }
        catch (IOException e){
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }

    }

}
