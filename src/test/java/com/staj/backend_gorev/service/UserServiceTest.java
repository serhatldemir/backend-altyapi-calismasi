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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // JUnit 5 ile Mockito'yu entegre eder
class UserServiceTest {

    @Mock
    private UserRepository userRepository; // Depoyu taklit et (Mock)

    @Mock
    private UserMapper userMapper; // Mapper'ı taklit et (Mock)

    @InjectMocks
    private UserService userService; // yukarıda olusturulan mockları test edilecek sınıfa yani serviceye enjekte et böylece servis veritabanı ve mapperda bizim taklit ettiklerimizi kullansin.

    private User user;
    private UserDTO userDTO;
    //testten once calisir,temiz veri hazirlar
    @BeforeEach
    void setUp() {
        // Her testten önce çalışır, temiz veri hazırlar
        user = new User(1L, "Ali", "Veli");
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Ali");
        userDTO.setSurname("Veli");
    }
    //listelemeyi test ediyor (crud-read)
    @Test
    void getAllUsers_ShouldReturnUserDTOList() {
        // 1. Senaryo: Depoda veriler var
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // 2. İşlem: Servisi çağır
        List<UserDTO> result = userService.getAllUsers();

        // 3. Kontrol (Assert): Sonuç doğru mu?
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ali", result.get(0).getName());
        
        // Mock'lar çağrıldı mı diye emin olalım süreç doğru islendimi ona bakıyor arka planda 
        verify(userRepository).findAll();
        verify(userMapper).toDTO(user);
    }
    //ekleme testi (crud-creat)
    @Test
    void createUser_ShouldReturnCreatedUserDTO() {
        // Senaryo
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // İşlem
        UserDTO result = userService.createUser(userDTO);

        // Kontrol
        assertNotNull(result);
        assertEquals(userDTO.getName(), result.getName());
        verify(userRepository).save(user);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUserDTO() {
        // Senaryo: Kullanıcı bulundu
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        // İşlem
        UserDTO result = userService.getUserById(1L);

        // Kontrol
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnNull() {
        // Senaryo: Kullanıcı yok
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // İşlem
        UserDTO result = userService.getUserById(99L);

        // Kontrol
        assertNull(result);
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUserDTO() {
        // Senaryo: Güncelleme yapılacak
        UserDTO updateInfo = new UserDTO();
        updateInfo.setName("Mehmet");
        updateInfo.setSurname("Yılmaz");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(updateInfo);

        // MapStruct void metodu olduğu için when(...) ile değil, sadece çağrıldığını doğrulayacağız.
        
        // İşlem
        UserDTO result = userService.updateUser(1L, updateInfo);

        // Kontrol
        assertNotNull(result);
        assertEquals("Mehmet", result.getName());
        
        // Kritik Kontrol: MapStruct'ın update metodu çalıştı mı?
        verify(userMapper).updateUserFromDTO(updateInfo, user);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldReturnNull() {
        // Senaryo: Kullanıcı yoksa güncelleme yapma
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserDTO updateInfo = new UserDTO();
        
        // İşlem
        UserDTO result = userService.updateUser(99L, updateInfo);

        // Kontrol
        assertNull(result);
        // Save metodunun ASLA çağrılmadığını doğrula
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_ShouldCallDeleteById() {
        // İşlem
        userService.deleteUser(1L);

        // Kontrol: Sadece deleteById metodunun çağrıldığını doğrulamak yeterli
        verify(userRepository, times(1)).deleteById(1L);
    }
}