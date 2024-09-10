package com.proyecto.flotavehicular_webapp.dto;


import com.proyecto.flotavehicular_webapp.enums.ESTATES;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long userId;

    @NotBlank(message = "Username is mandatory")
    private String userName;

    @NotBlank(message = "Password is mandatory")
    private String userPassword;

    @NotBlank(message = "Email is mandatory")
    private String userEmail;

    @Enumerated(EnumType.STRING)
    @NotBlank(message = "State is mandatory")
    private ESTATES userState;

    private Long conductorId;
}
