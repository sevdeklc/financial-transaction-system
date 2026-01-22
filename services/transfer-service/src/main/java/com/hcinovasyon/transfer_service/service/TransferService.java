package com.hcinovasyon.transfer_service.service;

import com.hcinovasyon.transfer_service.dto.TransferRequest;
import com.hcinovasyon.transfer_service.entity.Account;
import com.hcinovasyon.transfer_service.entity.Transfer;
import com.hcinovasyon.transfer_service.entity.TransferStatus;
import com.hcinovasyon.transfer_service.repository.AccountRepository;
import com.hcinovasyon.transfer_service.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Transfer createAndExecute(TransferRequest req) {
        Optional<Transfer> existingTransfer = transferRepository.findByRequestId(req.getRequestId());
        if (existingTransfer.isPresent()) {
            return existingTransfer.get();
        }

        Transfer transfer = Transfer.builder()
                .requestId(req.getRequestId())
                .fromAccountId(req.getFromAccountId())
                .toAccountId(req.getToAccountId())
                .amount(req.getAmount())
                .status(TransferStatus.CREATED.name())
                .createdAt(Instant.now())
                .build();

        try {
            transfer = transferRepository.save(transfer);
        } catch (DataIntegrityViolationException e) {
            Optional<Transfer> raceTransfer = transferRepository.findByRequestId(req.getRequestId());
            if (raceTransfer.isPresent()) {
                return raceTransfer.get();
            }
            throw e;
        }

        UUID fromId = req.getFromAccountId();
        UUID toId = req.getToAccountId();
        
        UUID firstId = fromId.compareTo(toId) < 0 ? fromId : toId;
        UUID secondId = fromId.compareTo(toId) < 0 ? toId : fromId;

        Optional<Account> firstAccountOpt = accountRepository.findByIdForUpdate(firstId);
        Optional<Account> secondAccountOpt = accountRepository.findByIdForUpdate(secondId);

        if (firstAccountOpt.isEmpty() || secondAccountOpt.isEmpty()) {
            transfer.setStatus(TransferStatus.FAILED.name());
            transfer = transferRepository.save(transfer);
            throw new IllegalArgumentException("Account not found: " + 
                (firstAccountOpt.isEmpty() ? firstId : secondId));
        }

        Account firstAccount = firstAccountOpt.get();
        Account secondAccount = secondAccountOpt.get();
        
        Account fromAccount;
        Account toAccount;
        if (firstId.equals(fromId)) {
            fromAccount = firstAccount;
            toAccount = secondAccount;
        } else {
            fromAccount = secondAccount;
            toAccount = firstAccount;
        }

        if (fromId.equals(toId)) {
            transfer.setStatus(TransferStatus.FAILED.name());
            return transferRepository.save(transfer);
        }

        if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            transfer.setStatus(TransferStatus.FAILED.name());
            return transferRepository.save(transfer);
        }

        if (fromAccount.getBalance().compareTo(req.getAmount()) < 0) {
            transfer.setStatus(TransferStatus.FAILED.name());
            return transferRepository.save(transfer);
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(req.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(req.getAmount()));
        fromAccount.setUpdatedAt(Instant.now());
        toAccount.setUpdatedAt(Instant.now());
        
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        transfer.setStatus(TransferStatus.COMPLETED.name());
        return transferRepository.save(transfer);
    }
}

