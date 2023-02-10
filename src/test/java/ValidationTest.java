import antifraud.domain.model.enums.TransactionResult;
import antifraud.domain.model.enums.WorldRegion;
import antifraud.rest.dto.*;
import antifraud.validation.AvailableRegion;
import antifraud.validation.IpAddress;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
public class ValidationTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Testing validator wrong feedback")
    public void testWrongFeedbackValidator() {
        TransactionFeedbackDTO feedback = new TransactionFeedbackDTO(1L,
                100L,
                "",
                "",
                WorldRegion.EAP,
                LocalDateTime.now(),
                TransactionResult.ALLOWED,
                "ALLOW");
        Set<ConstraintViolation<TransactionFeedbackDTO>> violations = validator.validate(feedback);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Testing validator wrong message")
    public void testWrongFeedbackMessageValidator() {
        TransactionFeedbackDTO feedback = new TransactionFeedbackDTO(1L,
                100L,
                "",
                "",
                WorldRegion.EAP,
                LocalDateTime.now(),
                TransactionResult.ALLOWED,
                "ALLOW");
        Set<ConstraintViolation<TransactionFeedbackDTO>> violations = validator.validate(feedback);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(p -> p.getMessage().equals("{antifraud.validation.AvailableFeedback.invalid}")));
    }

    @Test
    @DisplayName("Testing validator empty feedback")
    public void testEmptyFeedbackValidator() {
        TransactionFeedbackDTO feedback = new TransactionFeedbackDTO(1L,
                100L,
                "",
                "",
                WorldRegion.EAP,
                LocalDateTime.now(),
                TransactionResult.ALLOWED,
                "");
        Set<ConstraintViolation<TransactionFeedbackDTO>> violations = validator.validate(feedback);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Testing validator ALLOWED feedback")
    public void testAllowedFeedbackValidator() {
        TransactionFeedbackDTO feedback = new TransactionFeedbackDTO(1L,
                100L,
                "",
                "",
                WorldRegion.EAP,
                LocalDateTime.now(),
                TransactionResult.ALLOWED,
                "ALLOWED");
        Set<ConstraintViolation<TransactionFeedbackDTO>> violations = validator.validate(feedback);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Testing validator wrong region")
    public void testWrongRegionValidator() {
        TransactionDTO transactionDTO = new TransactionDTO(100L,
                "192.168.0.1",
                "4000008449433403",
                "WWW",
                LocalDateTime.now(),
                TransactionResult.ALLOWED,
                "");
        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Testing validator empty region")
    public void testEmptyRegionValidator() {
        TransactionDTO transactionDTO = new TransactionDTO(100L,
                "192.168.0.1",
                "4000008449433403",
                "",
                LocalDateTime.now(),
                TransactionResult.ALLOWED,
                "");
        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Testing validator wrong region message")
    public void testWrongRegionMessageValidator() {
        TransactionDTO transactionDTO = new TransactionDTO(100L,
                "192.168.0.1",
                "4000008449433403",
                "",
                LocalDateTime.now(),
                TransactionResult.ALLOWED,
                "");
        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(p -> p.getMessage().equals("{antifraud.validation.AvailableRegion.invalid}")));
    }

    @Test
    @DisplayName("Testing validator EAP region")
    public void testCorrectRegionValidator() {
            TransactionDTO transactionDTO = new TransactionDTO(100L,
                "192.168.0.1",
                "4000008449433403",
                "EAP",
                LocalDateTime.now(),
                TransactionResult.ALLOWED,
                "");
        Set<ConstraintViolation<TransactionDTO>> violations = validator.validate(transactionDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Testing validator wrong role")
    public void testWrongRoleValidator() {
        UserRoleDTO userRoleDTO = new UserRoleDTO("test",
                "WWWW");
        Set<ConstraintViolation<UserRoleDTO>> violations = validator.validate(userRoleDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Testing validator empty role")
    public void testEmptyRoleValidator() {
        UserRoleDTO userRoleDTO = new UserRoleDTO("test",
                "");
        Set<ConstraintViolation<UserRoleDTO>> violations = validator.validate(userRoleDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Testing validator wrong role message")
    public void testWrongRoleMessageValidator() {
        UserRoleDTO userRoleDTO = new UserRoleDTO("test",
                "WWW");
        Set<ConstraintViolation<UserRoleDTO>> violations = validator.validate(userRoleDTO);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(p -> p.getMessage().equals("{antifraud.validation.AvailableRole.invalid}")));
    }

    @Test
    @DisplayName("Testing validator ADMINISTRATOR role")
    public void testCorrectRoleValidator() {
        UserRoleDTO userRoleDTO = new UserRoleDTO("test",
                "ADMINISTRATOR");
        Set<ConstraintViolation<UserRoleDTO>> violations = validator.validate(userRoleDTO);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Testing validator wrong ip")
    public void testWrongIPValidator() {
        IpDTO ipDTO = new IpDTO(0L,
                "256.168.0.1");
        Set<ConstraintViolation<IpDTO>> violations = validator.validate(ipDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Testing validator empty ip")
    public void testEmptyIPValidator() {
        UserRoleDTO userRoleDTO = new UserRoleDTO("test",
                "");
        Set<ConstraintViolation<UserRoleDTO>> violations = validator.validate(userRoleDTO);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Testing validator wrong ip message")
    public void testWrongIPMessageValidator() {
        IpDTO ipDTO = new IpDTO(0L,
                "192.168.333.1");
        Set<ConstraintViolation<IpDTO>> violations = validator.validate(ipDTO);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(p -> p.getMessage().equals("{antifraud.validation.IpAddress.invalid}")));
    }

    @Test
    @DisplayName("Testing validator ip")
    public void testCorrectIPValidator() {
        IpDTO ipDTO = new IpDTO(0L,
                "192.168.0.1");
        Set<ConstraintViolation<IpDTO>> violations = validator.validate(ipDTO);
        assertTrue(violations.isEmpty());
    }
}