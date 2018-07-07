package me.saker.webflux.demo.handlers;

import lombok.extern.slf4j.Slf4j;
import me.saker.webflux.demo.domain.User;
import me.saker.webflux.demo.repository.JdbcUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;


/**
 * https://github.com/chang-chao/spring-webflux-async-jdbc-sample
 * @author jing
 */
@Component
@Slf4j
public class JdbcUserHandler {
    
    @Autowired
    private JdbcUserRepository userRepo;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    @Qualifier("jdbcScheduler")
    private Scheduler jdbcScheduler;
    
    public Flux<User> findAll() {
        Flux<User> defer = Flux.defer(() -> Flux.fromIterable(this.userRepo.findAll()));
        return defer.subscribeOn(jdbcScheduler);
    }
    
    public Mono<User> add(String name) {
        return Mono.fromCallable(() -> transactionTemplate.execute(status -> {
            User user = new User();
            user.setUsername(name);
            User savedUser = userRepo.save(user);
            return savedUser;
        })).subscribeOn(jdbcScheduler);
    }
}
