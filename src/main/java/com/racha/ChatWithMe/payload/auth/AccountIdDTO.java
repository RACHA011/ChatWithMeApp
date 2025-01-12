package com.racha.ChatWithMe.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountIdDTO {
    @Schema(example = "2", description = "Account id", requiredMode = RequiredMode.REQUIRED)
    public String id;
}
