package com.library.model;

import java.time.LocalDate;

public class Borrowing {
    private String id;
    private String readerId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    private String notes;
    
    // Constructors
    public Borrowing() {
    }
    
    public Borrowing(String id, String readerId, LocalDate borrowDate, LocalDate dueDate) {
        this.id = id;
        this.readerId = readerId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = "ACTIVE";
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getReaderId() {
        return readerId;
    }
    
    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }
    
    public LocalDate getBorrowDate() {
        return borrowDate;
    }
    
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // Helper methods
    public boolean isOverdue() {
        if (returnDate != null) {
            return false; // Already returned
        }
        return LocalDate.now().isAfter(dueDate);
    }
    
    public boolean isReturned() {
        return returnDate != null;
    }
    
    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
    
    @Override
    public String toString() {
        return "Borrowing{" +
                "id='" + id + '\'' +
                ", readerId='" + readerId + '\'' +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", status='" + status + '\'' +
                '}';
    }
}