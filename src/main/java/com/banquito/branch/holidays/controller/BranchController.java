package com.banquito.branch.holidays.controller;

import com.banquito.branch.holidays.dto.BranchHolidayDTO;
import com.banquito.branch.holidays.dto.BranchRequestDTO;
import com.banquito.branch.holidays.dto.BranchResponseDTO;
import com.banquito.branch.holidays.service.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
@Tag(name = "Branches", description = "Branch Holidays API")
public class BranchController {

    private static final Logger log = LoggerFactory.getLogger(BranchController.class);

    private final BranchService branchService;

    @Operation(summary = "List all branches")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping
    public ResponseEntity<List<BranchResponseDTO>> getAllBranches() {
        log.info("GET /api/v1/branches - list all branches");
        return ResponseEntity.ok(branchService.getAll());
    }

    @Operation(summary = "Create a branch (without holidays)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping
    public ResponseEntity<BranchResponseDTO> createBranch(@Valid @RequestBody BranchRequestDTO dto) {
        log.info("POST /api/v1/branches - create branch");
        BranchResponseDTO created = branchService.createBranch(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Get branch by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BranchResponseDTO> getBranchById(@PathVariable String id) {
        log.info("GET /api/v1/branches/{} - get branch by id", id);
        return ResponseEntity.ok(branchService.getById(id));
    }

    @Operation(summary = "Update phone number only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PatchMapping("/{id}/phone")
    public ResponseEntity<BranchResponseDTO> updatePhone(
            @PathVariable String id,
            @Parameter(description = "New phone number", example = "0999999999")
            @RequestParam String phoneNumber) {

        log.info("PATCH /api/v1/branches/{}/phone - update phone number", id);
        return ResponseEntity.ok(branchService.updatePhoneNumber(id, phoneNumber));
    }

    @Operation(summary = "Add a holiday to a branch")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PostMapping("/{id}/holidays")
    public ResponseEntity<BranchResponseDTO> addHoliday(
            @PathVariable String id,
            @Valid @RequestBody BranchHolidayDTO dto) {

        log.info("POST /api/v1/branches/{}/holidays - add holiday", id);
        return ResponseEntity.ok(branchService.addHoliday(id, dto));
    }

    @Operation(summary = "Delete a holiday from a branch by date")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @DeleteMapping("/{id}/holidays/{date}")
    public ResponseEntity<Void> deleteHoliday(
            @PathVariable String id,
            @Parameter(description = "Holiday date (ISO-8601)", example = "2025-12-25")
            @PathVariable LocalDate date) {

        log.info("DELETE /api/v1/branches/{}/holidays/{} - delete holiday", id, date);
        branchService.deleteHoliday(id, date);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List holidays of a branch")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}/holidays")
    public ResponseEntity<List<BranchHolidayDTO>> getHolidays(@PathVariable String id) {
        log.info("GET /api/v1/branches/{}/holidays - list holidays", id);
        return ResponseEntity.ok(branchService.getById(id).getBranchHolidays());
    }

    @Operation(summary = "Check if a date is a holiday for a branch")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}/holidays/check")
    public ResponseEntity<Boolean> isHoliday(
            @PathVariable String id,
            @Parameter(description = "Date to check (ISO-8601)", example = "2025-01-01")
            @RequestParam LocalDate date) {

        log.info("GET /api/v1/branches/{}/holidays/check?date={} - check holiday", id, date);

        boolean isHoliday = branchService.getById(id).getBranchHolidays().stream()
                .anyMatch(h -> h.getDate().equals(date));

        return ResponseEntity.ok(isHoliday);
    }
}