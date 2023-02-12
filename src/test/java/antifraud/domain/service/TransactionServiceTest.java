package antifraud.domain.service;

import antifraud.config.transaction.TransactionProperty;
import antifraud.domain.model.RegularCard;
import antifraud.domain.model.RegularCardFactory;
import antifraud.domain.model.Transaction;
import antifraud.domain.model.TransactionFactory;
import antifraud.domain.model.enums.TransactionResult;
import antifraud.domain.model.enums.WorldRegion;
import antifraud.domain.service.model.RegularCardServiceImpl;
import antifraud.domain.service.model.StolenCardServiceImpl;
import antifraud.domain.service.model.SuspiciousIPServiceImpl;
import antifraud.domain.service.model.TransactionServiceImpl;
import antifraud.exceptions.ExistingFeedbackException;
import antifraud.exceptions.TransactionsNotFoundException;
import antifraud.persistence.repository.RegularCardRepository;
import antifraud.persistence.repository.StolenCardRepository;
import antifraud.persistence.repository.SuspiciousIPRepository;
import antifraud.persistence.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    private RegularCardServiceImpl regularCardService;
    private TransactionServiceImpl transactionService;
    private TransactionRepository transactionRepository;

    private RegularCard regularCard;
    private Transaction transaction;
    private Transaction transactionAmount;
    private Transaction transactionAnotherIP;
    private Transaction transactionAnotherRegion;
    private String cardNumber;

    @BeforeEach
    void init(){
        setupFields();
        setupService();
    }

    void setupFields(){
        cardNumber =  "4000008449433403";
        regularCard = RegularCardFactory.create(cardNumber);
        regularCard.setAllowedLimit(200L);
        regularCard.setManualProcessingLimit(1500L);

        transaction = TransactionFactory.create(
                100L,
                "192.168.0.1",
                cardNumber,
                WorldRegion.EAP,
                LocalDateTime.now());

        transactionAmount = TransactionFactory.create(
                1600L,
                "192.168.0.1",
                "4000008449433403",
                WorldRegion.EAP,
                LocalDateTime.now());

        transactionAnotherIP = TransactionFactory.create(
                100L,
                "192.168.5.1",
                cardNumber,
                WorldRegion.EAP,
                LocalDateTime.now());

        transactionAnotherRegion = TransactionFactory.create(
                100L,
                "192.168.0.1",
                cardNumber,
                WorldRegion.ECA,
                LocalDateTime.now());

    }

    void setupService() {
        var transactionProperty = new TransactionProperty(
                200,
                1500,
                3,
                0.8,
                0.2);

        var regularCardRepository = mock(RegularCardRepository.class);
        regularCardService = new RegularCardServiceImpl(regularCardRepository);

        var stolenCardRepository = mock(StolenCardRepository.class);
        var stolenCardService = new StolenCardServiceImpl(stolenCardRepository);

        var suspiciousIPRepository = mock(SuspiciousIPRepository.class);
        var suspiciousIPService = new SuspiciousIPServiceImpl(suspiciousIPRepository);

        transactionRepository = mock(TransactionRepository.class);
        transactionService = new TransactionServiceImpl(
                transactionProperty,
                transactionRepository,
                suspiciousIPService,
                stolenCardService,
                regularCardService
        );
    }

    @Test
    @DisplayName("Testing transaction history with empty number should return exception")
    void whenCardNumberIsEmptyThenShowTransactionHistoryForSpecificCardNumberShouldReturnException() {
        assertThrows(TransactionsNotFoundException.class,
                () -> transactionService.showTransactionHistoryForSpecificCardNumber(""));
    }

    @Test
    @DisplayName("Testing process transaction should return none")
    void whenProcessTransactionThenReturnNoneStatus() {
        when(regularCardService.findByNumber(cardNumber)).thenReturn(regularCard);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        var transactionResult = transactionService.processTransaction(transaction);
        assertEquals(transactionResult.getTransactionInfo(), "none");
    }

    @Test
    @DisplayName("Testing process transaction should return amount")
    void whenProcessTransactionThenReturnAmountStatus() {
        when(regularCardService.findByNumber(cardNumber)).thenReturn(regularCard);
        when(transactionRepository.save(transactionAmount)).thenReturn(transactionAmount);
        var transactionResult = transactionService.processTransaction(transactionAmount);
        assertEquals(transactionResult.getTransactionInfo(), "amount");
    }

    @Test
    @DisplayName("Testing process transaction should return none")
    void whenProcessTransactionWithSeveralIPAndRegionThenReturnNone() {
        when(regularCardService.findByNumber(cardNumber)).thenReturn(regularCard);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(transactionRepository.findByCardNumberAndDateTimeBetween(cardNumber,
                transaction.getDateTime().minusHours(1),
                transaction.getDateTime())).thenReturn(List.of(transaction,
                transactionAnotherRegion,
                transactionAnotherIP));

        var transactionResult = transactionService.processTransaction(transaction);
        assertEquals(transactionResult.getTransactionInfo(), "none");
    }

    @Test
    @DisplayName("Testing transaction history should return false")
    void whenAddTransactionToHistoryThenReturnNotFalse() {
        when(transactionRepository.findAll()).thenReturn(List.of(transaction,
                transactionAnotherRegion,
                transactionAnotherIP));
        var listOfTransactions = transactionService.showTransactionHistory();
        assertFalse(listOfTransactions.isEmpty());
    }

    @Test
    @DisplayName("Testing feedback for transaction should return exception")
    void whenGiveFeedbackForTransactionThenReturnException() {
        when(transactionRepository.findById(any())).thenReturn(Optional.of((transaction)));
        when(transactionRepository.existsByFeedbackAndFeedbackNotNull(any())).thenReturn(true);
        assertThrows(ExistingFeedbackException.class,()->transactionService.giveFeedback(transaction));
    }


    @Test
    @DisplayName("Testing feedback for transaction should return equals MANUAL_PROCESSING")
    void whenGiveFeedbackForTransactionThenReturnEquals() {
        given(transactionRepository.findById(any())).willReturn(Optional.of((transaction)));
        when(transactionRepository.existsByFeedbackAndFeedbackNotNull(any())).thenReturn(false);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(regularCardService.findByNumber(cardNumber)).thenReturn(regularCard);
        transactionAnotherIP.setFeedback(TransactionResult.MANUAL_PROCESSING);
        transaction = transactionService.giveFeedback(transactionAnotherIP);
        assertEquals(transaction.getFeedback(),TransactionResult.MANUAL_PROCESSING);
    }
}
