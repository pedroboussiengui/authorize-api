package org.example.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class UserTest {

    @Test
    public void test_validateUser_withCorrectInputs_shouldReturnSuccess() {
        User user = new User("email@email.com", "John Doe");
        assertTrue(user.validate());
    }

    @Test
    public void test_validateUser_withIncorrectName_shouldReturnFail() {
        User user = new User("email@email.com", "Jo");
        assertFalse(user.validate());
    }

    @Test
    public void test_validateUser_withIncorrectEmail_shouldReturnFail() {
        User user = new User("email", "John Doe");
        assertFalse(user.validate());
    }
}
