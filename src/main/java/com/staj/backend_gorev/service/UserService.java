package com.staj.backend_gorev.service;

import com.staj.backend_gorev.dto.UserDTO;
import com.staj.backend_gorev.entity.User;
import com.staj.backend_gorev.mapper.UserMapper;
import com.staj.backend_gorev.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Cacheable(value = "allUsers")
    public List<UserDTO> getAllUsers() {
        System.out.println("--> Veritabanından Tüm Kullanıcılar Çekiliyor...");
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "allUsers", allEntries = true)
    public UserDTO createUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Cacheable(value = "user", key = "#id", unless = "#result == null")
    public UserDTO getUserById(Long id) {
        System.out.println("--> Veritabanından ID ile Kullanıcı Çekiliyor: " + id);
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı: " + id));
    }

    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "allUsers", allEntries = true)
    })
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User mevcutUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Güncellenecek kullanıcı bulunamadı: " + id));

        userMapper.updateUserFromDTO(userDTO, mevcutUser);
        User savedUser = userRepository.save(mevcutUser);
        return userMapper.toDTO(savedUser);
    }

    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "allUsers", allEntries = true)
    })
    public void deleteUser(Long id) {
        // Silmeden önce var olup olmadığını kontrol etmek testi garantiye alır
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Silinecek kullanıcı bulunamadı: " + id);
        }
        userRepository.deleteById(id);
    }
}