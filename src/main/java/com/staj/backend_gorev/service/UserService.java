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

    // 1. LİSTELE (Cacheable)
    // Bu metod çalıştığında sonucu "allUsers" adıyla Redis'e kaydeder.
    // İkinci kez çağrıldığında metoda girmez, direkt Redis'ten verir.
    @Cacheable(value = "allUsers")
    public List<UserDTO> getAllUsers() {
        // Konsola yazdıralım ki Redis'ten mi DB'den mi geldiğini anlayalım
        System.out.println("--> Veritabanından Kullanıcılar Çekiliyor...");

        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    // 2. EKLE (CacheEvict)
    // Yeni veri eklenince "allUsers" listesi artık bayatladı. Onu siliyoruz.
    @CacheEvict(value = "allUsers", allEntries = true)
    public UserDTO createUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    // 3. BUL (Cacheable)
    // Her bir ID için ayrı kayıt tutar. Örn: user::1, user::2 gibi.
    @Cacheable(value = "user", key = "#id")
    public UserDTO getUserById(Long id) {
        System.out.println("--> Veritabanından ID ile Kullanıcı Çekiliyor: " + id);

        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElse(null);
    }

    // 4. GÜNCELLE (CacheEvict)
    // Güncelleme olunca hem o kişinin özel cache'ini (user::1)
    // hem de genel listeyi (allUsers) silmemiz lazım ki güncel veri görünsün.
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"), // Tekil cache'i sil
            @CacheEvict(value = "allUsers", allEntries = true) // Listeyi sil
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

    // 5. SİL (CacheEvict)
    // Silme olunca da hem o kişiyi hem de listeyi temizliyoruz.
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "allUsers", allEntries = true)
    })
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}