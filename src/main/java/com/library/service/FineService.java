package com.library.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.library.dao.FineDAO;
import com.library.model.Borrowing;
import com.library.model.Fine;

public class FineService {
    private static FineService instance;
    private FineDAO fineDAO;
    private BorrowingService borrowingService;
    
    // Fine amount per day of late return (in VND)
    private static final double FINE_RATE_PER_DAY = 5000.0;
    
    private FineService() {
        fineDAO = FineDAO.getInstance();
        borrowingService = BorrowingService.getInstance();
    }
    
    public static FineService getInstance() {
        if (instance == null) {
            instance = new FineService();
        }
        return instance;
    }
    
    public List<Fine> getAllFines() {
        return fineDAO.findAll();
    }
    
    public Optional<Fine> getFineById(String id) {
        return fineDAO.findById(id);
    }
    
    public List<Fine> getFinesByBorrowingId(String borrowingId) {
        return fineDAO.findByBorrowingId(borrowingId);
    }
    
    public List<Fine> getUnpaidFines() {
        return fineDAO.findUnpaidFines();
    }
    
    public boolean createFine(Fine fine) {
        if (fine.getNgayPhat() == null) {
            fine.setNgayPhat(LocalDate.now());
        }
        
        // Remove the call to setNgayHenTra since it doesn't exist in Fine class
        // fine.setNgayHenTra(null);
        
        if (!fine.isDaTra()) {
            fine.setNgayTra(null);
        }
        
        return fineDAO.save(fine);
    }
    
    public boolean updateFine(Fine fine) {
        return fineDAO.update(fine);
    }
    
    public boolean payFine(String fineId) {
        return fineDAO.payFine(fineId);
    }
    
    public boolean deleteFine(String id) {
        return fineDAO.delete(id);
    }
    
    /**
     * Automatically create fines for overdue books when they are returned
     * @param borrowingId The ID of the borrowing record
     * @return true if fine was created successfully, false otherwise
     */
    public boolean createOverdueFine(String borrowingId) {
        Optional<Borrowing> optBorrowing = borrowingService.getBorrowingById(borrowingId);
        
        if (optBorrowing.isPresent()) {
            Borrowing borrowing = optBorrowing.get();
            
            // Check if the book is returned late
            if (borrowing.getNgayTraThucTe() != null && borrowing.getNgayHenTra() != null && 
                borrowing.getNgayTraThucTe().isAfter(borrowing.getNgayHenTra())) {
                
                // Calculate days overdue
                long daysLate = ChronoUnit.DAYS.between(borrowing.getNgayHenTra(), borrowing.getNgayTraThucTe());
                
                // Calculate fine amount
                double fineAmount = daysLate * FINE_RATE_PER_DAY;
                
                // Create fine record
                Fine fine = new Fine();
                fine.setMaMuonTra(borrowingId);
                fine.setSoTien(fineAmount);
                fine.setLyDo("Trả sách trễ " + daysLate + " ngày");
                fine.setNgayPhat(LocalDate.now());
                
                
                return fineDAO.save(fine);
            }
        }
        
        return false;
    }
    
    /**
     * Calculate the total amount of unpaid fines
     * @return The total amount of unpaid fines
     */
    public double getTotalUnpaidFines() {
        return getUnpaidFines().stream()
                .mapToDouble(Fine::getSoTien)
                .sum();
    }
    
    /**
     * Calculate the total amount of fines collected in a given period
     * @param startDate The start date of the period
     * @param endDate The end date of the period
     * @return The total amount of fines collected in the period
     */
    public double getTotalFinesCollected(LocalDate startDate, LocalDate endDate) {
        return getAllFines().stream()
                .filter(fine -> fine.isDaTra() && fine.getNgayTra() != null)
                .filter(fine -> !fine.getNgayTra().isBefore(startDate) && !fine.getNgayTra().isAfter(endDate))
                .mapToDouble(Fine::getSoTien)
                .sum();
    }
    
    /**
     * Gets fines within a specific date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of fines in the date range
     */
    public List<Fine> getFinesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Fine> allFines = fineDAO.findAll();
        return allFines.stream()
                .filter(fine -> !fine.getNgayPhat().isBefore(startDate) && !fine.getNgayPhat().isAfter(endDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets the total amount of fines
     * @return the total fine amount
     */
    public double getTotalFineAmount() {
        List<Fine> allFines = fineDAO.findAll();
        return allFines.stream()
                .mapToDouble(Fine::getSoTienPhat)
                .sum();
    }
    
    /**
     * Gets the total amount of paid fines
     * @return the total paid fine amount
     */
    public double getPaidFineAmount() {
        List<Fine> allFines = fineDAO.findAll();
        return allFines.stream()
                .filter(Fine::isDaTra)
                .mapToDouble(Fine::getSoTienPhat)
                .sum();
    }
    
    /**
     * Gets the total amount of unpaid fines
     * @return the total unpaid fine amount
     */
    public double getUnpaidFineAmount() {
        List<Fine> allFines = fineDAO.findAll();
        return allFines.stream()
                .filter(fine -> !fine.isDaTra())
                .mapToDouble(Fine::getSoTienPhat)
                .sum();
    }
}
