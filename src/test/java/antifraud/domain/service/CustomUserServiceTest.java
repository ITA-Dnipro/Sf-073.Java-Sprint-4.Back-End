package antifraud.domain.service;

import antifraud.domain.model.CustomUser;
import antifraud.domain.model.CustomUserFactory;
import antifraud.domain.model.UserPrincipal;
import antifraud.domain.model.enums.UserAccess;
import antifraud.domain.model.enums.UserRole;
import antifraud.domain.service.impl.CustomUserServiceImpl;
import antifraud.exceptions.AccessViolationException;
import antifraud.exceptions.AlreadyProvidedException;
import antifraud.exceptions.ExistingAdministratorException;
import antifraud.persistence.repository.CustomUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CustomUserServiceTest {

    private CustomUserService customUserService;
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
        customUserService = customUserServiceImpl;
    }

    @Test
    void WhenRegisterNewUserThenReturnRegisteredUser() {
        given(this.customUserRepository.save(user)).willReturn(user);

        Optional<CustomUser> customUser = customUserService.registerUser(user);

        assertEquals(Optional.of(user), customUser);
    }

    @Test
    void WhenRegisterNewUserThenInvokeAllInnerMethods() {
        given(this.customUserRepository.save(user)).willReturn(user);
        String password = user.getPassword();
        String username = user.getUsername();

        customUserService.registerUser(user);

//        verify(encoder, times(1)).encode(password);
        then(encoder).should(times(1)).encode(password);
        verifyNoMoreInteractions(encoder);
        then(customUserRepository).should(times(1)).count();
        then(customUserRepository).should(times(1)).existsByUsername(username);
        then(customUserRepository).should(times(1)).save(user);
        verifyNoMoreInteractions(customUserRepository);
    }

    @Test
    void WhenRegisterFirstUserThenRoleIsAdministrator() {
        given(this.customUserRepository.save(user)).willReturn(user);
        UserRole expectedRole = UserRole.ADMINISTRATOR;

        CustomUser customUser = customUserService.registerUser(user).get();

        assertEquals(expectedRole, customUser.getRole());
    }

    @Test
    void WhenRegisterFirstUserThenAccessIsUnlocked() {
        given(this.customUserRepository.save(user)).willReturn(user);
        UserAccess expectedAccess = UserAccess.UNLOCK;

        CustomUser customUser = customUserService.registerUser(user).get();

        assertEquals(expectedAccess, customUser.getAccess());
    }

    @Test
    void WhenRegisterSecondUserThenRoleIsMerchant() {
        CustomUser secondUser = CustomUserFactory.create("JaneDoe", "jane333doe", "secretz");
        given(this.customUserRepository.save(any()))
                .willReturn(user)
                .willReturn(secondUser);
        given(this.customUserRepository.count())
                .willReturn(0L)
                .willReturn(1L);
        UserRole expectedRoleFirstUser = UserRole.ADMINISTRATOR;
        UserRole expectedRoleSecondUser = UserRole.MERCHANT;

        CustomUser customUser = customUserService.registerUser(user).get();
        CustomUser secondCustomUser = customUserService.registerUser(secondUser).get();

        assertAll(
                () -> assertEquals(expectedRoleFirstUser, customUser.getRole()),
                () -> assertEquals(expectedRoleSecondUser, secondCustomUser.getRole())
        );
    }

    @Test
    void WhenRegisterSecondUserThenAccessIsLocked() {
        CustomUser secondUser = CustomUserFactory.create("JaneDoe", "jane333doe", "secretz");
        given(this.customUserRepository.save(any()))
                .willReturn(user)
                .willReturn(secondUser);
        given(this.customUserRepository.count())
                .willReturn(0L)
                .willReturn(1L);
        UserAccess expectedAccessFirstUser = UserAccess.UNLOCK;
        UserAccess expectedAccessSecondUser = UserAccess.LOCK;

        CustomUser customUser = customUserService.registerUser(user).get();
        CustomUser secondCustomUser = customUserService.registerUser(secondUser).get();

        assertAll(
                () -> assertEquals(expectedAccessFirstUser, customUser.getAccess()),
                () -> assertEquals(expectedAccessSecondUser, secondCustomUser.getAccess())
        );
    }

    @Test
    void WhenRegisterNewUserTwiceThenFirstTimeReturnUserAndSecondTimeReturnEmpty() {
        given(this.customUserRepository.existsByUsername(user.getUsername()))
                .willReturn(false)
                .willReturn(true);
        given(this.customUserRepository.save(user)).willReturn(user);
        Optional<CustomUser> expectedFirstSave = Optional.of(user);

        Optional<CustomUser> firstTimeRegister = customUserService.registerUser(user);
        Optional<CustomUser> secondTimeRegister = customUserService.registerUser(user);

        assertAll(
                () -> assertEquals(expectedFirstSave, firstTimeRegister),
                () -> assertThat(secondTimeRegister).isEmpty());
    }

    @Test
    void WhenRegisterExistingUserThenReturnEmpty() {
        String username = user.getUsername();
        given(this.customUserRepository.existsByUsername(username))
                .willReturn(true);

        Optional<CustomUser> customUser = customUserService.registerUser(user);

        assertThat(customUser).isEmpty();
    }

    @Test
    void WhenRegisterExistentUserThenNotInvokeSave() {
        String username = user.getUsername();
        given(this.customUserRepository.existsByUsername(username))
                .willReturn(true);

        customUserService.registerUser(user);

        then(customUserRepository).should(never()).save(any());
    }

    @Test
    void WhenRepoIsEmptyThenGetUsersReturnEmptyCollection() {
        List<CustomUser> users = customUserService.getUsers();

        assertThat(users).isEmpty();
    }

    @Test
    void WhenRepoIsNotEmptyThenGetUsersReturnCollection() {
        List<CustomUser> customUsers = Arrays.asList(user,
                CustomUserFactory.create("JaneDoe", "jane333doe", "secretz"));
        given(this.customUserRepository.findAll())
                .willReturn(customUsers);
        int expectedSize = 2;

        List<CustomUser> users = customUserService.getUsers();

        assertEquals(expectedSize, users.size());
    }

    @Test
    void WhenDeletingNonExistentUserThenThrowException() {
        Executable executable = () -> customUserService.deleteUser(any());

        assertThrows(UsernameNotFoundException.class, executable);
    }

    @Test
    void WhenDeleteNonExistentUserThrowExceptionThenDoNotInvokeDelete() {
        String username = user.getUsername();

        Executable executable = () -> customUserService.deleteUser(username);

        then(customUserRepository).should(never()).deleteById(any());
        verifyNoMoreInteractions(customUserRepository);
    }

    @Test
    void WhenDeletingExistentUserThenDoesNotThrowException() {
        String username = user.getUsername();
        given(customUserRepository.findByUsernameIgnoreCase(username))
                .willReturn(Optional.of(user));

        Executable executable = () -> customUserService.deleteUser(username);

        assertDoesNotThrow(executable);
    }

    @Test
    void WhenChangingRoleToNonExistentUserThenThrowException() {
        Executable executable = () -> customUserService.changeUserRole(user);

        assertThrows(UsernameNotFoundException.class, executable);
    }

    @Test
    void WhenChangeRoleWithSameRoleThenThrowException() {
        String username = user.getUsername();
        user.setRole(UserRole.MERCHANT);
        given(customUserRepository.findByUsernameIgnoreCase(username))
                .willReturn(Optional.of(user));

        Executable executable = () -> customUserService.changeUserRole(user);

        assertThrows(AlreadyProvidedException.class, executable);
    }

    @Test
    void WhenChangeAdministratorRoleThenThrowException() {
        CustomUser secondUser = CustomUserFactory.create("JaneDoe", "jane333doe", "secretz");
        secondUser.setRole(UserRole.MERCHANT);
        user.setRole(UserRole.ADMINISTRATOR);
        String username = user.getUsername();
        given(customUserRepository.findByUsernameIgnoreCase(username))
                .willReturn(Optional.of(secondUser));

        Executable executable = () -> customUserService.changeUserRole(user);

        assertThrows(ExistingAdministratorException.class, executable);
    }

    @Test
    void WhenChangingNonConflictRoleThenTheRoleWillBeChanged() {
        CustomUser userInDB = CustomUserFactory.create("JohnDoe", "johndoe1", "secret");
        userInDB.setRole(UserRole.MERCHANT);
        user.setRole(UserRole.SUPPORT);
        String username = user.getUsername();
        given(customUserRepository.findByUsernameIgnoreCase(username))
                .willReturn(Optional.of(userInDB));
        UserRole expectedRole = UserRole.SUPPORT;

        customUserService.changeUserRole(user);
        UserRole resultRole = userInDB.getRole();

        assertEquals(expectedRole, resultRole);
    }

    @Test
    void WhenChangeUserRoleThenInvokeSave() {
        CustomUser userInDB = CustomUserFactory.create("JohnDoe", "johndoe1", "secret");
        userInDB.setRole(UserRole.MERCHANT);
        user.setRole(UserRole.SUPPORT);
        String username = user.getUsername();
        given(customUserRepository.findByUsernameIgnoreCase(username))
                .willReturn(Optional.of(userInDB));

        customUserService.changeUserRole(user);

        then(customUserRepository).should(times(1)).save(userInDB);
        verifyNoMoreInteractions(customUserRepository);
    }

    @Test
    void WhenChangingAccessToNonExistentUserThenThrowException() {
        Executable executable = () -> customUserService.grantAccess(user);

        assertThrows(UsernameNotFoundException.class, executable);
    }

    @Test
    void WhenChangingAccessToAdministratorThenThrowException() {
        user.setRole(UserRole.ADMINISTRATOR);
        String username = user.getUsername();
        given(customUserRepository.findByUsernameIgnoreCase(username))
                .willReturn(Optional.of(user));

        Executable executable = () -> customUserService.grantAccess(user);

        assertThrows(AccessViolationException.class, executable);
    }

    @Test
    void WhenChangeAccessThenAccessWillBeChanged() {
        CustomUser userInDB = CustomUserFactory.create("JohnDoe", "johndoe1", "secret");
        userInDB.setAccess(UserAccess.LOCK);
        user.setAccess(UserAccess.UNLOCK);
        String username = user.getUsername();
        given(customUserRepository.findByUsernameIgnoreCase(username))
                .willReturn(Optional.of(userInDB));
        UserAccess expectedAccessLevel = UserAccess.UNLOCK;

        customUserService.grantAccess(user);
        UserAccess resultAccess = userInDB.getAccess();

        assertEquals(expectedAccessLevel, resultAccess);
    }

    @Test
    void WhenChangeAccessThenInvokeSave() {
        CustomUser userInDB = CustomUserFactory.create("JohnDoe", "johndoe1", "secret");
        userInDB.setAccess(UserAccess.LOCK);
        user.setAccess(UserAccess.UNLOCK);
        String username = user.getUsername();
        given(customUserRepository.findByUsernameIgnoreCase(username))
                .willReturn(Optional.of(userInDB));

        customUserService.grantAccess(user);

        then(customUserRepository).should(times(1)).save(userInDB);
        verifyNoMoreInteractions(customUserRepository);
    }

    @Test
    void WhenRetrievingNonExistentUsernameThenThrowException() {
        Executable executable = () -> customUserService.retrieveRealUsername(any());

        assertThrows(UsernameNotFoundException.class, executable);
    }

    @Test
    void WhenRetrievingUsernameThenReturnRealUsername() {
        CustomUser userInDB = CustomUserFactory.create("JohnDoe", "JoHnDoe1", "secret");
        String expectedUsername = "JoHnDoe1";
        given(customUserRepository.findByUsernameIgnoreCase(expectedUsername))
                .willReturn(Optional.of(userInDB));

        customUserService.retrieveRealUsername(expectedUsername);

        assertEquals(expectedUsername, userInDB.getUsername());
    }

    @Test
    void WhenLoadByNonExistentUsernameThenThrowException() {
        Executable executable = () -> customUserService.loadUserByUsername(any());

        assertThrows(UsernameNotFoundException.class, executable);
    }

    @Test
    void WhenLoadByUsernameThenReturnUserPrincipal() {
        user.setRole(UserRole.MERCHANT);
        UserPrincipal userPrincipal = new UserPrincipal(user);
        String username = user.getUsername();
        given(customUserRepository.findByUsernameIgnoreCase(username))
                .willReturn(Optional.of(user));

        UserDetails userDetails = customUserService.loadUserByUsername(username);

        assertThat(userPrincipal).usingRecursiveComparison().isEqualTo(userDetails);
    }

}