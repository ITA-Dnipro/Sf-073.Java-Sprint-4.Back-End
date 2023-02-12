package antifraud;


import antifraud.domain.model.StolenCard;
import antifraud.domain.model.StolenCardFactory;
import antifraud.domain.service.StolenCardService;

import antifraud.exceptionhandler.ExceptionConstants;
import antifraud.exceptions.ExistingCardException;
import antifraud.rest.controller.CardController;

import antifraud.rest.dto.CardDTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
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

import java.util.Optional;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = CardController.class)
@WithMockUser(roles = "SUPPORT")
class PostStolenCardControllerTest {

    @MockBean
    StolenCardService stolenCardServiceMock;

    @Autowired
    private MockMvc mockMvc;

    private final String CARD_NUMBER = "4000008449433403";

    private final String VALID_CREDIT_CARD = """
            {
               "number": "4000008449433403"
            }""";
    private final String INVALID_CARD_NUMBER = """
            {
               "number": "4000008449433402"
            }""";
    private final String URL = "/api/antifraud/stolencard";


    @Test
    void given_valid_stolenCard_when_saveStolenCard_then_status_200() throws Exception {
        StolenCard stolenCard = StolenCardFactory.create(CARD_NUMBER);

        when(stolenCardServiceMock.storeStolenCardNumber(any())).thenReturn(Optional.of(stolenCard));

        CardDTO expected = CardDTO.fromModel(stolenCard);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREDIT_CARD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.id()))
                .andExpect(jsonPath("$.number").value(expected.number()));

    }

    @Test
    void given_same_valid_stolenCard_when_saveStolenCard_then_status_409() throws Exception {
        doThrow(ExistingCardException.class)
                .when(stolenCardServiceMock).storeStolenCardNumber(any());

        String exceptionMsg = String.format(
                "{'status':'%s'}",
                ExceptionConstants.EXISTING_CARD
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREDIT_CARD))
                .andExpect(status().isConflict())
                .andExpect(content().json(exceptionMsg));
    }

    @Test
    @WithMockUser(roles = {
            "ADMINISTRATOR",
            "MERCHANT",
            "INTRUDER"
    })
    void given_unauthorized_user_when_saveStolenCard_then_status_403() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREDIT_CARD))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void given_anonymousUser_when_saveStolenCard_then_status_403() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREDIT_CARD))
                .andExpect(status().isForbidden());
    }

    @Test
    void given_invalid_card_number_when_saveStolenCard_then_status_403() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_CARD_NUMBER))
                .andExpect(status().isForbidden());
    }

    @Test
    void given_card_number_when_saveStolenCard_then_valid_business_logic() throws Exception {
        StolenCard stolenCard = StolenCardFactory.create(CARD_NUMBER);
        ArgumentCaptor<StolenCard> stolenCardArgumentCaptor = ArgumentCaptor.forClass(StolenCard.class);
        given(stolenCardServiceMock.storeStolenCardNumber(any()))
                .willReturn(Optional.of(stolenCard));

        mockMvc.perform(MockMvcRequestBuilders
                .post(URL)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_CREDIT_CARD));

        then(stolenCardServiceMock).should(times(1))
                .storeStolenCardNumber(stolenCardArgumentCaptor.capture());

        String expectedStolenCardNumber = stolenCardArgumentCaptor.getValue().getNumber();

        assertEquals("Assert equals ", CARD_NUMBER, expectedStolenCardNumber);
        assertNull("Is Null ", stolenCardArgumentCaptor.getValue().getId());
    }

}
