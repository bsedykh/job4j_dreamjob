package ru.job4j.dreamjob.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.model.User;

import java.util.Optional;

@Repository
public class Sql2oUserRepository implements UserRepository {
    private static final Logger LOG = LoggerFactory.getLogger(Sql2oUserRepository.class.getName());
    private final Sql2o sql2o;

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        Optional<User> result = Optional.empty();
        try (var connection = sql2o.open()) {
            var sql = """
                    INSERT INTO users(email, name, password)
                    VALUES (:email, :name, :password)
                    """;
            var query = connection.createQuery(sql, true)
                    .addParameter("email", user.getEmail())
                    .addParameter("name", user.getName())
                    .addParameter("password", user.getPassword());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            user.setId(generatedId);
            result = Optional.of(user);
        } catch (Sql2oException e) {
            LOG.warn("Could not save user", e);
        }
        return result;
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("""
                            SELECT * FROM users
                            WHERE email = :email AND password = :password
                            """);
            query.addParameter("email", email);
            query.addParameter("password", password);
            var user = query.executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }
    }

    public void deleteAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("""
                            DELETE FROM users
                            """);
            query.executeUpdate();
        }
    }
}
