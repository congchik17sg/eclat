package com.example.eclat.repository;


import com.example.eclat.entities.Order;
import com.example.eclat.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByVnpTxnRef(String vnpTxnRef);

    Optional<Transaction> findByOrder(Order order);
}
