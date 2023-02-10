package antifraud;

import antifraud.domain.model.IP;
import antifraud.domain.model.IPFactory;
import antifraud.persistence.repository.SuspiciousIPRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;



@DataJpaTest
class SuspiciousIPTest {

    @SpyBean
    SuspiciousIPRepository suspiciousIPRepositorySpy;

    private final String IP = "192.168.1.34";
    private final String NON_EXISTING_IP = "155.155.155.5";


    @BeforeEach
    void setUp() {
        IP suspiciousIP = IPFactory.create(IP);
        suspiciousIPRepositorySpy.save(suspiciousIP);
    }

    @Test
    void injected_components_are_not_null() {
        assertThat(suspiciousIPRepositorySpy).isNotNull();
    }

    @Test
    void given_existing_suspicious_IP_when_existByNumber_then_true() {
        boolean result = suspiciousIPRepositorySpy.existsByIpAddress(IP);

        assertTrue(result);
    }

    @Test
    void given_NOT_existing_suspicious_IP_when_existByNumber_then_false() {
        boolean expected = suspiciousIPRepositorySpy.existsByIpAddress(NON_EXISTING_IP);

        assertThat(expected).isFalse();
    }

    @Test
    void given_suspicious_IP_when_findByIpAddress_then_return_suspicious_IP() {
        Optional<IP> expected = suspiciousIPRepositorySpy.findByIpAddress(IP);

        assertThat(expected).isPresent();
        assertThat(expected.get().getIpAddress()).isEqualTo(IP);
    }

    @Test
    void given_wrong_suspicious_IP_when_findByIpAddress_then_is_empty() {
        Optional<IP> expected = suspiciousIPRepositorySpy.findByIpAddress(NON_EXISTING_IP);

        assertThat(expected).isNotPresent();
    }
}
