package zy.pointer.crps.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("zy.pointer")
@ServletComponentScan("zy.pointer") // Filter & Listener For (WebApplicationServer-Tomcat)
public class StartUp {

    public static void main(String[] args) {
        System.out.println("Hello! 1111222");
        SpringApplication.run( StartUp.class , args );
    }

}
