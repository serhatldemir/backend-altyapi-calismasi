package com.staj.backend_gorev.service;

import com.staj.backend_gorev.dto.UserDTO;
import com.staj.backend_gorev.entity.User;
import com.staj.backend_gorev.mapper.UserMapper;
import com.staj.backend_gorev.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Ali", "Veli");
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Ali");
        userDTO.setSurname("Veli");
    }

    @Test
    void getAllUsers_ShouldReturnUserDTOList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        List<UserDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void createUser_ShouldReturnCreatedUserDTO() {
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.createUser(userDTO);

        assertNotNull(result);
        assertEquals(userDTO.getName(), result.getName());
        verify(userRepository).save(user);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUserDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowNotFound() {
        // ARRANGE: Kullanıcı bulunamasın
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT: Artik null değil, ResponseStatusException bekliyoruz
        assertThrows(ResponseStatusException.class, () -> userService.getUserById(99L));
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUserDTO() {
        UserDTO updateInfo = new UserDTO();
        updateInfo.setName("Mehmet");
        updateInfo.setSurname("Yılmaz");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(updateInfo);

        UserDTO result = userService.updateUser(1L, updateInfo);

        assertNotNull(result);
        assertEquals("Mehmet", result.getName());
        verify(userMapper).updateUserFromDTO(updateInfo, user);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldThrowNotFound() {
        // ARRANGE
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT: Güncelleme sırasında kullanıcı yoksa hata fırlatmalı
        assertThrows(ResponseStatusException.class, () -> userService.updateUser(99L, new UserDTO()));

        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDelete() {
        // ARRANGE: Silmeden önce varlık kontrolünü mockluyoruz
        when(userRepository.existsById(1L)).thenReturn(true);

        // ACT
        userService.deleteUser(1L);

        // ASSERT
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowNotFound() {
        // ARRANGE
        when(userRepository.existsById(99L)).thenReturn(false);

        // ACT & ASSERT
        assertThrows(ResponseStatusException.class, () -> userService.deleteUser(99L));

        verify(userRepository, never()).deleteById(anyLong());
    }
}