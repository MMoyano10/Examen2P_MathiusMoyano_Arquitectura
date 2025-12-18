package com.banquito.branch.holidays.service;

import com.banquito.branch.holidays.dto.BranchHolidayDTO;
import com.banquito.branch.holidays.dto.BranchRequestDTO;
import com.banquito.branch.holidays.dto.BranchResponseDTO;
import com.banquito.branch.holidays.exception.BadRequestException;
import com.banquito.branch.holidays.exception.NotFoundException;
import com.banquito.branch.holidays.model.Branch;
import com.banquito.branch.holidays.model.BranchHoliday;
import com.banquito.branch.holidays.model.BranchState;
import com.banquito.branch.holidays.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;


    public BranchResponseDTO createBranch(BranchRequestDTO dto) {
        Branch branch = Branch.builder()
                .emailAddress(dto.getEmailAddress())
                .name(dto.getName())
                .phoneNumber(dto.getPhoneNumber())
                .state(parseState(dto.getState()))
                .creationDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .branchHolidays(new ArrayList<>())
                .build();

        return mapToResponse(branchRepository.save(branch));
    }

    public BranchResponseDTO getById(String id) {
        return mapToResponse(findBranch(id));
    }

    public List<BranchResponseDTO> getAll() {
        return branchRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public BranchResponseDTO update(String id, BranchRequestDTO dto) {
        Branch branch = findBranch(id);

        branch.setEmailAddress(dto.getEmailAddress());
        branch.setName(dto.getName());
        branch.setPhoneNumber(dto.getPhoneNumber());
        branch.setState(parseState(dto.getState()));
        branch.setLastModifiedDate(OffsetDateTime.now());

        if (dto.getBranchHolidays() != null) {
            branch.setBranchHolidays(mapHolidayDtoList(dto.getBranchHolidays()));
        }

        return mapToResponse(branchRepository.save(branch));
    }

    public void delete(String id) {
        branchRepository.delete(findBranch(id));
    }

    public BranchResponseDTO addHoliday(String branchId, BranchHolidayDTO dto) {
        Branch branch = findBranch(branchId);

        boolean exists = branch.getBranchHolidays().stream()
                .anyMatch(h -> h.getDate().equals(dto.getDate()));

        if (exists) {
            throw new BadRequestException("Holiday already exists");
        }

        BranchHoliday holiday = new BranchHoliday();
        holiday.setDate(dto.getDate());
        holiday.setName(dto.getName());

        branch.getBranchHolidays().add(holiday);
        branch.setLastModifiedDate(OffsetDateTime.now());

        return mapToResponse(branchRepository.save(branch));
    }

    public void deleteHoliday(String branchId, java.time.LocalDate date) {
        Branch branch = findBranch(branchId);
        if (branch.getBranchHolidays() == null || branch.getBranchHolidays().isEmpty()) {
            throw new NotFoundException("No holidays found for branch");
        }

        boolean removed = branch.getBranchHolidays().removeIf(h -> date.equals(h.getDate()));
        if (!removed) {
            throw new NotFoundException("Holiday not found for date: " + date);
        }

        branch.setLastModifiedDate(OffsetDateTime.now());
        branchRepository.save(branch);
    }


    public BranchResponseDTO updatePhoneNumber(String id, String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new BadRequestException("Phone number is required");
        }

        Branch branch = findBranch(id);
        branch.setPhoneNumber(phoneNumber);
        branch.setLastModifiedDate(OffsetDateTime.now());

        return mapToResponse(branchRepository.save(branch));
    }

    private Branch findBranch(String id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Branch not found"));
    }

    private BranchState parseState(String state) {
        if (state == null || state.isBlank()) {
            throw new BadRequestException("State is required");
        }
        try {
            return BranchState.valueOf(state.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid state. Allowed: ACTIVE, INACTIVE");
        }
    }

    private List<BranchHoliday> mapHolidayDtoList(List<BranchHolidayDTO> dtos) {
        if (dtos == null) return new ArrayList<>();

        return dtos.stream()
                .map(d -> {
                    BranchHoliday h = new BranchHoliday();
                    h.setDate(d.getDate());
                    h.setName(d.getName());
                    return h;
                })
                .toList();
    }

    private BranchResponseDTO mapToResponse(Branch b) {
        BranchResponseDTO dto = new BranchResponseDTO();
        dto.setId(b.getId());
        dto.setEmailAddress(b.getEmailAddress());
        dto.setName(b.getName());
        dto.setPhoneNumber(b.getPhoneNumber());
        dto.setState(b.getState() != null ? b.getState().name() : null);
        dto.setCreationDate(b.getCreationDate());
        dto.setLastModifiedDate(b.getLastModifiedDate());

        dto.setBranchHolidays(
                b.getBranchHolidays() == null
                        ? new ArrayList<>()
                        : b.getBranchHolidays().stream()
                        .map(h -> {
                            BranchHolidayDTO hdto = new BranchHolidayDTO();
                            hdto.setDate(h.getDate());
                            hdto.setName(h.getName());
                            return hdto;
                        })
                        .toList()
        );

        return dto;
    }
}