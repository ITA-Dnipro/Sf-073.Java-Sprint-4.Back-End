import antifraud.domain.model.StolenCard;
import antifraud.domain.service.impl.StolenCardServiceImpl;
import antifraud.persistence.repository.StolenCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class StolenCardServiceImplTest {
    private StolenCardRepository stolenCardRepository;

    @BeforeEach
    void setupService() {
        stolenCardRepository = mock(StolenCardRepository.class);
    }

    @Test
    void storeStolenCardNumber() {
        StolenCard stolenCard = new StolenCard(1L, "4556958813071411");
        when(stolenCardRepository.save(stolenCard)).thenReturn(stolenCard);

        StolenCard savedStolenCard = stolenCardRepository.save(stolenCard);

        assertThat(savedStolenCard.getNumber()).isNotNull();

    }

    @Test
    void removeCardNumber() {
    }

    @Test
    void showCardNumbers() {
    }

    @Test
    void existsByNumber() {
    }
}