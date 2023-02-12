package antifraud;


import antifraud.domain.model.Transaction;
import antifraud.domain.model.TransactionFactory;
import antifraud.domain.model.enums.WorldRegion;
import antifraud.domain.service.impl.TransactionServiceImpl;
import antifraud.exceptions.TransactionsNotFoundException;
import antifraud.persistence.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceHistoryTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;
    @Mock
    private TransactionRepository transactionRepository;

    private final String IP = "192.168.1.34";
    private final String CARD_NUMBER_ONE = "4000008449433403";
    private final String NON_EXISTING_CARD_NUMBER = "341846397906706";
    private final WorldRegion REGION = WorldRegion.ECA;
    private final LocalDateTime time = LocalDateTime.now();
    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;

    @BeforeEach
    void setUp() {
        transaction1 = TransactionFactory.create(200L, IP, CARD_NUMBER_ONE, REGION, time);
        transaction2 = TransactionFactory.create(500L, IP, CARD_NUMBER_ONE, REGION, time);
        transaction3 = TransactionFactory.create(1800L, IP, CARD_NUMBER_ONE, REGION, time);

    }

    @Test
    void given_card_number_when_showTransactionHistoryForSpecificCardNumber_then_return_list() {
        when(transactionRepository.findTransactionByCardNumber(CARD_NUMBER_ONE))
                .thenReturn(List.of(transaction1,transaction2,transaction3));

        List<Transaction> expectedList = transactionService.showTransactionHistoryForSpecificCardNumber(CARD_NUMBER_ONE);

        int expected = 3;

        assertEquals(expected, expectedList.size());
    }

    @Test
    void given_non_existent_card_number_when_showTransactionHistoryForSpecificCardNumber_then_throw_transactionsNotFoundException() {
        when(transactionRepository.findTransactionByCardNumber(NON_EXISTING_CARD_NUMBER))
                .thenThrow(TransactionsNotFoundException.class);

        Executable executable = () -> transactionService.showTransactionHistoryForSpecificCardNumber(NON_EXISTING_CARD_NUMBER);

        assertThrows(TransactionsNotFoundException.class, executable);
    }
}
