package com.library.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.library.model.Book;
import com.library.model.Borrowing;
import com.library.model.Fine;
import com.library.model.Reader;

import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class PDFGenerator {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    
    public <T> boolean generateReport(String reportType, List<T> data, String filePath) {
        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Add title
            String title = getReportTitle(reportType);
            Paragraph titleParagraph = new Paragraph(title, TITLE_FONT);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(titleParagraph);
            
            // Add date
            Paragraph dateParagraph = new Paragraph("Generated on: " + 
                    LocalDate.now().format(DATE_FORMATTER), NORMAL_FONT);
            dateParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(dateParagraph);
            
            document.add(new Paragraph(" ")); // Add space
            
            // Add table based on report type
            if (reportType.equals("book_inventory") && !data.isEmpty() && data.get(0) instanceof Book) {
                addBookInventoryTable(document, (List<Book>) data);
            } else if (reportType.equals("borrowing_history") && !data.isEmpty() && data.get(0) instanceof Borrowing) {
                addBorrowingHistoryTable(document, (List<Borrowing>) data);
            } else if (reportType.equals("fines") && !data.isEmpty() && data.get(0) instanceof Fine) {
                addFinesTable(document, (List<Fine>) data);
            } else if (reportType.equals("reader_statistics") && !data.isEmpty() && data.get(0) instanceof Reader) {
                addReaderStatisticsTable(document, (List<Reader>) data);
            } else {
                document.add(new Paragraph("No data available for this report type."));
            }
            
            document.close();
            return true;
        } catch (Exception e) {
            System.err.println("Error generating PDF report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private String getReportTitle(String reportType) {
        switch (reportType) {
            case "book_inventory":
                return "Book Inventory Report";
            case "borrowing_history":
                return "Borrowing History Report";
            case "fines":
                return "Fines Report";
            case "reader_statistics":
                return "Reader Statistics Report";
            default:
                return "Library Management System Report";
        }
    }
    
    private void addBookInventoryTable(Document document, List<Book> books) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{1, 3, 2, 1, 2, 2, 1, 1});
        table.setWidthPercentage(100);
        
        // Add table headers
        addTableHeader(table, Arrays.asList(
                "ID", "Title", "Author", "Year", "Category", "Status", "Quantity", "Available"
        ));
        
        // Add table data
        for (Book book : books) {
            table.addCell(createCell(book.getMaSach(), NORMAL_FONT));
            table.addCell(createCell(book.getTenSach(), NORMAL_FONT));
            table.addCell(createCell(book.getTacGia(), NORMAL_FONT));
            table.addCell(createCell(String.valueOf(book.getNamXuatBan()), NORMAL_FONT));
            table.addCell(createCell(book.getTheLoai(), NORMAL_FONT));
            table.addCell(createCell(book.getTrangThai(), NORMAL_FONT));
            table.addCell(createCell(String.valueOf(book.getSoLuong()), NORMAL_FONT));
            table.addCell(createCell(String.valueOf(book.getSoLuongKhaDung()), NORMAL_FONT));
        }
        
        document.add(table);
    }
    
    private void addBorrowingHistoryTable(Document document, List<Borrowing> borrowings) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{1, 3, 2, 2, 2, 2, 2});
        table.setWidthPercentage(100);
        
        // Add table headers
        addTableHeader(table, Arrays.asList(
                "ID", "Book Title", "Reader", "Borrow Date", "Due Date", "Return Date", "Status"
        ));
        
        // Add table data
        for (Borrowing borrowing : borrowings) {
            table.addCell(createCell(borrowing.getMaMuonTra(), NORMAL_FONT));
            table.addCell(createCell(borrowing.getTenSach(), NORMAL_FONT));
            table.addCell(createCell(borrowing.getTenDocGia(), NORMAL_FONT));
            table.addCell(createCell(formatDate(borrowing.getNgayMuon()), NORMAL_FONT));
            table.addCell(createCell(formatDate(borrowing.getNgayHenTra()), NORMAL_FONT));
            table.addCell(createCell(formatDate(borrowing.getNgayTraThucTe()), NORMAL_FONT));
            table.addCell(createCell(borrowing.getTrangThai(), NORMAL_FONT));
        }
        
        document.add(table);
    }
    
    private void addFinesTable(Document document, List<Fine> fines) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{1, 1, 2, 2, 3, 2, 1});
        table.setWidthPercentage(100);
        
        // Add table headers
        addTableHeader(table, Arrays.asList(
                "ID", "Borrowing ID", "Reader", "Amount", "Reason", "Date", "Paid"
        ));
        
        // Add table data
        double totalAmount = 0;
        for (Fine fine : fines) {
            table.addCell(createCell(fine.getMaPhieuPhat(), NORMAL_FONT));
            table.addCell(createCell(fine.getMaMuonTra(), NORMAL_FONT));
            table.addCell(createCell(fine.getTenDocGia(), NORMAL_FONT));
            table.addCell(createCell(String.format("%.2f", fine.getSoTienPhat()), NORMAL_FONT));
            table.addCell(createCell(fine.getLyDo(), NORMAL_FONT));
            table.addCell(createCell(formatDate(fine.getNgayPhat()), NORMAL_FONT));
            table.addCell(createCell(fine.isDaTra() ? "Yes" : "No", NORMAL_FONT));
            
            totalAmount += fine.getSoTienPhat();
        }
        
        document.add(table);
        
        // Add summary
        Paragraph summary = new Paragraph("Total Fine Amount: " + String.format("%.2f", totalAmount), HEADER_FONT);
        summary.setAlignment(Element.ALIGN_RIGHT);
        document.add(summary);
    }
    
    private void addReaderStatisticsTable(Document document, List<Reader> readers) throws DocumentException {
        PdfPTable table = new PdfPTable(new float[]{1, 3, 3, 2, 2});
        table.setWidthPercentage(100);
        
        // Add table headers
        addTableHeader(table, Arrays.asList(
                "ID", "Name", "Email", "Phone", "Join Date"
        ));
        
        // Add table data
        for (Reader reader : readers) {
            table.addCell(createCell(reader.getMaDocGia(), NORMAL_FONT));
            table.addCell(createCell(reader.getTenDocGia(), NORMAL_FONT));
            table.addCell(createCell(reader.getEmail(), NORMAL_FONT));
            table.addCell(createCell(reader.getSoDienThoai(), NORMAL_FONT));
            // table.addCell(createCell(formatDate(reader.getNgayDangKy()), NORMAL_FONT));
        }
        
        document.add(table);
    }
    
    private void addTableHeader(PdfPTable table, List<String> headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, HEADER_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }
    
    private PdfPCell createCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setPadding(5);
        return cell;
    }
    
    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }
}
