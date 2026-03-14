package com.codedemonbr.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HelloResponse {
    private String message;
    private String status;
}
