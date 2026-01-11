package com.pm.patientservice.dto;

import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

//DTO is used for 1)Data hiding from user 2)Performance (Selective Data) Patient Entity mein ek bada Photo blob ya bahut saari internal details hain.
//
//Agar tum list of patients mangte ho, toh tum nahi chahoge ki har patient ke saath unki puri history aur photo load ho (heavy memory).

public class PatientRequestDTO {

    @NotBlank // checks 1)Null toh ni 2)Length 0 toh ni 3)sirf khaali spaces to ni. Stronger than @NotNull
    @Size(max = 100, message = "name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;


    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;

    // groups = CreatePatientValidationGroup.class iska mtlb ye bs user create krte waqt validate hoga update krte waqt ni
    @NotBlank(groups = CreatePatientValidationGroup.class, message =
            "Registered date is required")
    private String registeredDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }





}
