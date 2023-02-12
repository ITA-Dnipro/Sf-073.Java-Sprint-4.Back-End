package antifraud.persistence;

import antifraud.domain.model.RegularCard;
import antifraud.domain.model.RegularCardFactory;
import antifraud.persistence.repository.RegularCardRepository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RegularCardRepositoryTest {

    @SpyBean
    RegularCardRepository regularCardRepository;

    private final String CARD_NUMBER = "4000008449433403";
    private final String NON_EXISTING_CARD_NUMBER = "341846397906706";

    @BeforeEach
    void setUp() {
        RegularCard regularCard = RegularCardFactory.create(CARD_NUMBER);
        regularCardRepository.save(regularCard);
    }

    @Test
    void injected_components_are_not_null() {
        assertThat(regularCardRepository).isNotNull();
    }

    @Test
    void given_existing_card_number_when_existByNumber_then_true() {
        boolean expected = regularCardRepository.existsByNumber(CARD_NUMBER);

        Assertions.assertThat(expected).isTrue();
    }

    @Test
    void given_NOT_existing_card_number_when_existByNumber_then_false() {
        boolean expected = regularCardRepository.existsByNumber(NON_EXISTING_CARD_NUMBER);

        assertThat(expected).isFalse();
    }

    @Test
    void given_credit_card_number_when_findByNumber_then_return_stolen_creditCardNumber() {
        RegularCard expected = regularCardRepository.findByNumber(CARD_NUMBER);

        assertThat(expected).isNotNull();
        assertThat(expected.getNumber()).isEqualTo(CARD_NUMBER);
    }

    @Test
    void given_wrong_credit_card_number_when_findByNumber_then_isEmpty() {
        RegularCard expected = regularCardRepository.findByNumber(NON_EXISTING_CARD_NUMBER);

        assertThat(expected).isNull();
    }
}
