package com.financemanager.webapp.service.impl;

import com.financemanager.webapp.dto.TransactionDTO;
import com.financemanager.webapp.exception.ResourceNotFoundException;
import com.financemanager.webapp.model.Category;
import com.financemanager.webapp.model.Transaction;
import com.financemanager.webapp.model.User;
import com.financemanager.webapp.repository.CategoryRepository;
import com.financemanager.webapp.repository.TransactionRepository;
import com.financemanager.webapp.repository.UserRepository;
import com.financemanager.webapp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Mapper
    private TransactionDTO mapToTransactionDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getCategory().getId(),
                transaction.getCategory().getName() // Include category name
        );
    }

    @Override
    @Transactional
    public TransactionDTO addTransaction(Long userId, TransactionDTO transactionDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Category category = categoryRepository.findByIdAndUserId(transactionDTO.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", transactionDTO.getCategoryId() + " for user " + userId));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setType(transactionDTO.getType());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setDate(transactionDTO.getDate());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionDTO(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(userId);
        return transactions.stream()
                .map(this::mapToTransactionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDTO getTransactionByIdAndUserId(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId + " for user id: " + userId));
        return mapToTransactionDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionDTO updateTransaction(Long userId, Long transactionId, TransactionDTO transactionDTO) {
        // Ensure user exists (optional, as findByIdAndUserId checks user implicitly)
        // User user = userRepository.findById(userId)
        //       .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Transaction existingTransaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId + " for user id: " + userId));

        // If category is being changed, ensure the new category exists and belongs to the user
        if (!existingTransaction.getCategory().getId().equals(transactionDTO.getCategoryId())) {
            Category newCategory = categoryRepository.findByIdAndUserId(transactionDTO.getCategoryId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("New Category", "id", transactionDTO.getCategoryId() + " for user " + userId));
            existingTransaction.setCategory(newCategory);
        }

        // Update fields
        existingTransaction.setType(transactionDTO.getType());
        existingTransaction.setAmount(transactionDTO.getAmount());
        existingTransaction.setDescription(transactionDTO.getDescription());
        existingTransaction.setDate(transactionDTO.getDate());

        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        return mapToTransactionDTO(updatedTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId + " for user id: " + userId));
        transactionRepository.delete(transaction);
    }
}