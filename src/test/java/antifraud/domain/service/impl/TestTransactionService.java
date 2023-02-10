package antifraud.domain.service.impl;


import antifraud.config.transaction.TransactionProperty;
import antifraud.domain.model.RegularCard;
import antifraud.domain.model.Transaction;
import antifraud.domain.model.enums.TransactionResult;
import antifraud.domain.model.enums.WorldRegion;
import antifraud.domain.service.RegularCardService;
import antifraud.domain.service.StolenCardService;
import antifraud.domain.service.SuspiciousIPService;
import antifraud.persistence.repository.RegularCardRepository;
import antifraud.persistence.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//@TestPropertySource("transaction.values")
//@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = TransactionProperty.class)
@TestPropertySource("classpath:application.properties")
public class TestTransactionService {
    @Autowired
    private TransactionProperty transactionProperty;
    @MockBean
    private TransactionRepository transactionRepository;
    @MockBean
    private SuspiciousIPService suspiciousIPService;
    @MockBean
    private StolenCardService stolenCardService;
    @MockBean
    private RegularCardService regularCardService;
    @MockBean
    RegularCardRepository regularCardRepository;
    @MockBean
    RegularCardServiceImpl regularCardServiceImpl;
    @MockBean
    TransactionServiceImpl transactionServiceImpl;

    @Test
    void test_processTransaction_when_transactionIsAmountIs150_should_returnAllowedTransaction() {
        Transaction transaction = new Transaction();
        transaction.setMoney(150L);
        transaction.setIpAddress("192.168.1.4");
        transaction.setCardNumber("4000008449433403");
        transaction.setTransactionResult(TransactionResult.ALLOWED);
        transaction.setWorldRegion(WorldRegion.SSA);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setTransactionInfo("none");

        RegularCard regularCard = new RegularCard(1L, "4000008449433403", 250L, 300L);
        when(regularCardService.findByNumber("4000008449433403")).thenReturn(regularCard);

        TransactionProperty property = mock(TransactionProperty.class);
        when(property.allowed()).thenReturn(transaction.getMoney());

        when(transactionServiceImpl.processTransaction(transaction)).thenReturn(transaction);

        when(transactionRepository.findTransactionByCardNumber(transaction.getCardNumber())).thenReturn(List.of(transaction));
        List<Transaction> transactions = transactionRepository.findTransactionByCardNumber(
                "4000008449433403");

        assertThat(transaction, samePropertyValuesAs(transactions.get(0)));
    }

    @Test
    void test_processTransaction_when_transactionIsAmountIs250_should_returnManualProcessingTransaction() {
        Transaction transaction = new Transaction();
        transaction.setMoney(250L);
        transaction.setIpAddress("192.168.1.4");
        transaction.setCardNumber("4000008449433403");
        transaction.setTransactionResult(TransactionResult.MANUAL_PROCESSING);
        transaction.setWorldRegion(WorldRegion.SSA);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setTransactionInfo("MANUAL_PROCESSING");

        RegularCard regularCard = new RegularCard(1L, "4000008449433403", 250L, 300L);
        when(regularCardService.findByNumber("4000008449433403")).thenReturn(regularCard);

        TransactionProperty property = mock(TransactionProperty.class);
        when(property.allowed()).thenReturn(transaction.getMoney());

        when(transactionServiceImpl.processTransaction(transaction)).thenReturn(transaction);

        when(transactionRepository.findTransactionByCardNumber(transaction.getCardNumber())).thenReturn(List.of(transaction));
        List<Transaction> transactions = transactionRepository.findTransactionByCardNumber(
                "4000008449433403");

        assertThat(transaction, samePropertyValuesAs(transactions.get(0)));
    }

    @Test
    void test_processTransaction_when_transactionIsAmountIs2500_should_returnProhibitedTransaction() {
        Transaction transaction = new Transaction();
        transaction.setMoney(250L);
        transaction.setIpAddress("192.168.1.4");
        transaction.setCardNumber("4000008449433403");
        transaction.setTransactionResult(TransactionResult.PROHIBITED);
        transaction.setWorldRegion(WorldRegion.SSA);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setTransactionInfo("PROHIBITED");

        RegularCard regularCard = new RegularCard(1L, "4000008449433403", 250L, 300L);
        when(regularCardService.findByNumber("4000008449433403")).thenReturn(regularCard);

        TransactionProperty property = mock(TransactionProperty.class);
        when(property.allowed()).thenReturn(transaction.getMoney());

        when(transactionServiceImpl.processTransaction(transaction)).thenReturn(transaction);

        when(transactionRepository.findTransactionByCardNumber(transaction.getCardNumber())).thenReturn(List.of(transaction));
        List<Transaction> transactions = transactionRepository.findTransactionByCardNumber(
                "4000008449433403");

        assertThat(transaction, samePropertyValuesAs(transactions.get(0)));
    }
}
