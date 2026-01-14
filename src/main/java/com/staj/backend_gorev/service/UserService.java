package com.staj.backend_gorev.service;

import com.staj.backend_gorev.dto.UserDTO;
import com.staj.backend_gorev.entity.User;
import com.staj.backend_gorev.mapper.UserMapper;
import com.staj.backend_gorev.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * 1. LİSTELE (Cacheable)
     * "allUsers" cache'ini kullanır. Veri eklendiğinde/silindiğinde temizlenir.
     */
    @Cacheable(value = "allUsers")
    public List<UserDTO> getAllUsers() {
        System.out.println("--> Veritabanından Tüm Kullanıcılar Çekiliyor...");
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 2. EKLE (CacheEvict)
     * Yeni kullanıcı eklendiğinde genel listeyi (allUsers) temizler.
     */
    @CacheEvict(value = "allUsers", allEntries = true)
    public UserDTO createUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    /**
     * 3. BUL (Cacheable)
     * unless = "#result == null" -> Eğer sonuç null ise cache'e yazma.
     */
    @Cacheable(value = "user", key = "#id", unless = "#result == null")
    public UserDTO getUserById(Long id) {
        System.out.println("--> Veritabanından ID ile Kullanıcı Çekiliyor: " + id);

        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElse(null);
    }

    /**
     * 4. GÜNCELLE (Caching/CacheEvict)
     * Hem tekil cache'i (user::id) hem de genel listeyi (allUsers) geçersiz kılar.
     */
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "allUsers", allEntries = true)
    })
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User mevcutUser = userRepository.findById(id).orElse(null);

        if (mevcutUser != null) {
            userMapper.updateUserFromDTO(userDTO, mevcutUser);
            User savedUser = userRepository.save(mevcutUser);
            return userMapper.toDTO(savedUser);
        }
        return null;
    }

    /**
     * 5. SİL (Caching/CacheEvict)
     * Veri silindiğinde ilgili tüm cache kayıtlarını temizler.
     */
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "allUsers", allEntries = true)
    })
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}