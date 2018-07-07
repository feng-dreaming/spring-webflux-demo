package me.saker.webflux.demo.repository;

import me.saker.webflux.demo.domain.Role;
import me.saker.webflux.demo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MongoDBPopulator implements CommandLineRunner {

    // Test
    private static final User ADMIN = new User("", "admin", "password", "Default", "Admin", Role.ADMIN, true);

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        userRepository.count()
                .doOnNext(count -> {
                    log.info("MongoDB started successfully. Users count: " + count);
                    if (count == 0) {
                        createDefaultAdminUser();
                    }
                })
                .doOnError(error
                        -> log.warn("Can't connect to MongoDB. Did you forget to start it with 'mongod --dbpath=<path-to-mongo-data-directory>'?"))
                .subscribe();
    }

    /* Pre-populate MongoDB with default admin user */
    private void createDefaultAdminUser() {
        userRepository.save(ADMIN)
                .subscribe(user -> log.info("Users not found - adding default Admin user: " + user));
    }

}
