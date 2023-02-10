package antifraud;

import antifraud.domain.model.CustomUser;
import antifraud.domain.model.CustomUserFactory;
import antifraud.persistence.repository.CustomUserRepository;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomUserRepositoryTest {

    @SpyBean
    CustomUserRepository customUserRepository;

    private final String NAME = "Johny Bravo";
    private final String USER_NAME = "JohnyBravo";
    private final String PASSWORD = "secret";
    private final String NON_EXISTING_USER_NAME = "GuyRichie";

    @BeforeEach
    void setUp() {
        CustomUser user = CustomUserFactory.create(NAME, USER_NAME, PASSWORD);
        customUserRepository.save(user);
    }

    @Test
    void injected_components_are_not_null() {
        assertThat(customUserRepository).isNotNull();
    }

    @Test
    void given_existing_user_when_existsByUsername_then_true() {
        boolean expected = customUserRepository.existsByUsername(USER_NAME);

        Assertions.assertThat(expected).isTrue();
    }

    @Test
    void given_NOT_existing_user_when_existsByUsername_then_false() {
        boolean expected = customUserRepository.existsByUsername(NON_EXISTING_USER_NAME);

        assertThat(expected).isFalse();
    }

    @Test
    void given_user_when_findByUsernameIgnoreCase_then_return_user() {
        Optional<CustomUser> expected = customUserRepository.findByUsernameIgnoreCase(USER_NAME);

        assertThat(expected).isPresent();
        assertThat(expected.get().getUsername()).isEqualTo(USER_NAME);
    }

    @Test
    void given_non_existing_user_when_findByUsernameIgnoreCase_then_isEmpty() {
        Optional<CustomUser> expected = customUserRepository.findByUsernameIgnoreCase(NON_EXISTING_USER_NAME);

        assertThat(expected).isNotPresent();
    }

    @Test
    void given_uppercase_lowercase_userName_when_findByUsernameIgnoreCase_then_return_user() {
        String lowerUpperUserName = "JOhnyBrAvo";

        Optional<CustomUser> expected = customUserRepository.findByUsernameIgnoreCase(lowerUpperUserName);

        assertThat(expected).isPresent();
        assertThat(expected.get().getUsername()).isEqualTo(USER_NAME);
    }
}
