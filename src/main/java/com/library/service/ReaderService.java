package com.library.service;

import java.util.List;
import java.util.Optional;

import com.library.dao.ReaderDAO;
import com.library.model.Reader;

public class ReaderService {
    private static ReaderService instance;
    private ReaderDAO readerDAO;
    
    private ReaderService() {
        // Use getInstance() instead of the constructor
        readerDAO = ReaderDAO.getInstance();
    }
    
    public static ReaderService getInstance() {
        if (instance == null) {
            instance = new ReaderService();
        }
        return instance;
    }
    
    public List<Reader> getAllReaders() {
        return readerDAO.findAll();
    }
    
    public Optional<Reader> getReaderById(String id) {
        return readerDAO.findById(id);
    }
    
    public List<Reader> searchReaders(String keyword) {
        return readerDAO.search(keyword);
    }
    
    public boolean addReader(Reader reader) {
        return readerDAO.save(reader);
    }
    
    public boolean updateReader(Reader reader) {
        return readerDAO.update(reader);
    }
    
    public boolean deleteReader(String id) {
        return readerDAO.delete(id);
    }
    
    public int getTotalReaders() {
        return readerDAO.countAll();
    }
}
