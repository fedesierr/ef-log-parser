package com.ef;

import com.ef.model.Duration;
import joptsimple.BuiltinHelpFormatter;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.JOptCommandLinePropertySource;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.TimeZone;

/**
 * @author fsierra on 2019-07-28
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception{

        OptionParser parser = optionParser();
        try{
            OptionSet options = parser.parse(args);

            if (options.has("help")) {
                parser.printHelpOn(System.out);
                return;
            }

            JOptCommandLinePropertySource jOptSource = new JOptCommandLinePropertySource(options);
            AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
            ctx.getEnvironment().getPropertySources().addFirst(jOptSource);
            ctx.refresh();

            System.out.println("Use access log file: " + ctx.getEnvironment().getProperty("accesslog"));
        } catch (OptionException e) {
            parser.printHelpOn(System.out);
            return;
        }

        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @PostConstruct
    public void init(){
        // Setting Spring Boot SetTimeZone
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    private static OptionParser optionParser() {

        return new OptionParser() {
            {
                accepts("startDate", "start date in \"yyyy-MM-dd.HH:mm:ss\" format")
                        .withRequiredArg()
                        .required();

                accepts("duration", "duration \"hourly\", \"daily\" as inputs")
                        .withRequiredArg().ofType(Duration.class).required();

                accepts("threshold", "threshold int value")
                        .withRequiredArg().ofType(Integer.class).required();

                accepts("accesslog", "path to access log file")
                        .withRequiredArg().required();

                acceptsAll(Arrays.asList("help", "h", "?"), "show help").forHelp();

                formatHelpWith(new BuiltinHelpFormatter(120, 2));
            }
        };
    }

}

