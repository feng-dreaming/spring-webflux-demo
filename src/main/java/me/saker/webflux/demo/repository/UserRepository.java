package me.saker.webflux.demo.repository;

import me.saker.webflux.demo.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> { //extends MongoRepository<User, String>
    Flux<User> findByUsername(final String username);
    Mono<User> findOneByUsername(final String username);
}
