package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private final UserDto userDto = new UserDto(
            null,
            "marek@mail.ru",
            "Marek");

    private final User user = new User(
            1L,
            "marek@mail.ru",
            "Marek");

    @Test
    void createUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto createdUser = userService.create(userDto);

        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(1, createdUser.getId());
        Assertions.assertEquals(userDto.getName(), createdUser.getName());
        Assertions.assertEquals(userDto.getEmail(), createdUser.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findAll() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> users = userService.findAll();

        Assertions.assertNotNull(users);
        Assertions.assertEquals(1, users.size());
        Assertions.assertEquals(1, users.get(0).getId());
        Assertions.assertEquals(userDto.getName(), users.get(0).getName());
        Assertions.assertEquals(userDto.getEmail(), users.get(0).getEmail());

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto user = userService.findUserById(1L);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(1L, user.getId());
        Assertions.assertEquals(userDto.getName(), user.getName());
        Assertions.assertEquals(userDto.getEmail(), user.getEmail());

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserById() {
        UserDto newUpdatedUserDto = new UserDto(null, "vov23@gmail.com","Vovan");
        User newUpdatedUser = new User(1L, "vov23@gmail.com","Vovan");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(newUpdatedUser);

        UserDto updatedUser = userService.update(newUpdatedUserDto, 1L);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(1, updatedUser.getId());
        Assertions.assertEquals(newUpdatedUserDto.getName(), updatedUser.getName());
        Assertions.assertEquals(newUpdatedUserDto.getEmail(), updatedUser.getEmail());

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void delUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delUserById(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(userRepository);
    }

}