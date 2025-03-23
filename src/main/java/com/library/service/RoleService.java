package com.library.service;

import com.library.dao.RoleDAO;
import com.library.model.Role;

import java.util.List;
import java.util.Optional;

public class RoleService {
    private static RoleService instance;
    private final RoleDAO roleDAO;
    
    private RoleService() {
        roleDAO = RoleDAO.getInstance();
    }
    
    public static RoleService getInstance() {
        if (instance == null) {
            instance = new RoleService();
        }
        return instance;
    }
    
    public List<Role> getAllRoles() {
        return roleDAO.findAll();
    }
    
    public Optional<Role> getRoleById(String id) {
        return roleDAO.findById(id);
    }
    
    public Optional<Role> getRoleByName(String name) {
        return roleDAO.findByName(name);
    }
    
    public boolean saveRole(Role role) {
        return roleDAO.save(role);
    }
    
    public boolean updateRole(Role role) {
        return roleDAO.update(role);
    }
    
    public boolean deleteRole(String id) {
        return roleDAO.delete(id);
    }
    
    public List<String> getAllPermissions() {
        // Return a list of all available permissions in the system
        return List.of(
            "USER_VIEW", "USER_CREATE", "USER_EDIT", "USER_DELETE",
            "BOOK_VIEW", "BOOK_CREATE", "BOOK_EDIT", "BOOK_DELETE",
            "BORROW_VIEW", "BORROW_CREATE", "BORROW_EDIT", "BORROW_DELETE",
            "READER_VIEW", "READER_CREATE", "READER_EDIT", "READER_DELETE",
            "REPORT_VIEW", "REPORT_GENERATE",
            "SYSTEM_CONFIG"
        );
    }
}