package com.financemanager.webapp.service;

import com.financemanager.webapp.dto.TransactionDTO;

import java.util.List;

public interface TransactionService {
    TransactionDTO addTransaction(Long userId, TransactionDTO transactionDTO);
    List<TransactionDTO> getTransactionsByUserId(Long userId);
    TransactionDTO getTransactionByIdAndUserId(Long transactionId, Long userId);
    TransactionDTO updateTransaction(Long userId, Long transactionId, TransactionDTO transactionDTO);
    void deleteTransaction(Long userId, Long transactionId);
}