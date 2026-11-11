package com.eurder.eurder.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EurderRepository extends JpaRepository<Eurder, UUID> {
}