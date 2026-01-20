package com.hcinovasyon.transfer_service.controller;

import com.hcinovasyon.transfer_service.dto.TransferRequest;
import com.hcinovasyon.transfer_service.dto.TransferResponse;
import com.hcinovasyon.transfer_service.entity.Transfer;
import com.hcinovasyon.transfer_service.repository.TransferRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferRepository transferRepository;

    @PostMapping
    public ResponseEntity<TransferResponse> createTransfer(@Valid @RequestBody TransferRequest request) {
        Transfer transfer = Transfer.builder()
                .requestId(request.getRequestId())
                .fromAccountId(request.getFromAccountId())
                .toAccountId(request.getToAccountId())
                .amount(request.getAmount())
                .status("CREATED")
                .createdAt(Instant.now())
                .build();

        Transfer savedTransfer = transferRepository.save(transfer);

        TransferResponse response = TransferResponse.builder()
                .transferId(savedTransfer.getId())
                .status(savedTransfer.getStatus())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

