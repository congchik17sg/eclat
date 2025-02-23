package com.example.eclat.model.request;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults( level = AccessLevel.PRIVATE)
public class TransactionRequest implements Serializable {

        String status;
        String message;
        String URL;

}
