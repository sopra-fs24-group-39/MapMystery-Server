package ch.uzh.ifi.hase.soprafs24.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UtilityServiceTest {

    private UtilityService utilityService;

    @BeforeEach
    public void setUp() {
        utilityService = new UtilityService();
    }

    @Test
    public void testAssertWithTrueExpression() {
        assertDoesNotThrow(() -> utilityService.Assert(true, "This should not throw an exception"));
    }

    @Test
    public void testAssertWithFalseExpression() {
        AssertionError exception = assertThrows(AssertionError.class, () -> utilityService.Assert(false, "This should throw an exception"));
        assertEquals("This should throw an exception", exception.getMessage());
    }
}
