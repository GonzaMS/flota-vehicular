package com.proyecto.flotavehicular_webapp.utils;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
public class DateRange {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Start date is required")
    private Date startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "End date is required")
    private Date endDate;
}
