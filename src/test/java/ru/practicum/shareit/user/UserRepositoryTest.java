package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/*
@Transactional
//@Rollback(false)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://192.168.0.108:7779/shareit",
        "spring.datasource.username=dbuser",
        "spring.datasource.password=12345"
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
*/

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    private final User user = new User(
            null,
            "tj00@mail.ru",
            "Tanja");

    @Test
    void createUser() {
        userRepository.save(user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userTarg = query.setParameter("email", user.getEmail())
                .getSingleResult();

        /*
        Assertions.assertNotNull(found);
        Assertions.assertEquals(1L, found.getId());
        Assertions.assertEquals(user.getName(), found.getName());
        Assertions.assertEquals(user.getEmail(), found.getEmail()); */

        assertThat(userTarg.getId(), notNullValue());
        assertThat(userTarg.getName(), equalTo(user.getName()));
        assertThat(userTarg.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void deleteUserById() {
        Assertions.assertNull(user.getId());
        em.persist(user);
        em.flush();
        Assertions.assertNotNull(user.getId());

        userRepository.deleteById(user.getId());

        User userFound = userRepository.findById(user.getId()).orElse(null);

        Assertions.assertNull(userFound);
    }
}
