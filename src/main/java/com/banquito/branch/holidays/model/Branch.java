package com.banquito.branch.holidays.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "branches")
public class Branch {

    @Id
    private String id;

    private String emailAddress;
    private String name;
    private String phoneNumber;

    private BranchState state;

    private OffsetDateTime creationDate;
    private OffsetDateTime lastModifiedDate;

    @Builder.Default
    private List<BranchHoliday> branchHolidays = new ArrayList<>();
}