package com.example.eclat.service;


import com.example.eclat.mapper.OrderDetailMapper;
import com.example.eclat.model.request.OrderDetailRequest;
import com.example.eclat.model.response.OrderDetailResponse;
import com.example.eclat.repository.OptionRepository;
import com.example.eclat.repository.OrderDetailRepository;
import com.example.eclat.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderDetailService {

    OrderDetailRepository orderDetailRepository;
    OrderRepository orderRepository;
    OptionRepository optionRepository;

    OrderDetailMapper orderDetailMapper;

//    public OrderDetailResponse createOrderDetail(OrderDetailRequest resquest){
//
//    }

}
