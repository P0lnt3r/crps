package zy.pointer.crps.sync.local.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan("zy.pointer")
@ServletComponentScan("zy.pointer") // Filter & Listener For (WebApplicationServer-Tomcat)
@EnableAsync
public class StartUp {

    public static void main(String[] args) {
        SpringApplication.run( StartUp.class , args );
    }

}