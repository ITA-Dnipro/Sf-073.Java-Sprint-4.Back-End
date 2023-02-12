import antifraud.domain.model.StolenCard;
import antifraud.domain.service.impl.StolenCardServiceImpl;
import antifraud.exceptions.CardNotFoundException;
import antifraud.persistence.repository.StolenCardRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class StolenCardServiceImplTest {

    @Mock
    StolenCardRepository stolenCardRepository;
    @InjectMocks
    StolenCardServiceImpl stolenCardService;
    private StolenCard stolenCard;
    private StolenCard nonSavedStolenCard;
    @BeforeEach
    void setupService() {
        stolenCard = new StolenCard(1L, "4556958813071411");
        nonSavedStolenCard = new StolenCard(1L, "4556958813071412");
    }

    @Test
    void canStoreStolenCard() {
        when(stolenCardRepository.save(stolenCard)).thenReturn(stolenCard);

        Optional<StolenCard> savedStolenCard = stolenCardService.storeStolenCardNumber(stolenCard);

        assertNotNull(savedStolenCard);
    }

    @Test
    void canRemoveCardByNumber() {
        when(stolenCardRepository.findByNumber(stolenCard.getNumber())).thenReturn(Optional.of(stolenCard));
        //Optional<StolenCard> foundStolenCard = stolenCardRepository.findByNumber("4556958813071411");
        doNothing().when(stolenCardRepository).deleteById(any());

        assertDoesNotThrow(()-> stolenCardService.removeCardNumber(stolenCard.getNumber()));

    }

    @Test
    void whenCardDoesntExistsExceptionIsThrown() {
        when(stolenCardRepository.findByNumber(any())).thenReturn(Optional.empty());

        assertThrowsExactly(CardNotFoundException.class, () ->
                stolenCardService.removeCardNumber(nonSavedStolenCard.getNumber()));
    }


    @Test
    void canShowAllCardNumbers() {
        List<StolenCard> stolenCards = List.of(stolenCard);

        when(stolenCardRepository.findAll()).thenReturn(stolenCards);

        List<StolenCard> foundStolenCards = stolenCardService.showCardNumbers();
        log.info("list of stolen cards {}", foundStolenCards);

        assertThat(foundStolenCards).isNotEmpty();
    }

    @Test
    void existsByNumber() {
        when(stolenCardRepository.existsByNumber(stolenCard.getNumber())).thenReturn(true);

        boolean actualValue = stolenCardService.existsByNumber(stolenCard.getNumber());

        assertTrue(actualValue);
    }

    @Test
    void whenCardDoesntExistsInDbShouldNotMatch() {
        when(stolenCardRepository.existsByNumber(nonSavedStolenCard.getNumber())).thenReturn(false);

        boolean actualValue = stolenCardService.existsByNumber(nonSavedStolenCard.getNumber());

        assertFalse(actualValue);
    }
}