package antifraud.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomUserTest {

    @Test
    void WhenCreatingUserFromFactoryThenReturnObjectWithCreatedFields() {
        String expectedName = "John";
        String expectedUsername = "johndoe1";
        String expectedPass = "secret";

        CustomUser customUser = CustomUserFactory.create("John", "johndoe1", "secret");

        assertAll(
                () -> assertEquals(expectedName, customUser.getName()),
                () -> assertEquals(expectedUsername, customUser.getUsername()),
                () -> assertEquals(expectedPass, customUser.getPassword())
        );
    }
}