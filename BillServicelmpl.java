package com.inn.cafe.serviceImpl;

import com.inn.cafe.jwt.JwtFilter;
import com.inn.cafe.POGO.Bill;
import com.inn.cafe.constents.CafeConstants;
import com.inn.cafe.dao.BillDao;
import com.inn.cafe.service.BillService;
import com.inn.cafe.utils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.stream;

@Slf4j
@Service
public class BillServiceImpl implements BillService{

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    BillDao billDao;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("Inside generateReport")
        try {
            String fileName;
            if(ValidateRequestMap(requestMap)){
                if (requestMap.containsKey("isGenerate") && !(Boolean)requestMap.get("isGenerate")) {
                    fileName = (String) requestMap.get("vvid");
                }else {
                    fileName = CafeUtils.getVVID();
                    requestMap.put("vvid",fileName);
                    insertBill(requestMap);
                }

                String data = "Name: "+requestMap.get("name") + "\n"+"Contact Number: "+requestMap.get("contactNumber") +
                        "\n"+"Email: "+requestMap.get("email") + "\n"+"Paymeny Method: "+requestMap.get("paymentMethod");

                Document document = new Docoment();
                PdfWriter.getInstance(document,new FileOutputStream(CafeConstants.STORE_LOCATION+"\\"+fileName+".pdf");

                document.open();
                setRectangleInPdf(document);

                Paragraph chunk = new Paragraph(string: "Cafe Management System",getFont("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);

                Paragraph paragraph = new Paragrap(data+"\n \n",getFont("Data"));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                addTableHeader(table);

                JSONArray jsonArray = CafeUtils.getJsonArrayFromString((String) requestMap.get("productDetails"));
                for (int i=0;i<jsonArray.length();i++){
                    addRows(table,CafeUtils,getMapFromJson(jsonArray.getString(i)));
                    document.add(footer);
                    document.close();
                    return new ResponseEntity<>("{\"uuid\":\""+fileName+"\}",HttpStatus.OK);
                }
                document.add(table);

                Paragraph footer = new Paragraph("Total : "+requestMap.get("totalAmount")+"\n"
                +"Thank you for visiting.Please visit again!!",getFont("Data"));

            }
            return CafeUtils.getResponseEntity(responceMassage "Required data not found.", HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            ex.printStractTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void addRows(PdfPTable table, Map<String, Object> data){
        log.info("Inside addRows");
        table.addCell((String)data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double)data.get("price")));
        table.addCell(Double.toString((Double)data.get("total")))
    }

    private void addTableHeader(PdfPTable table) {
        log.info("INside addTableHeader");
        Stream.of("Name","Category","Quantity","Price","Sub Total")
                .forEach(columnTitle -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            header.setBackgroundColor(BaseColor.YELLOW);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(header);
        });
    }

    private Font getFont(String type) {
        log.info("INside getFont");
        switch (type){
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, size 18,BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;
            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BASEColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;
            default:
                return new Font();
        }
    }

    private void setRectangleInPdf(Document document) throws DocumentException{
        log.info("INside setRectangleInPdf");
        Rectangle rect = new Rectangle( llx:577, lly:825, urx:18, ury:15);
        rect.enableBroderSide(1);
        rect.enableBroderSide(2);
        rect.enableBroderSide(4);
        rect.enableBroderSide(8);
        rect.setBorderColor(BaseColor.BLACK);
        rect.setBorderWidth(1);
        document.add(rect);
    }

    private void insertBill(Map<String, Object> requestMap) {
        try {
            Bill bill = new Bill();
            bill.setVvid((String) requestMap.get("vvid"));
            bill.setName(requestMap.get("name"));
            bill.setEmail(requestMap.get("email"));
            bill.setContactNumber(requestMap.get("contactNumber"));
            bill.setPaymentMethod(requestMap.get("paymentMethod"));
            bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
            bill.setProductDetail((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());
            billDao.save(bill);
        }catch (Exception ex) {
            ex.printStractTrace();
        }
    }

    private boolean vaildateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email")  &&
                requestMap.containsKey("paymentMethod")  &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("totalAmount");
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        List<Bill> list = new ArrayList();
        if (jwtFilter.isAdmin()){
            list = billDao.getAllBills();
        }else {
            list = billDao.getBillsByUserName(jwtFilter.getCurrentUser());`
        }
        return new ResponeEntity<>(List,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("Inside getPdf : requestMap {}",requestMap);
        try {
           byte[] byteArray = new byte[0];
           if (!requestMap.containsKey("vvid") && vaildateRequestMap(requestMap))
               return new ResponseEntity<>(byteArray,HttpStatus.BAD_REQUEST);
           String filePath = CafeConstants.STORE_LOCATION+"\\"+(String) requestMap.get("vvid")+".pdf";
           if (CafeUtils.isFileExist(filePath)){
               byteArray = getByteArray(filePath);
               return new ResponseEntity<>(byteArray, HttpStatus.OK);
           }else {
               requestMap.put("isGeneral", false);
               generateReport(requestMap);
               byteArray = getByteArray(filePath);
               return new ResponseEntity<>(byteArray, HttpStatus.OK);
           }
        }catch (Exception ex) {
            ex.printStractTrace();
        }
        return null;
    }

    private byte[] getByteArray(String filePath) throws Exception {
        File initialFile = new File(filePath);
        IntputStrem targetStream = new FileInputStream(initialFile);
        byte[] byteArray = IOUTils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id){
        try {
            Optional optional = billDao.findById(id);
            if (!optional.isEmpty()) {
                billDao.deleteById(id);
                return CafeUtils.getResponseEntity("Bill Deleted Successfully",HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity("Bill id dose not exist",HttpStatus.OK);
        }catch (Exception ex) {
            ex.printStractTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


}