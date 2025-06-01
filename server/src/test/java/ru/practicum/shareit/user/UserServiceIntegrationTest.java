package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/*
@Transactional
//@Rollback(false)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
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
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;
    @Autowired
    private EntityManager em;

    private final UserDto userDto = new UserDto(
            null,
            "tj00@mail.ru",
            "Tanja");

    @Test
    void createNewUser() {
        UserDto createdUser = userService.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getUserByWrongId() {
        Long userId = 0L;

        Assertions
                .assertThrows(NotFoundException.class, () -> userService.findUserById(userId));
    }
}
