package com.farmer.Form.DTO;

import com.farmer.Form.Entity.Company;
import lombok.Data;

@Data
public class CompanyCreationRequest {
    private String name;
    private String shortName;
    private String email;
    private String phone;
    private String defaultTimezone;
    private String status; // ACTIVE | INACTIVE

    private String address;

    // Admin credentials (optional)
    private String adminEmail;
    private String adminPassword;

    public Company toCompany() {
        Company c = new Company();
        c.setName(name);
        c.setShortName(shortName);
        c.setEmail(email);
        c.setPhone(phone);
        c.setDefaultTimezone(defaultTimezone);
        try {
            c.setStatus(Company.Status.valueOf(status == null ? "ACTIVE" : status.toUpperCase()));
        } catch (Exception ignored) {
            c.setStatus(Company.Status.ACTIVE);
        }
        return c;
    }
}


