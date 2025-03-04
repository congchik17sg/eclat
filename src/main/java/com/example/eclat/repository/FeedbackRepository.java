package com.example.eclat.repository;

import com.example.eclat.entities.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedBack, Long> {


    List<FeedBack> findByUserId(String userId); // Truy vấn theo userId

    @Query("SELECT f FROM FeedBack f WHERE f.orderDetail.productOption.product.productId = :productId")
    List<FeedBack> findByProductId(@Param("productId") Long productId);

    List<FeedBack> findByOrderDetail_ProductOption_Product_ProductId(Long productId);

}
