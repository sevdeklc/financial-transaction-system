package com.hcinovasyon.transfer_service.repository;

import com.hcinovasyon.transfer_service.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, UUID> {
    Optional<Transfer> findByRequestId(UUID requestId);
}

