package antifraud;


import antifraud.domain.model.StolenCard;
import antifraud.domain.model.StolenCardFactory;
import antifraud.persistence.repository.StolenCardRepository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class StolenCardRepositoryTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    StolenCardRepository stolenCardRepository;

    private final String CARD_NUMBER = "4000008449433403";
    private final String NON_EXISTING_CARD_NUMBER = "341846397906706";


    @BeforeEach
    void setUp() {
        StolenCard stolenCard = StolenCardFactory.create(CARD_NUMBER);
        entityManager.persist(stolenCard);
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(stolenCardRepository).isNotNull();
    }

    @Test
    void given_existing_stolen_card_number_when_existByNumber_then_true() {
        boolean expected = stolenCardRepository.existsByNumber(CARD_NUMBER);

        assertThat(expected).isTrue();
    }

    @Test
    void given_NOT_existing_stolen_card_number_when_existByNumber_then_false() {
        boolean expected = stolenCardRepository.existsByNumber(NON_EXISTING_CARD_NUMBER);

        assertThat(expected).isFalse();
    }

    @Test
    void given_credit_stolen_card_number_when_findByNumber_then_return_stolen_creditCardNumber() {
        Optional<StolenCard> expected = stolenCardRepository.findByNumber(CARD_NUMBER);

        assertThat(expected).isPresent();
        assertThat(expected.get().getNumber()).isEqualTo(CARD_NUMBER);
    }

    @Test
    void given_wrong_credit_stolen_card_number_when_findByNumber_then_isEmpty() {
        Optional<StolenCard> expected = stolenCardRepository.findByNumber(NON_EXISTING_CARD_NUMBER);

        assertThat(expected).isNotPresent();
    }
}
