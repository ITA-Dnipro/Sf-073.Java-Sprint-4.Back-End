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
class RegularCardServiceImplTest {

    @InjectMocks
    private RegularCardServiceImpl regularCardServiceImpl;
    @Mock
    private RegularCardRepository regularCardRepository;

    @Test
    void WhenRegularCardRepoIsEmptyThenFindByNumberShouldReturnNull() {
        RegularCard regularCard = regularCardServiceImpl.findByNumber("");
        assertThat(regularCard).isNull();
    }

    @Test
    void WhenRegularCardRepoMatchesThenFindByNumberShouldRegularCardAndShouldMatch() {
        RegularCard customRegularCard = RegularCardFactory.create("6250941006528599");
        given(this.regularCardRepository.findByNumber("6250941006528599")).willReturn(customRegularCard);

        RegularCard regularCard = this.regularCardRepository.findByNumber("6250941006528599");

        assertEquals(customRegularCard.getNumber(), regularCard.getNumber());
    }

    @Test
    void WhenRegularCardDoesNotMatchesThenFindByNumberShouldRegularCardAndShouldNotMatch() {
        RegularCard customRegularCard1 = RegularCardFactory.create("6250941006528599");
        RegularCard customRegularCard2 = RegularCardFactory.create("6250941006528588");

        given(this.regularCardRepository.findByNumber("6250941006528588")).willReturn(customRegularCard2);

        RegularCard regularCard = this.regularCardRepository.findByNumber("6250941006528588");

        assertNotEquals(customRegularCard1.getNumber(), regularCard.getNumber());
    }

    @Test
    void WhenRegularCardRepoIsEmptyThenExistsByNumberShouldReturnFalse() {
        boolean isPresent = regularCardServiceImpl.existsByNumber("");
        assertFalse(isPresent);
    }

    @Test
    void WhenRegularCardExistsThenExistsByNumberShouldReturnTrue() {
        given(this.regularCardRepository.existsByNumber("6250941006528599")).willReturn(true);
        boolean isPresent = this.regularCardRepository.existsByNumber("6250941006528599");
        assertTrue(isPresent);
    }

    @Test
    void WhenSavingRegularCardThenReturnTheRegularCard() {
        RegularCard regularCard = RegularCardFactory.create("6250941006528599");
        given(this.regularCardRepository.save(regularCard)).willReturn(regularCard);
        RegularCard customRegularCard = regularCardServiceImpl.save(regularCard);
        assertEquals(regularCard, customRegularCard);
    }


}