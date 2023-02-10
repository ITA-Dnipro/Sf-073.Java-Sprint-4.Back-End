package antifraud.rest.controller;

import antifraud.domain.model.Transaction;
import antifraud.domain.model.TransactionFactory;
import antifraud.domain.model.enums.TransactionResult;
import antifraud.domain.model.enums.WorldRegion;
import antifraud.domain.service.CustomUserService;
import antifraud.domain.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(TransactionController.class)
public class TestTransactionController {
    private static final String URL = "http://localhost:28852/api/antifraud";
    @MockBean
    CustomUserService userService;
    @MockBean
    private TransactionService transactionService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test_makeTransaction_when_userIsAuthorized_should_return_UNAUTHORIZED() throws Exception {
        Transaction result = new Transaction();
        result.setTransactionResult(TransactionResult.ALLOWED);
        result.setTransactionInfo("none");

        when(transactionService.processTransaction(any())).thenReturn(result);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL + "/transaction")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content("""
                                                                                     {
                                                                                         "amount": 150,
                                                                                         "ip": "192.168.1.4",
                                                                                         "number": "4000008449433403",
                                                                                         "region": "SSA",
                                                                                         "date": "2022-01-22T16:02:00"
                                                                                     }"""))
                                     .andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @WithMockUser(username = "merchant", authorities = {"ROLE_MERCHANT"})
    public void test_makeTransaction_when_userIsAuthorized_should_return_OK() throws Exception {
        Transaction result = new Transaction();
        result.setTransactionResult(TransactionResult.ALLOWED);
        result.setTransactionInfo("none");

        when(transactionService.processTransaction(any())).thenReturn(result);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL + "/transaction")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content("""
                                                                                     {
                                                                                         "amount": 150,
                                                                                         "ip": "192.168.1.4",
                                                                                         "number": "4000008449433403",
                                                                                         "region": "SSA",
                                                                                         "date": "2022-01-22T16:02:00"
                                                                                     }"""))
                                     .andReturn();

        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.OK.value()));
    }

    @Test
    @WithMockUser(username = "merchant", authorities = {"ROLE_MERCHANT"})
    public void test_makeTransaction_when_AmountIsNull_should_return_BADREQUEST() throws Exception {
        Transaction result = new Transaction();
        result.setTransactionResult(TransactionResult.ALLOWED);
        result.setTransactionInfo("none");

        when(transactionService.processTransaction(any())).thenReturn(result);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL + "/transaction")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content("""
                                                                                     {
                                                                                         "ip": "192.168.1.4",
                                                                                         "number": "",
                                                                                         "region": "SSA",
                                                                                         "date": "2022-01-22T16:02:00"
                                                                                     }"""))
                                     .andReturn();

        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithMockUser(username = "merchant", authorities = {"ROLE_MERCHANT"})
    public void test_makeTransaction_when_IPIsBlanc_should_return_BADREQUEST() throws Exception {
        Transaction result = new Transaction();
        result.setTransactionResult(TransactionResult.ALLOWED);
        result.setTransactionInfo("none");

        when(transactionService.processTransaction(any())).thenReturn(result);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL + "/transaction")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content("""
                                                                                     {
                                                                                         "amount": 150,
                                                                                         "ip": "",
                                                                                         "number": "4000008449433403",
                                                                                         "region": "SSA",
                                                                                         "date": "2022-01-22T16:02:00"
                                                                                     }"""))
                                     .andReturn();

        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithMockUser(username = "merchant", authorities = {"ROLE_MERCHANT"})
    public void test_makeTransaction_when_cardNumberIsInvalid_should_return_BADREQUEST() throws Exception {
        Transaction result = new Transaction();
        result.setTransactionResult(TransactionResult.ALLOWED);
        result.setTransactionInfo("none");

        when(transactionService.processTransaction(any())).thenReturn(result);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL + "/transaction")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content("""
                                                                                     {
                                                                                         "amount": 150,
                                                                                         "ip": "192.168.1.4",
                                                                                         "number": "40000084494334038989",
                                                                                         "region": "SSA",
                                                                                         "date": "2022-01-22T16:02:00"
                                                                                     }"""))
                                     .andReturn();

        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithMockUser(username = "merchant", authorities = {"ROLE_MERCHANT"})
    public void test_makeTransaction_when_regionIsBlanc_should_return_BADREQUEST() throws Exception {
        Transaction result = new Transaction();
        result.setTransactionResult(TransactionResult.ALLOWED);
        result.setTransactionInfo("none");

        when(transactionService.processTransaction(any())).thenReturn(result);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL + "/transaction")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content("""
                                                                                     {
                                                                                         "amount": 150,
                                                                                         "ip": "192.168.1.4",
                                                                                         "number": "4000008449433403",
                                                                                         "region": "",
                                                                                         "date": "2022-01-22T16:02:00"
                                                                                     }"""))
                                     .andReturn();

        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithMockUser(username = "merchant", authorities = {"ROLE_MERCHANT"})
    public void test_makeTransaction_when_dateIsWrongFormat_should_return_BADREQUEST() throws Exception {
        Transaction result = new Transaction();
        result.setTransactionResult(TransactionResult.ALLOWED);
        result.setTransactionInfo("none");

        when(transactionService.processTransaction(any())).thenReturn(result);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL + "/transaction")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content("""
                                                                                     {
                                                                                         "amount": 150,
                                                                                         "ip": "192.168.1.4",
                                                                                         "number": "4000008449433403",
                                                                                         "region": "SSA",
                                                                                         "date": "2022-01-22T16:02:00f"
                                                                                     }"""))
                                     .andReturn();

        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void test_addFeedback_when_userIsUnAuthorized_should_return_UNAUTHORIZED() throws Exception {
        Transaction feedback = TransactionFactory.create(1L, "192.168.1.4", "4000008449433403", WorldRegion.SSA,
                                                         LocalDateTime.now());
        feedback.setTransactionResult(TransactionResult.MANUAL_PROCESSING);
        feedback.setFeedback(TransactionResult.MANUAL_PROCESSING);
        feedback.setTransactionInfo("none");

        when(transactionService.giveFeedback(any())).thenReturn(feedback);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(URL + "/transaction")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content("""
                                                                                     {
                                                                                         "transactionId": 1,
                                                                                         "amount": 150,
                                                                                         "ip": "192.168.1.4",
                                                                                         "number": "4000008449433403",
                                                                                         "region": "SSA",
                                                                                         "date": "2022-01-22T16:02:00",
                                                                                         "result": "ALLOWED",
                                                                                         "feedback": "ALLOWED"
                                                                                     }"""))
                                     .andReturn();

        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @WithMockUser(username = "support", roles = {"SUPPORT"})
    public void test_addFeedback_when_userIsAuthorized_should_return_OK() throws Exception {
        Transaction feedback = TransactionFactory.create(1L, "192.168.1.4", "4000008449433403", WorldRegion.SSA,
                                                         LocalDateTime.now());
        feedback.setTransactionResult(TransactionResult.MANUAL_PROCESSING);
        feedback.setFeedback(TransactionResult.MANUAL_PROCESSING);
        feedback.setTransactionInfo("none");

        when(transactionService.giveFeedback(any())).thenReturn(feedback);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(URL + "/transaction")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content("""
                                                                                     {
                                                                                         "transactionId": 1,
                                                                                         "amount": 150,
                                                                                         "ip": "192.168.1.4",
                                                                                         "number": "4000008449433403",
                                                                                         "region": "SSA",
                                                                                         "date": "2022-01-22T16:02:00",
                                                                                         "result": "ALLOWED",
                                                                                         "feedback": "ALLOWED"
                                                                                     }"""))
                                     .andReturn();

        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.OK.value()));
    }

    @Test
    @WithMockUser(username = "support", roles = {"SUPPORT"})
    public void test_addFeedback_when_transactionIdIsNull_should_return_BADREQUEST() throws Exception {
        Transaction feedback = TransactionFactory.create(1L, "192.168.1.4", "4000008449433403", WorldRegion.SSA,
                                                         LocalDateTime.now());
        feedback.setTransactionResult(TransactionResult.MANUAL_PROCESSING);
        feedback.setFeedback(TransactionResult.MANUAL_PROCESSING);
        feedback.setTransactionInfo("none");

        when(transactionService.giveFeedback(any())).thenReturn(feedback);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(URL + "/transaction")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content("""
                                                                                     {
                                                                                         "amount": 150,
                                                                                         "ip": "192.168.1.4",
                                                                                         "number": "4000008449433403",
                                                                                         "region": "SSA",
                                                                                         "date": "2022-01-22T16:02:00",
                                                                                         "result": "ALLOWED",
                                                                                         "feedback": "ALLOWED"
                                                                                     }"""))
                                     .andReturn();

        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @WithMockUser(username = "support", roles = {"SUPPORT"})
    public void test_addFeedback_when_feedBackIsBlanck_should_return_BADREQUEST() throws Exception {
        Transaction feedback = TransactionFactory.create(1L, "192.168.1.4", "4000008449433403", WorldRegion.SSA,
                                                         LocalDateTime.now());
        feedback.setTransactionResult(TransactionResult.MANUAL_PROCESSING);
        feedback.setFeedback(TransactionResult.MANUAL_PROCESSING);
        feedback.setTransactionInfo("none");

        when(transactionService.giveFeedback(any())).thenReturn(feedback);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(URL + "/transaction")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .content("""
                                                                                     {
                                                                                         "transactionId": 1,
                                                                                         "amount": 150,
                                                                                         "ip": "192.168.1.4",
                                                                                         "number": "4000008449433403",
                                                                                         "region": "SSA",
                                                                                         "date": "2022-01-22T16:02:00",
                                                                                         "result": "ALLOWED",
                                                                                         "feedback": ""
                                                                                     }"""))
                                     .andReturn();

        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void test_getHistory_when_userIsUnAuthorized_should_return_UNAUTHORIZED() throws Exception {
        Transaction feedback = TransactionFactory.create(1L, "192.168.1.4",
                                                         "4000008449433403", WorldRegion.SSA, LocalDateTime.now());
        feedback.setTransactionResult(TransactionResult.MANUAL_PROCESSING);
        feedback.setFeedback(TransactionResult.MANUAL_PROCESSING);
        feedback.setTransactionInfo("none");
        List<Transaction> list = List.of(feedback);

        when(transactionService.showTransactionHistory()).thenReturn(list);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URL + "/history")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON))
                                     .andReturn();

        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @WithMockUser(username = "support", roles = {"SUPPORT"})
    public void test_getHistory_when_userIsAuthorized_should_return_OK() throws Exception {
        Transaction feedback = TransactionFactory.create(1L, "192.168.1.4", "4000008449433403", WorldRegion.SSA,
                                                         LocalDateTime.now());
        feedback.setTransactionResult(TransactionResult.MANUAL_PROCESSING);
        feedback.setFeedback(TransactionResult.MANUAL_PROCESSING);
        feedback.setTransactionInfo("none");
        List<Transaction> list = List.of(feedback);

        when(transactionService.showTransactionHistory()).thenReturn(list);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URL + "/history")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON))
                                     .andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.OK.value()));
    }

    @Test
    public void test_getHistoryForCardNumber_when_userIsUnAuthorized_should_return_UNAUTHORIZED() throws Exception {
        Transaction feedback = TransactionFactory.create(1L, "192.168.1.4", "4000008449433403", WorldRegion.SSA,
                                                         LocalDateTime.now());
        feedback.setTransactionResult(TransactionResult.MANUAL_PROCESSING);
        feedback.setFeedback(TransactionResult.MANUAL_PROCESSING);
        feedback.setTransactionInfo("none");
        List<Transaction> list = List.of(feedback);

        when(transactionService.showTransactionHistoryForSpecificCardNumber(any())).thenReturn(list);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URL + "/history/4000008449433403")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON))
                                     .andReturn();

        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @WithMockUser(username = "support", roles = {"SUPPORT"})
    public void test_getHistoryForCardNumber_when_userIsAuthorized_should_return_OK() throws Exception {
        Transaction feedback = TransactionFactory.create(1L, "192.168.1.4", "4000008449433403", WorldRegion.SSA,
                                                         LocalDateTime.now());
        feedback.setTransactionResult(TransactionResult.MANUAL_PROCESSING);
        feedback.setFeedback(TransactionResult.MANUAL_PROCESSING);
        feedback.setTransactionInfo("none");
        List<Transaction> list = List.of(feedback);

        when(transactionService.showTransactionHistoryForSpecificCardNumber(any())).thenReturn(list);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URL + "/history/4000008449433403")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON))
                                     .andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.OK.value()));
    }

    @Test
    @WithMockUser(username = "support", roles = {"SUPPORT"})
    public void test_getHistoryForCardNumber_when_cardNumberIsInvalid_should_return_BADREQUEST() throws Exception {
        Transaction feedback = TransactionFactory.create(1L, "192.168.1.4", "4000008449433403", WorldRegion.SSA,
                                                         LocalDateTime.now());
        feedback.setTransactionResult(TransactionResult.MANUAL_PROCESSING);
        feedback.setFeedback(TransactionResult.MANUAL_PROCESSING);
        feedback.setTransactionInfo("none");
        List<Transaction> list = List.of(feedback);

        when(transactionService.showTransactionHistoryForSpecificCardNumber(any())).thenReturn(list);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URL + "/history/40000084494334037878")
                                                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                                                    .contentType(MediaType.APPLICATION_JSON))
                                     .andReturn();
        assertEquals(mvcResult.getResponse().getStatus(), (HttpStatus.BAD_REQUEST.value()));
    }
}

