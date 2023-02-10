package antifraud.domain.service.controllers;

import antifraud.domain.model.IP;
import antifraud.domain.service.SuspiciousIPService;
import antifraud.rest.controller.IPController;
import antifraud.rest.dto.CustomMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = IPController.class)
@ExtendWith(MockitoExtension.class)
class IPControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SuspiciousIPService suspiciousIPService;

    @Autowired
    ObjectMapper objectMapper;
    private IP validIp;
    private IP invalidIp;
    private List<IP> ipList;
    private static final String URL = "/api/antifraud/suspicious-ip";

    @BeforeEach
    void setUp() {
        validIp = new IP(1L, "19.117.63.126");
        invalidIp = new IP(2L, "101.5. 40.1");
        ipList = List.of(validIp, new IP(3L, "19.117.63.123"));
    }


    @Nested
    class DeleteAddressTests {

        @Test
        @WithMockUser(username = "georgi", authorities = {"ROLE_SUPPORT"})
        void should_Return_StatusOK_When_IPSuccessfullyDeleted() throws Exception {
            doNothing().when(suspiciousIPService).removeIpAddress(anyString());

            mockMvc.perform(MockMvcRequestBuilders
                            .delete(URL + "/" + validIp.getIpAddress())
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validIp.getIpAddress()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("*").value(String.format("IP %s successfully removed!", validIp.getIpAddress())));
        }

        @Test
        void should_ReturnUnauthorized_When_RoleSupportIsNotProvided() throws Exception {
            doNothing().when(suspiciousIPService).removeIpAddress(anyString());

            mockMvc.perform(MockMvcRequestBuilders
                            .delete(URL + "/" + validIp.getIpAddress())
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validIp.getIpAddress()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "georgi", authorities = {"ROLE_SUPPORT"})
        void should_ReturnBadRequest_When_IPFormatIsInvalid() throws Exception {
            doNothing().when(suspiciousIPService).removeIpAddress(anyString());

            mockMvc.perform(MockMvcRequestBuilders
                            .delete(URL + "/" + invalidIp.getIpAddress())
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validIp.getIpAddress()))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetIpAddressesTests {

        @Test
        @WithMockUser(username = "georgi", authorities = {"ROLE_SUPPORT"})
        void should_ReturnListOfIPs_When_Requested() throws Exception {
            when(suspiciousIPService.showIpAddresses()).thenReturn(ipList);

            mockMvc.perform(MockMvcRequestBuilders
                            .get(URL)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].ip").value("19.117.63.126"))
                    .andExpect(jsonPath("$[1].id").value(3))
                    .andExpect(jsonPath("$[1].ip").value("19.117.63.123"));
        }

        @Test
        void should_ReturnUnauthorized_When_RoleSupportIsNotProvided() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                            .get(URL)
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }

}
