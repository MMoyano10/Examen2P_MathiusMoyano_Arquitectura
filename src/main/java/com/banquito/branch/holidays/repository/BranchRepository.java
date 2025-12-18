package com.banquito.branch.holidays.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.banquito.branch.holidays.model.Branch;

public interface BranchRepository extends MongoRepository<Branch, String> {
}
