package antifraud;


import antifraud.domain.model.StolenCard;
import antifraud.domain.model.StolenCardFactory;
import antifraud.persistence.repository.StolenCardRepository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class StolenCardTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    StolenCardRepository stolenCardRepository;

    private final String cardNumber = "4000008449433403";
    private final String nonExistingCardNumber = "341846397906706";

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(stolenCardRepository).isNotNull();
    }

    @Test
    void given_existing_stolen_card_number_when_existByNumber_then_true() {
        StolenCard stolenCard = StolenCardFactory.create(cardNumber);

        entityManager.persist(stolenCard);
        boolean expected = stolenCardRepository.existsByNumber(cardNumber);

        Assertions.assertThat(expected).isTrue();
    }

    @Test
    void given_NOT_existing_stolen_card_number_when_existByNumber_then_false() {
        StolenCard stolenCard = StolenCardFactory.create(cardNumber);

        entityManager.persist(stolenCard);
        boolean expected = stolenCardRepository.existsByNumber(nonExistingCardNumber);

        assertThat(expected).isFalse();
    }

    @Test
    void given_credit_stolen_card_number_when_findByNumber_then_return_stolen_creditCardNumber() {
        StolenCard stolenCard = StolenCardFactory.create(cardNumber);
        entityManager.persist(stolenCard);

        Optional<StolenCard> expected = stolenCardRepository.findByNumber(cardNumber);

        assertThat(expected).isPresent();
        assertThat(expected.get().getNumber()).isEqualTo(cardNumber);
    }

    @Test
    void given_wrong_credit_stolen_card_number_when_findByNumber_then_isEmpty() {
        StolenCard stolenCard = StolenCardFactory.create(cardNumber);
        entityManager.persist(stolenCard);

        Optional<StolenCard> expected = stolenCardRepository.findByNumber(nonExistingCardNumber);

        assertThat(expected).isNotPresent();
    }
}
