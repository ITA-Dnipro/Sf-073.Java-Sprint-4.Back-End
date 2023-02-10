package antifraud;


import antifraud.domain.model.Transaction;
import antifraud.domain.model.TransactionFactory;
import antifraud.domain.model.enums.TransactionResult;
import antifraud.domain.model.enums.WorldRegion;
import antifraud.persistence.repository.TransactionRepository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.boot.test.mock.mockito.SpyBean;


import java.time.LocalDateTime;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
class TransactionRepositoryTest {

    @SpyBean
    TransactionRepository transactionRepository;

    private final String IP = "192.168.1.34";
    private final String CARD_NUMBER_ONE = "4000008449433403";
    private final String NON_EXISTING_CARD_NUMBER = "341846397906706";
    private final WorldRegion REGION = WorldRegion.ECA;
    private final LocalDateTime time = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        Transaction transaction1 = TransactionFactory.create(200L, IP, CARD_NUMBER_ONE, REGION, time);
        Transaction transaction2 = TransactionFactory.create(500L, IP, CARD_NUMBER_ONE, REGION, time);
        Transaction transaction3 = TransactionFactory.create(1800L, IP, CARD_NUMBER_ONE, REGION, time);
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
    }

    @Test
    void injected_components_are_not_null() {
        assertThat(transactionRepository).isNotNull();
    }

    @Test
    void given_transaction_result_when_existsByFeedbackAndFeedbackNotNull_then_true() {
        Transaction transaction = TransactionFactory.createWithFeedback(1L, TransactionResult.ALLOWED);
        transactionRepository.save(transaction);

        boolean expected = transactionRepository.existsByFeedbackAndFeedbackNotNull(TransactionResult.ALLOWED);

        assertThat(expected).isTrue();
    }

    @Test
    void given_transaction_result_MANUAL_PROCESSING_when_existsByFeedbackAndFeedbackNotNull_then_true() {
        Transaction transaction = TransactionFactory.createWithFeedback(1L, TransactionResult.MANUAL_PROCESSING);
        transactionRepository.save(transaction);

        boolean expected = transactionRepository.existsByFeedbackAndFeedbackNotNull(TransactionResult.MANUAL_PROCESSING);

        assertThat(expected).isTrue();
    }

    @Test
    void given_transaction_result_PROHIBITED_when_existsByFeedbackAndFeedbackNotNull_then_true() {
        Transaction transaction = TransactionFactory.createWithFeedback(1L, TransactionResult.PROHIBITED);
        transactionRepository.save(transaction);

        boolean expected = transactionRepository.existsByFeedbackAndFeedbackNotNull(TransactionResult.PROHIBITED);

        assertThat(expected).isTrue();
    }

    @Test
    void given_NOT_existing_transaction_result_ALLOWED_when_existsByFeedbackAndFeedbackNotNull_then_false() {
        boolean expected = transactionRepository.existsByFeedbackAndFeedbackNotNull(TransactionResult.ALLOWED);

        assertThat(expected).isFalse();
    }

    @Test
    void given_null_transaction_result_when_existsByFeedbackAndFeedbackNotNull_then_false() {
        boolean expected = transactionRepository.existsByFeedbackAndFeedbackNotNull(null);

        assertThat(expected).isFalse();
    }

    @Test
    void given_existing_card_number_when_findTransactionByCardNumber_then_return_list_transactions() {
        List<Transaction> expectedList = transactionRepository.findTransactionByCardNumber(CARD_NUMBER_ONE);

        int expected = 3;

        assertEquals(expected, expectedList.size());
    }

    @Test
    void given_non_existing_card_number_when_findTransactionByCardNumber_then_is_empty() {
        List<Transaction> expectedList = transactionRepository.findTransactionByCardNumber(NON_EXISTING_CARD_NUMBER);

        int expected = 0;

        assertEquals(expected, expectedList.size());
    }

    @Test
    void given_existing_card_number_with_time_last_hour_when_findByCardNumberAndDateTimeBetween_then_list_of_transactions_within_one_hour() {
        var timeMinusOneHour = LocalDateTime.now().minusHours(1L);

        List<Transaction> expectedList = transactionRepository.findByCardNumberAndDateTimeBetween(CARD_NUMBER_ONE,timeMinusOneHour,time);

        int expected = 3;

        assertEquals(expected, expectedList.size());
    }

    @Test
    void given_non_existing_card_number_when_findByCardNumberAndDateTimeBetween_then_is_empty() {
        var timeMinusOneHour = LocalDateTime.now().minusHours(1L);

        List<Transaction> expectedList = transactionRepository.findByCardNumberAndDateTimeBetween(NON_EXISTING_CARD_NUMBER,timeMinusOneHour,time);

        int expected = 0;

        assertEquals(expected, expectedList.size());
    }

    @Test
    void given_existing_card_number_tow_hours_old_when_findByCardNumberAndDateTimeBetween_then_is_empty() {
        var timeMinusOneHour = LocalDateTime.now().minusHours(1L);
        var timeMinusTwoHours = LocalDateTime.now().minusHours(2L);

        Transaction transaction4 = TransactionFactory.create(500L, IP, CARD_NUMBER_ONE, REGION, timeMinusTwoHours);
        Transaction transaction5 = TransactionFactory.create(1800L, IP, CARD_NUMBER_ONE, REGION, timeMinusTwoHours);
        transactionRepository.save(transaction4);
        transactionRepository.save(transaction5);

        List<Transaction> expectedList = transactionRepository.findByCardNumberAndDateTimeBetween(CARD_NUMBER_ONE,timeMinusOneHour,time);

        int expected = 3;

        assertEquals(expected, expectedList.size());
    }
}


