package com.ef.job;

import com.ef.model.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author fsierra on 2019-07-28
 */
@Component
public class CommandLineBatch implements CommandLineRunner {

    @Autowired
    private Environment env;

    @Value("${startDate}")
    private String startDate;

    @Value("${duration}")
    private Duration duration;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(startDate);
        System.out.println(duration);
    }
}
