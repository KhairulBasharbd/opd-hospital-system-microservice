package com.ztrios.opd_auth_service.dto;

import jakarta.validation.constraints.*;

public record RegisterPatientRequest(@NotBlank(message = "Full name is required")
                                             @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
                                             String fullName,

                                     @NotBlank(message = "Email is required")
                                             @Email(message = "Email should be valid")
                                             String email,

                                     @NotBlank(message = "Password is required")
//                                             @Pattern(
//                                                     regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
//                                                     message = "Password must contain at least 8 characters, one uppercase, one lowercase, one number and one special character"
//                                             )
                                             String password

//                                         @NotBlank(message = "Phone number is required")
//                                             //@Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
//                                             String phone,
//
//                                         @NotNull(message = "Date of birth is required")
//                                             @Past(message = "Date of birth must be in the past")
//                                         LocalDate dateOfBirth,
//
//                                         @NotNull(message = "Gender is required")
//                                         Gender gender,
//
//                                         BloodGroup bloodGroup,
//
//                                         @NotBlank(message = "Address is required")
//                                             @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
//                                             String address
) { }
