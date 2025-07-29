package com.farmer.Form.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.farmer.Form.DTO.UserDTO;
import com.farmer.Form.DTO.UserViewDTO;
import com.farmer.Form.Entity.User;
import com.farmer.Form.Entity.Role;
import com.farmer.Form.Entity.UserStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Custom method to convert String to LocalDate
    @Named("stringToLocalDate")
    default LocalDate stringToLocalDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            // Handle MM/DD/YYYY format from frontend
            if (dateString.contains("/")) {
                return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            }
            // Handle YYYY-MM-DD format
            return LocalDate.parse(dateString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + dateString);
        }
    }

    // Custom method to convert LocalDate to String
    @Named("localDateToString")
    default String localDateToString(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    // Custom method to convert Role enum to String
    @Named("roleToString")
    default String roleToString(Role role) {
        if (role == null) {
            return null;
        }
        return role.name();
    }

    // Custom method to convert UserStatus enum to String
    @Named("statusToString")
    default String statusToString(UserStatus status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }

    // Mapping DTO to Entity (for registration)
    @Mapping(target = "dateOfBirth", source = "dateOfBirth", qualifiedByName = "stringToLocalDate")
    User toEntity(UserDTO dto);

    // Mapping Entity to View DTO
    @Mapping(target = "dateOfBirth", source = "dateOfBirth", qualifiedByName = "localDateToString")
    UserViewDTO toViewDto(User user);
}
