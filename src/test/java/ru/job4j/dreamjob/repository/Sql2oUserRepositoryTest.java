package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepository() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        sql2oUserRepository.deleteAll();
    }

    @Test
    public void whenSaveThenGetSame() {
        var user = sql2oUserRepository.save(
                new User(0, "ivan@test.com", "Ivan", "123")).get();
        var savedUser = sql2oUserRepository.findByEmailAndPassword(
                user.getEmail(), user.getPassword());
        assertThat(savedUser.get()).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenSaveUserWithDuplicateEmail() {
        assertThat(sql2oUserRepository.save(
                new User(0, "ivan@test.com", "Ivan1", "123"))).isNotEmpty();
        assertThat(sql2oUserRepository.save(
                new User(0, "ivan@test.com", "Ivan2", "321"))).isEmpty();
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oUserRepository.findByEmailAndPassword(
                "ivan@test.com", "123")).isEmpty();
    }
}
