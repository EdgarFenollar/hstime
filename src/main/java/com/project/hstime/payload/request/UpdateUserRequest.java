package com.project.hstime.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class UpdateUserRequest {
    @Email
    @Size(min = 5, max = 50)
    private String email;

    @Size(min = 6, max = 40)
    private String password;

    private int IdHotel;

    private String DNI;

    private Set<String> roles;

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdHotel() {
        return IdHotel;
    }

    public void setIdHotel(int idHotel) {
        IdHotel = idHotel;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}