package antifraud;

import antifraud.domain.model.StolenCard;
import antifraud.domain.model.StolenCardFactory;
import antifraud.domain.service.StolenCardService;
import antifraud.rest.controller.CardController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = CardController.class)
@WithMockUser(roles = "SUPPORT")
class GetCardControllerTest {

    @MockBean
    StolenCardService stolenCardServiceMock;

    @Autowired
    private MockMvc mockMvc;

    private final String VALID_CREDIT_CARD = """
            {
               "number": "4000008449433403"
            }""";

    private final String URL = "/api/antifraud/stolencard";

    @Test
    void given_card_number_when_getCardNumbers_return_list_of_all_stolen_credit_cards_200_ok() throws Exception {
        StolenCard stolenCard1 = StolenCardFactory.createWithId(1L, "4000008449433403");
        StolenCard stolenCard2 = StolenCardFactory.createWithId(2L, "12345674");
        StolenCard stolenCard3 = StolenCardFactory.createWithId(3L, "7992739871");

        when(stolenCardServiceMock.showCardNumbers()).thenReturn(List.of(stolenCard1, stolenCard2, stolenCard3));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].number").value("4000008449433403"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].number").value("12345674"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].number").value("7992739871"))

                .andExpect(status().isOk());
    }



    @Test
    @WithMockUser(roles = {
            "ADMINISTRATOR",
            "MERCHANT",
            "INTRUDER"
    })
    void given_unauthorized_user_when_getCardNumbers_then_status_401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREDIT_CARD))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void given_anonymousUser_when_getCardNumbers_then_status_401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREDIT_CARD))
                .andExpect(status().isUnauthorized());
    }

}
