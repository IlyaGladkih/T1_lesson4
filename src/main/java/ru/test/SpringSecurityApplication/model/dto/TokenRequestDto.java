package ru.test.SpringSecurityApplication.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class TokenRequestDto {
    private String name;
    private String password;
}
