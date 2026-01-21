package com.stannapav.emailsystem.configs;

import com.stannapav.emailsystem.db.services.CronService;
import com.stannapav.emailsystem.db.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public UserService userBean() {
        return new UserService();
    }

    @Bean
    public CronService cronJobBean() { return new CronService(); }

    @Bean
    public ModelMapper modelMapperBean() {
        return new ModelMapper();
    }
}
