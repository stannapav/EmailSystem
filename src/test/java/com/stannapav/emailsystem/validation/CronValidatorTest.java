package com.stannapav.emailsystem.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class CronValidatorTest {
    private CronValidator cronValidator;

    @BeforeEach
    void setUp() {
        cronValidator = new CronValidator();
    }

    @Test
    void CronValidator_ValidCron_DoesNotThrow() {
        assertDoesNotThrow(() -> cronValidator.validate("0 0 12 * * ?"));
    }

    @Test
    void CronValidator_InvalidCron_ThrowsIllegalArgumentException() {
        Assertions.assertThatThrownBy(() -> cronValidator.validate("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid cron expression");
    }
}
