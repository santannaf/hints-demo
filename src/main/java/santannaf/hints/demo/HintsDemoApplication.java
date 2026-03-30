package santannaf.hints.demo;

import com.tanna.annotation.EnabledArchKafka;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnabledArchKafka(appName = "hints-demo")
public class HintsDemoApplication {
    static void main(String[] args) {
        SpringApplication.run(HintsDemoApplication.class, args);
    }
}
