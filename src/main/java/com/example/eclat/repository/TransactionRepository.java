package com.example.eclat.repository;


import com.example.eclat.entities.Order;
import com.example.eclat.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByVnpTxnRef(String vnpTxnRef);

    Optional<Transaction> findByOrder(Order order);

    List<Transaction> findByTransactionStatusAndExpireAtBefore(String status, LocalDateTime time);

    List<Transaction> findByOrder_User_Id(String userId);

}
