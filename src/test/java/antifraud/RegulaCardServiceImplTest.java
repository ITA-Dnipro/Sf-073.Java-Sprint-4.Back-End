package antifraud;

import antifraud.domain.model.RegularCard;
import antifraud.domain.model.RegularCardFactory;
import antifraud.domain.service.impl.RegularCardServiceImpl;

import antifraud.persistence.repository.RegularCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RegulaCardServiceImplTest {

    @InjectMocks
    RegularCardServiceImpl regularCardServiceMock;

    @Mock
    private RegularCardRepository regularCardRepositoryMock;
    private final String CARD_NUMBER = "4000008449433403";
    private final String NON_EXISTING_CARD_NUMBER = "341846397906706";
    private final RegularCard regularCard = RegularCardFactory.create(CARD_NUMBER);
    private final RegularCard nonRegularCard = RegularCardFactory.create(NON_EXISTING_CARD_NUMBER);


    @Test
    void given_card_number_when_existsByNumber_then_true() {
        when(regularCardRepositoryMock.existsByNumber(CARD_NUMBER))
                .thenReturn(true);

        boolean expected = regularCardServiceMock.existsByNumber(CARD_NUMBER);

        assertTrue(expected);
    }

    @Test
    void given_non_existing_card_number_when_existsByNumber_then_false() {
        when(regularCardRepositoryMock.existsByNumber(NON_EXISTING_CARD_NUMBER))
                .thenReturn(false);

        boolean expected = regularCardServiceMock.existsByNumber(NON_EXISTING_CARD_NUMBER);

        assertFalse(expected);
    }

    @Test
    void given_card_number_when_findByNumber_then_return_regular_card() {
        when(regularCardRepositoryMock.findByNumber(CARD_NUMBER))
                .thenReturn(regularCard);

        RegularCard expected = regularCardServiceMock.findByNumber(CARD_NUMBER);

        assertThat(expected.getNumber()).isEqualTo(CARD_NUMBER);
    }

    @Test
    void given_non_existing_card_number_when_existsByNumber_then_return_card_number() {
        when(regularCardRepositoryMock.findByNumber(NON_EXISTING_CARD_NUMBER))
                .thenReturn(nonRegularCard);

        RegularCard expected = regularCardServiceMock.findByNumber(NON_EXISTING_CARD_NUMBER);

        assertThat(expected.getNumber()).isNotEqualTo(CARD_NUMBER);
    }

    @Test
    void given_regular_card_number_when_existsByNumber_then_return_card_number() {
        when(regularCardRepositoryMock.findByNumber(NON_EXISTING_CARD_NUMBER))
                .thenReturn(nonRegularCard);

        RegularCard expected = regularCardServiceMock.findByNumber(NON_EXISTING_CARD_NUMBER);

        assertThat(expected.getNumber()).isNotEqualTo(CARD_NUMBER);
    }

    @Test
    void given_regular_card_number_when_save_then_return_regular_card() {
        when(regularCardRepositoryMock.save(regularCard))
                .thenReturn(regularCard);

        RegularCard expected = regularCardServiceMock.save(regularCard);

        assertThat(expected.getNumber()).isEqualTo(CARD_NUMBER);
    }

    @Test
    void given_regular_card_number_when_existsByNumber_then_return_non_exist() {
        when(regularCardRepositoryMock.save(nonRegularCard))
                .thenReturn(null);

        RegularCard expected = regularCardServiceMock.save(nonRegularCard);

        assertNull(expected);
    }
}
