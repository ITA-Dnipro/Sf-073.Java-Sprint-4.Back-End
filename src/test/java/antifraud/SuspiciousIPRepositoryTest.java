package antifraud;

import antifraud.domain.model.IP;
import antifraud.domain.model.IPFactory;
import antifraud.persistence.repository.SuspiciousIPRepository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
class SuspiciousIPRepositoryTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    SuspiciousIPRepository suspiciousIPRepository;


    private final String IP = "192.168.1.34";
    private final String NON_EXISTING_IP = "155.155.155.5";


    @BeforeEach
    void setUp() {
        IP suspiciousIP = IPFactory.create(IP);
        entityManager.persist(suspiciousIP);
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(suspiciousIPRepository).isNotNull();
    }

    @Test
    void given_existing_suspicious_IP_when_existByNumber_then_true() {
        boolean result = suspiciousIPRepository.existsByIpAddress(IP);

        assertTrue(result);

    }

    @Test
    void given_NOT_existing_suspicious_IP_when_existByNumber_then_false() {
        boolean expected = suspiciousIPRepository.existsByIpAddress(NON_EXISTING_IP);

        assertThat(expected).isFalse();
    }

    @Test
    void given_suspicious_IP_when_findByIpAddress_then_return_suspicious_IP() {
        Optional<IP> expected = suspiciousIPRepository.findByIpAddress(IP);

        assertThat(expected).isPresent();
        assertThat(expected.get().getIpAddress()).isEqualTo(IP);
    }

    @Test
    void given_wrong_suspicious_IP_when_findByIpAddress_then_isEmpty() {
        Optional<IP> expected = suspiciousIPRepository.findByIpAddress(NON_EXISTING_IP);

        assertThat(expected).isNotPresent();
    }
}
