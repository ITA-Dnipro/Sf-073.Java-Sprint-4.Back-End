package antifraud;

import antifraud.domain.service.StolenCardService;
import antifraud.rest.controller.CardController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = CardController.class)
@WithMockUser(roles = "SUPPORT")
class DeleteCardControllerTest {

    @MockBean
    StolenCardService stolenCardServiceMock;

    @Autowired
    private MockMvc mockMvc;

    private final String VALID_CARD_NUMBER = "4000008449433403";
    private final String INVALID_CARD_NUMBER = "4000008449433402";

    private final String VALID_CREDIT_CARD = """
            {
               "number": "4000008449433403"
            }""";

    private final String URL = "/api/antifraud/stolencard/";

    @Test
    void given_valid_card_number_when_deleteCardNumber_then_status_200_OK() throws Exception {
        doNothing().when(stolenCardServiceMock)
                .removeCardNumber(VALID_CARD_NUMBER);

        String urlCardNumber = URL + VALID_CARD_NUMBER;

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(urlCardNumber)
                        .content(VALID_CARD_NUMBER)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("*")
                        .value(String.format("Card %s successfully removed!", VALID_CARD_NUMBER))
                );
    }
    @Test
    void given_invalid_card_number_when_deleteCardNumber_then_status_400() throws Exception {
        doNothing().when(stolenCardServiceMock)
                .removeCardNumber(INVALID_CARD_NUMBER);

        String urlCardNumber = URL + INVALID_CARD_NUMBER;

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(urlCardNumber)
                        .content(INVALID_CARD_NUMBER)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {
            "ADMINISTRATOR",
            "MERCHANT",
            "INTRUDER"
    })
    void given_unauthorized_user_when_deleteStolenCard_then_status_403() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREDIT_CARD))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void given_anonymousUser_when_saveStolenCard_then_status_403() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREDIT_CARD))
                .andExpect(status().isForbidden());
    }
}
