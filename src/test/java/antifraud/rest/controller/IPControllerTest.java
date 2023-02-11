package antifraud.rest.controller;

import antifraud.config.security.DelegatedAuthenticationEntryPoint;
import antifraud.config.security.DelegatedSecurityConfig;
import antifraud.domain.model.IP;
import antifraud.domain.model.IPFactory;
import antifraud.domain.service.SuspiciousIPService;
import antifraud.exceptionhandler.ExceptionConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = IPController.class)
@Import({DelegatedAuthenticationEntryPoint.class, DelegatedSecurityConfig.class})
@WithMockUser(roles = "SUPPORT")
class IPControllerTest {
    private static final String URL = "http://localhost:28852/api/antifraud/suspicious-ip";
    private static final String VALID_CONTENT = "{\"ip\":\"196.168.01.1\"}";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SuspiciousIPService suspiciousIPService;
    private IP ip;

    @BeforeEach
    void setup(WebApplicationContext wac) {
        this.ip = IPFactory.createWithId(1L, "196.168.01.1");
    }

    @Test
    void WhenSavingNonExistentIpAddressThenReturnSavedIp() throws Exception {
        given(suspiciousIPService.saveSuspiciousAddress(any(IP.class))).willReturn(Optional.of(ip));

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CONTENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ip.getId()))
                .andExpect(jsonPath("$.ip").value(ip.getIpAddress()));
    }

    @Test
    void WhenSavingExistentIpAddressThenReturnStatus409() throws Exception {
        given(suspiciousIPService.saveSuspiciousAddress(any(IP.class))).willReturn(Optional.empty());
        String exceptionMessage = String.format("{'status':'%s'}", ExceptionConstants.EXISTING_IP);

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CONTENT))
                .andExpect(status().isConflict())
                .andExpect(content().json(exceptionMessage));
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRATOR", "MERCHANT"})
    void WhenAccessWithIncorrectRoleThenReturnStatus403() throws Exception {
        // accessing API with incorrect role
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CONTENT))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void WhenAccessWithAnonymousUserThenReturnStatus401() throws Exception {
        // accessing API with unauthorized user
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CONTENT))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void WhenGivingEmptyBodyThenReturnMethodArgumentNotValidException() throws Exception {
        String emptyBody = "{}";

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyBody))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }

    @Test
    void WhenRequestBodyIsAbsentThenReturnHttpMessageNotReadableException() throws Exception {
        // absent request body
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException));
    }

    @Test
    void WhenSavingInvalidIpAddressThenReturnStatus400() throws Exception {
        String invalidIP = "{\"ip\":\"196.168.01\"}";

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidIP))
                .andExpect(status().isBadRequest());
    }

    @Test
    void WhenUsingIncorrectHTTPMethodThenReturnStatus500() throws Exception {
        // using incorrect HTTP method
        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CONTENT))
                .andExpect(status().isInternalServerError());
    }
}