package antifraud.rest.dto;

import antifraud.domain.model.CustomUser;
import antifraud.domain.model.enums.UserAccess;
import lombok.Builder;

@Builder
public record UserStatusDTO(String username,
                            UserAccess status) {

    public static UserStatusDTO fromModel(CustomUser userPermission) {

        return UserStatusDTO.builder()
                .username(userPermission.getUsername())
                .status(userPermission.getAccess())
                .build();
    }
}
