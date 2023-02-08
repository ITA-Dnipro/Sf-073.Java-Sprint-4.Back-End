package antifraud.domain.service;

import antifraud.domain.model.CustomUser;
import antifraud.domain.model.CustomUserFactory;
import antifraud.domain.model.enums.UserRole;
import antifraud.domain.service.impl.CustomUserServiceImpl;
import antifraud.persistence.repository.CustomUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CustomUserServiceTest {
    @InjectMocks
    private CustomUserServiceImpl customUserServiceImpl;
    @Mock
    private CustomUserRepository customUserRepository;
    @Mock
    private PasswordEncoder encoder;
    private CustomUser user;

    @BeforeEach
    void setup() {
        this.user = CustomUserFactory.create("JohnDoe", "johndoe1", "secret");
    }

    @Test
    void WhenRegisterNewUserThenReturnRegisteredUser() {
        given(this.customUserRepository.save(user)).willReturn(user);

        Optional<CustomUser> customUser = customUserServiceImpl.registerUser(user);

        assertEquals(Optional.of(user), customUser);
    }

    @Test
    void WhenRegisterFirstUserThenRoleIsAdministrator() {
        given(this.customUserRepository.save(user)).willReturn(user);
        UserRole expectedRole = UserRole.ADMINISTRATOR;

        CustomUser customUser = customUserServiceImpl.registerUser(user).get();

        assertEquals(expectedRole, customUser.getRole());
    }

    @Test
    void WhenRegisterSecondUserThenRoleIsMerchant() {
        CustomUser secondUser = CustomUserFactory.create("JaneDoe", "jane333doe", "secretz");
        given(this.customUserRepository.save(user)).willReturn(user);
        given(this.customUserRepository.save(secondUser)).willReturn(secondUser);
        given(this.customUserRepository.count())
                .willReturn(0L)
                .willReturn(1L);
        UserRole expectedRoleFirstUser = UserRole.ADMINISTRATOR;
        UserRole expectedRoleSecondUser = UserRole.MERCHANT;

        CustomUser customUser = customUserServiceImpl.registerUser(user).get();
        CustomUser secondCustomUser = customUserServiceImpl.registerUser(secondUser).get();

        assertAll(
                () -> assertEquals(expectedRoleFirstUser, customUser.getRole()),
                () -> assertEquals(expectedRoleSecondUser, secondCustomUser.getRole())
        );
    }

    @Test
    void WhenRegisterNewUserTwiceThenFirstTimeReturnUserAndSecondTimeReturnEmpty() {
        given(this.customUserRepository.existsByUsername(user.getUsername()))
                .willReturn(false)
                .willReturn(true);
        given(this.customUserRepository.save(user)).willReturn(user);
        Optional<CustomUser> expectedFirstSave = Optional.of(user);

        Optional<CustomUser> firstTimeRegister = customUserServiceImpl.registerUser(user);
        Optional<CustomUser> secondTimeRegister = customUserServiceImpl.registerUser(user);

        assertAll(
                () -> assertEquals(expectedFirstSave, firstTimeRegister),
                () -> assertThat(secondTimeRegister).isEmpty());
    }

    @Test
    void WhenRegisterExistingUserThenReturnEmpty() {
        given(this.customUserRepository.existsByUsername(user.getUsername()))
                .willReturn(true);

        Optional<CustomUser> customUser = customUserServiceImpl.registerUser(user);

        assertThat(customUser).isEmpty();
    }

}