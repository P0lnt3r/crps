package zy.pointer.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@ComponentScan("zy.pointer")
public class EthAddSyncStartUp {

    public static void main(String[] args) {
        SpringApplication.run( EthAddSyncStartUp.class , args );
    }

}
