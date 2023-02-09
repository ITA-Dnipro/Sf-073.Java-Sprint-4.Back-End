package antifraud.domain.service;

import antifraud.domain.model.RegularCard;
import antifraud.domain.model.RegularCardFactory;
import antifraud.domain.service.impl.RegularCardServiceImpl;
import antifraud.persistence.repository.RegularCardRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class RegularCardServiceTest {

    @InjectMocks
    private RegularCardServiceImpl regularCardServiceImpl;
    @Mock
    private RegularCardRepository regularCardRepository;

    private final String validCardNumber = "6250941006528599";

    @Test
    void whenRegularCardRepoIsEmptyThenFindByNumberShouldReturnNull() {
        RegularCard regularCard = regularCardServiceImpl.findByNumber("");
        assertThat(regularCard).isNull();
    }

    @Test
    void whenRegularCardRepoMatchesThenFindByNumberShouldRegularCardAndShouldMatch() {
        RegularCard customRegularCard = RegularCardFactory.create(validCardNumber);
        given(regularCardRepository.findByNumber(validCardNumber)).willReturn(customRegularCard);

        RegularCard regularCard = regularCardRepository.findByNumber(validCardNumber);

        assertEquals(customRegularCard.getNumber(), regularCard.getNumber());
    }

    @Test
    void whenRegularCardDoesNotMatchesThenFindByNumberShouldRegularCardAndShouldNotMatch() {
        RegularCard customRegularCard = RegularCardFactory.create(validCardNumber);
        String invalidCardNumber = "6250941006528588";
        RegularCard invalidCard = RegularCardFactory.create(invalidCardNumber);

        given(regularCardRepository.findByNumber(invalidCardNumber)).willReturn(invalidCard);
        RegularCard regularCard = regularCardRepository.findByNumber(invalidCardNumber);

        assertNotEquals(customRegularCard.getNumber(), regularCard.getNumber());
    }

    @Test
    void whenRegularCardRepoIsEmptyThenExistsByNumberShouldReturnFalse() {
        boolean isPresent = regularCardServiceImpl.existsByNumber("");
        assertFalse(isPresent);
    }

    @Test
    void whenRegularCardExistsThenExistsByNumberShouldReturnTrue() {
        given(regularCardRepository.existsByNumber(validCardNumber)).willReturn(true);
        boolean isPresent = regularCardRepository.existsByNumber(validCardNumber);
        assertTrue(isPresent);
    }

    @Test
    void whenSavingRegularCardThenReturnTheRegularCard() {
        RegularCard regularCard = RegularCardFactory.create(validCardNumber);
        given(regularCardRepository.save(regularCard)).willReturn(regularCard);
        RegularCard customRegularCard = regularCardServiceImpl.save(regularCard);
        assertEquals(regularCard, customRegularCard);
    }
}
