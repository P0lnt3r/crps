package zy.pointer.sync.pmeth.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@ComponentScan("zy.pointer")
public class EthSyncStartUp {

    public static void main(String[] args) {
        SpringApplication.run( EthSyncStartUp.class , args );
    }

}
