package antifraud.rest.controller;

import antifraud.domain.model.StolenCard;
import antifraud.domain.model.StolenCardFactory;
import antifraud.domain.service.StolenCardService;
import antifraud.exceptions.CardNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
class CardControllerTest {

    @MockBean
    private StolenCardService stolenCardService;
    @Autowired
    private MockMvc mockMvc;

    String api = "http://localhost:28852/api/antifraud/stolencard";
    private final String validCardNumber = "374245455400126";

    private final String invalidCardNumber = "6250941006528588";

    @Test
    @AutoConfigureMockMvc(addFilters = false)
    @WithMockUser(username = "veselin", authorities = "SUPPORT")
    void addingStolenCardFailsWhenInvalidNumber() throws Exception {
        mockMvc.perform(post(api)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"number\" : \"" + invalidCardNumber + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @AutoConfigureMockMvc(addFilters = false)
    @WithMockUser(username = "veselin", authorities = "SUPPORT")
    void addingStolenCardToRepoWhenWithValidNumber() throws Exception {
        StolenCard customStolenCard = new StolenCard(1L,validCardNumber);
        Optional<StolenCard> optionalStolenCard = Optional.of(customStolenCard);

        when(stolenCardService.storeStolenCardNumber(any())).thenReturn(optionalStolenCard);

        mockMvc.perform(post(api)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"number\" : \"" + validCardNumber + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect( jsonPath("$.number").value(validCardNumber));;
    }


    @Test
    @AutoConfigureMockMvc(addFilters = false)
    @WithMockUser(username = "veselin", authorities = "SUPPORT")
    void addingStolenCardToRepoWhenDuplicateNumber() throws Exception {
        StolenCard customStolenCard = StolenCardFactory.create(validCardNumber);
        Optional<StolenCard> optionalStolenCard = Optional.of(customStolenCard);

        when(stolenCardService.storeStolenCardNumber(customStolenCard)).thenReturn(optionalStolenCard);

        mockMvc.perform(post(api)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"number\" : \"" + validCardNumber + "\"}"))
                .andExpect(status().isConflict());
    }


    @Test
    @AutoConfigureMockMvc(addFilters = false)
    @WithMockUser(username = "veselin", authorities = "SUPPORT")
    void deleteStolenCardFailsWhenInvalidNumber() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(api + "/{id}", invalidCardNumber)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @AutoConfigureMockMvc(addFilters = false)
    @WithMockUser(username = "veselin", authorities = "SUPPORT")
    void deleteStolenCardWhenNumberIsValidButCardIsNotFound() throws Exception {

        doThrow(CardNotFoundException.class).doNothing().when(stolenCardService).removeCardNumber(validCardNumber);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(api + "/{id}", validCardNumber)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @AutoConfigureMockMvc(addFilters = false)
    @WithMockUser(username = "veselin", authorities = "SUPPORT")
    void deleteStolenCardWhenNumberIsValidAndDeletes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(api + "/{id}", validCardNumber)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "veselin", authorities = "SUPPORT")
    void getStolenCardNumbersShouldBeOk() throws Exception {
        List<StolenCard> customCardNumbers = List.of(new StolenCard(1L, "374245455400126"), new StolenCard(2L, "378282246310005"));

        when(stolenCardService.showCardNumbers()).thenReturn(customCardNumbers);

        mockMvc.perform(get(api).with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].number").value("374245455400126"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].number").value("378282246310005"))
                .andExpect((jsonPath("$", hasSize(2))));
    }
}