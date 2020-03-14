package com.datta.app.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class LockRequest {
    Long userId;
    Long documentId;
}
