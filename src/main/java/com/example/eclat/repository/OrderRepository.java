package com.example.eclat.repository;

import com.example.eclat.entities.Order;
import com.example.eclat.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
}
