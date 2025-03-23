package com.library.model;

import java.util.ArrayList;
import java.util.List;

public class Role {
    private String id;
    private String name;
    private String description;
    private List<String> permissions;
    
    public Role() {
        this.permissions = new ArrayList<>();
    }
    
    public Role(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.permissions = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
    
    public void addPermission(String permission) {
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
    }
    
    public void removePermission(String permission) {
        permissions.remove(permission);
    }
    
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
}