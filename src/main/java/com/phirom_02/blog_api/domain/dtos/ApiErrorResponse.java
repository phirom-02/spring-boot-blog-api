package com.phirom_02.blog_api.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing an API error response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    private int status;
    private String message;
    private List<FieldError> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
}
