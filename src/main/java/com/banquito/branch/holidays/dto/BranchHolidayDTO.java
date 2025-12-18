package com.banquito.branch.holidays.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BranchHolidayDTO {

    @NotNull(message = "Holiday date is required")
    private LocalDate date;

    @NotBlank(message = "Holiday name is required")
    private String name;

    public BranchHolidayDTO() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
