package antifraud.domain.service.servicesImpl;

import antifraud.domain.model.IP;
import antifraud.domain.service.impl.SuspiciousIPServiceImpl;
import antifraud.exceptions.IpNotFoundException;
import antifraud.persistence.repository.SuspiciousIPRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SuspiciousIPServiceImplTest {

    @InjectMocks
    private SuspiciousIPServiceImpl suspiciousIPService;

    @Mock
    private SuspiciousIPRepository suspiciousIPRepositoryMock;
    private IP ip;

    @BeforeEach
    void setUp() {
        this.ip = new IP(1L, "19.117.63.126");
    }

    @Nested
    class SaveSuspiciousAddressTests {
        @Test
        void should_ReturnOptionalEmpty_When_IPExists() {
            when(suspiciousIPRepositoryMock.existsByIpAddress(anyString()))
                    .thenReturn(true);

            Optional<IP> actual = suspiciousIPService.saveSuspiciousAddress(ip);
            assertTrue(actual.isEmpty());
        }

        @Test
        void should_ReturnSavedIP_When_IPDoesntExists() {
            when(suspiciousIPRepositoryMock.existsByIpAddress(anyString()))
                    .thenReturn(false);
            when(suspiciousIPRepositoryMock.save(ip)).thenReturn(ip);

            Optional<IP> actual = suspiciousIPService.saveSuspiciousAddress(ip);
            assertFalse(actual.isEmpty());
        }

        @Test
        void should_InvokeAllInnerMethods_When_SavingIP() {
            given(suspiciousIPRepositoryMock.save(ip)).willReturn(ip);

            suspiciousIPService.saveSuspiciousAddress(ip);
            then(suspiciousIPRepositoryMock).should(times(1)).existsByIpAddress(anyString());
            verifyNoMoreInteractions(suspiciousIPRepositoryMock);
        }
    }

    @Nested
    class RemoveIpAddressTests {

        @Test
        void should_Throw_When_IPNotFound() {
            when(suspiciousIPRepositoryMock.findByIpAddress(anyString()))
                    .thenThrow(IpNotFoundException.class);
            Executable executable = () -> suspiciousIPService.removeIpAddress(anyString());
            assertThrows(IpNotFoundException.class, executable);
        }

        @Test
        void should_RemoveIP_When_IPIsPresent() {
            when(suspiciousIPRepositoryMock.findByIpAddress(anyString()))
                    .thenReturn(Optional.of(ip));
            Executable executable = () -> suspiciousIPService.removeIpAddress(anyString());
            assertDoesNotThrow(executable);
        }

        @Test
        void should_NotInvokeDeleteById_When_IPNotFound() {
            Executable executable = () -> suspiciousIPService.removeIpAddress(any());

            then(suspiciousIPRepositoryMock).should(never()).deleteById(any());
            verifyNoMoreInteractions(suspiciousIPRepositoryMock);
        }
    }

    @Nested
    class ShowIpAddresses {
        @Test
        void should_ReturnList_When_IPRepositoryIsEmpty_And_When_IPRepoHasOneElement() {
            when(suspiciousIPService.showIpAddresses())
                    .thenReturn(Collections.singletonList(ip))
                    .thenReturn(Collections.emptyList());
            int expectedFirst = 1;
            int expectedSecond = 0;

            int actualFirst = suspiciousIPService.showIpAddresses().size();
            int actualSecond = suspiciousIPService.showIpAddresses().size();

            assertAll(
                    () -> assertEquals(expectedFirst, actualFirst),
                    () -> assertEquals(expectedSecond, actualSecond)
            );
        }
    }

    @Nested
    class ExistsByIpAddress {
        @Test
        void should_ReturnFalse_When_IPDoesntExists() {
            when(suspiciousIPService.existsByIpAddress(anyString()))
                    .thenReturn(false);
            boolean expected = suspiciousIPRepositoryMock.existsByIpAddress(anyString());
            assertFalse(expected);
        }

        @Test
        void should_ReturnTrue_When_IPExists() {
            when(suspiciousIPService.existsByIpAddress(anyString()))
                    .thenReturn(true);
            boolean expected = suspiciousIPRepositoryMock.existsByIpAddress(anyString());
            assertTrue(expected);
        }
    }
}