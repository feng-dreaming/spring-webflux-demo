package me.saker.webflux.demo.repository;

import me.saker.webflux.demo.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JdbcUserRepository extends JpaRepository<User, String> {

    List<User> findByUsername(final String username);

    User findOneByUsername(final String username);
}
