package antifraud.controller.user;

import antifraud.rest.dto.UserAccessDTO;
import antifraud.rest.dto.UserDTO;
import antifraud.rest.dto.UserRoleDTO;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class UserRequestBuilder {

    private final MockMvc mockMvc;

    public UserRequestBuilder(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    ResultActions createUser(UserDTO input) throws Exception {
        return mockMvc.perform(post("api/auth/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(input)));
    }

    ResultActions getUsers() throws Exception {
        return mockMvc.perform(get("api/auth/list"));
    }

    ResultActions deleteUser(String username) throws Exception {
        return mockMvc.perform(delete("api/auth/user/{username}", username));
    }

    ResultActions changeUserRole(UserRoleDTO input) throws Exception {
        return mockMvc.perform(put("api/auth/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(input)));
    }

    ResultActions grantAccess(UserAccessDTO input) throws Exception {
        return mockMvc.perform(put("api/auth/access")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(input)));
    }
}
