package antifraud.rest.dto;

import antifraud.domain.model.CustomUser;
import antifraud.domain.model.CustomUserFactory;
import antifraud.domain.model.enums.UserAccess;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserStatusDTO(@NotBlank
                            @JsonProperty(access = JsonProperty.Access.READ_ONLY)
                            String username,
                            @NotNull
                            @JsonProperty(access = JsonProperty.Access.READ_ONLY)
                            UserAccess access)
                           {

    public CustomUser toModel() {
        return CustomUserFactory.createWithAccess(username, access);
    }
}