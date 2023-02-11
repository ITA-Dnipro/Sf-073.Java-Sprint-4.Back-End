import antifraud.domain.model.StolenCard;
import antifraud.persistence.repository.StolenCardRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
class StolenCardServiceImplTest {
    private StolenCard stolenCard;
    private StolenCardRepository stolenCardRepository;

    @BeforeEach
    void setupService() {
        stolenCard = new StolenCard(1L, "4556958813071411");
        stolenCardRepository = mock(StolenCardRepository.class);
    }

    @Test
    void canStoreStolenCard() {
        when(stolenCardRepository.save(stolenCard)).thenReturn(stolenCard);

        StolenCard savedStolenCard = stolenCardRepository.save(stolenCard);
        assertThat(savedStolenCard.getNumber()).isNotNull();

    }

    @Test
    void canRemoveCardByNumber() {
        Optional<StolenCard> stolenCardOptional = Optional.of(stolenCard);

        when(stolenCardRepository.findByNumber(stolenCard.getNumber())).thenReturn(stolenCardOptional);
        Optional<StolenCard> foundStolenCardOptional = stolenCardRepository.findByNumber(stolenCard.getNumber());

        assertThat(foundStolenCardOptional).isNotEmpty();
    }
    @Test
    void whenCardDoesntExistsOptionalIsEmpty() {
        Optional<StolenCard> stolenCardOptional = Optional.of(stolenCard);

        when(stolenCardRepository.findByNumber(stolenCard.getNumber())).thenReturn(stolenCardOptional);
        Optional<StolenCard> foundStolenCardOptional = stolenCardRepository.findByNumber("4556958813071412");

        assertThat(foundStolenCardOptional).isEmpty();
    }
    @Test
    void canShowAllCardNumbers() {
        List<StolenCard> stolenCards = new ArrayList<>();
        stolenCards.add(stolenCard);

        when(stolenCardRepository.findAll()).thenReturn(stolenCards);

        List<StolenCard> foundStolenCards = stolenCardRepository.findAll();
        log.info("list of stolen cards {}", foundStolenCards);
        assertThat(foundStolenCards).isNotEmpty();
    }

    @Test
    void existsByNumber() {
        when(stolenCardRepository.existsByNumber(stolenCard.getNumber())).thenReturn(true);

        boolean actualValue = stolenCardRepository.existsByNumber("4556958813071411");
        assertTrue(actualValue);
    }

    @Test
    void whenCardDoesntExistsInDbShouldNotMatch() {
        boolean expectedValue = true;
        when(stolenCardRepository.existsByNumber(stolenCard.getNumber())).thenReturn(expectedValue);

        boolean actualValue = stolenCardRepository.existsByNumber("4556958813071412");
        assertThat(expectedValue).isNotEqualTo(actualValue);
    }
}