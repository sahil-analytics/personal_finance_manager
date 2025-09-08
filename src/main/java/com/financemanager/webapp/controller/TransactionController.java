package com.financemanager.webapp.controller;

import com.financemanager.webapp.dto.TransactionDTO;
import com.financemanager.webapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/transactions") // Transactions are user-specific
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDTO> addTransaction(@PathVariable Long userId, @RequestBody TransactionDTO transactionDTO) {
        TransactionDTO createdTransaction = transactionService.addTransaction(userId, transactionDTO);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getUserTransactions(@PathVariable Long userId) {
        // Consider adding pagination/filtering parameters here later (e.g., month, year, type)
        List<TransactionDTO> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long userId, @PathVariable Long transactionId) {
        TransactionDTO transaction = transactionService.getTransactionByIdAndUserId(transactionId, userId);
        if (transaction != null) {
            return ResponseEntity.ok(transaction);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long userId, @PathVariable Long transactionId, @RequestBody TransactionDTO transactionDTO) {
        TransactionDTO updatedTransaction = transactionService.updateTransaction(userId, transactionId, transactionDTO);
        if (updatedTransaction != null) {
            return ResponseEntity.ok(updatedTransaction);
        } else {
            return ResponseEntity.notFound().build(); // Or handle validation errors
        }
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long userId, @PathVariable Long transactionId) {
        transactionService.deleteTransaction(userId, transactionId);
        return ResponseEntity.noContent().build();
    }
}