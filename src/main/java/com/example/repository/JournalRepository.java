package com.example.repository;

import com.example.model.Journal;
import org.springframework.data.repository.CrudRepository;

public interface JournalRepository extends CrudRepository<Journal, Long> {
}
