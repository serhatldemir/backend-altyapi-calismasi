package com.staj.backend_gorev.service;

import com.staj.backend_gorev.dto.UserDTO;
import com.staj.backend_gorev.entity.User;
import com.staj.backend_gorev.mapper.UserMapper;
import com.staj.backend_gorev.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    //Constructor Injection ile mapper çagirdim.
    
    // 1. LİSTELE
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    // 2. EKLE
    public UserDTO createUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    // 3. BUL
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElse(null); 
    }

    // 4. GÜNCELLE (Senior Refactoring)
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User mevcutUser = userRepository.findById(id).orElse(null);

        if (mevcutUser != null) {
            // Manuel set işlemleri kalktı! MapStruct hedefi güncelliyor.
            userMapper.updateUserFromDTO(userDTO, mevcutUser);
            //yukarida diyoruzki usermapperda olan userDtoları mevcutUser olan depodaki değerlerle güncelle
            
            User savedUser = userRepository.save(mevcutUser);
            return userMapper.toDTO(savedUser);
        }
        return null;
    }

    // 5. SİL
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}