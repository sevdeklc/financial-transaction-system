package com.hcinovasyon.transfer_service.controller;

import com.hcinovasyon.transfer_service.dto.TransferRequest;
import com.hcinovasyon.transfer_service.dto.TransferResponse;
import com.hcinovasyon.transfer_service.entity.Transfer;
import com.hcinovasyon.transfer_service.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
@Tag(name = "Transfers", description = "API for creating and managing money transfers between accounts")
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    @Operation(
            summary = "Create a new transfer",
            description = "Creates a new money transfer from one account to another. The transfer is idempotent based on requestId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transfer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<TransferResponse> createTransfer(@Valid @RequestBody TransferRequest request) {
        Transfer transfer = transferService.createAndExecute(request);

        TransferResponse response = TransferResponse.builder()
                .transferId(transfer.getId())
                .status(transfer.getStatus())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

