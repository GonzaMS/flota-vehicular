package com.proyecto.flotavehicular_webapp.dto;

import com.proyecto.flotavehicular_webapp.enums.EORDERSSTATE;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Generated;

import java.time.LocalDateTime;

@Data
@Builder
public class TravelOrderDTO {
    private Long travelOrderId;
    private @NotBlank(
            message = "Client is required"
    ) String client;
    private @NotNull(
            message = "Travel leave date is required"
    ) LocalDateTime travelLeaveDate;
    private @NotNull(
            message = "Travel arrive date is required"
    ) LocalDateTime travelArriveDate;
    private @NotNull(
            message = "Travel order state is required"
    ) EORDERSSTATE travelOrderState;
}

