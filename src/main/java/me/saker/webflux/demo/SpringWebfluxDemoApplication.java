package me.saker.webflux.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class SpringWebfluxDemoApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(SpringWebfluxDemoApplication.class, args);
    }

//    @Bean
//    public Scheduler jdbcScheduler() {
//        return Schedulers.fromExecutor(Executors.newFixedThreadPool(100));
//    }
}
