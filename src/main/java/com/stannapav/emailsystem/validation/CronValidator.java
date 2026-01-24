package com.stannapav.emailsystem.validation;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import org.springframework.stereotype.Component;

@Component
public class CronValidator {
    private final CronParser cronParser;

    public CronValidator() {
        CronDefinition definition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
        this.cronParser = new CronParser(definition);
    }

    public void validate(String expression) {
        try {
            cronParser.parse(expression).validate();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cron expression");
        }
    }
}
