package com.staj.backend_gorev.controller;

import com.staj.backend_gorev.dto.UserDTO;
import com.staj.backend_gorev.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Buranın bir "Kontrol Merkezi" olduğunu belirtir.
@RequestMapping("/api/users") // Tarayıcıdaki adresimiz: localhost:8080/api/users
public class UserController {

    private final UserService userService;

    // Servis katmanını buraya bağlıyoruz
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. GET (Hepsini Getir) -> localhost:8080/api/users
    @GetMapping
    public List<UserDTO> getAllUsers() {
        // Artık geriye User listesi değil, UserDTO listesi dönüyoruz
        return userService.getAllUsers();
    }

    // 2. POST (Yeni Ekle) -> localhost:8080/api/users
    @PostMapping
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        // @RequestBody: Postman'den gelen JSON verisini artık DTO olarak alıyoruz
        return userService.createUser(userDTO);
    }

    // 3. GET (Tek Kişi Getir) -> localhost:8080/api/users/1
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        // Tek bir kullanıcı dönerken de DTO dönüyoruz
        return userService.getUserById(id);
    }

    // 4. PUT (Güncelle) -> localhost:8080/api/users/1
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        // Güncelleme için gelen veriyi de DTO olarak alıyoruz
        return userService.updateUser(id, userDTO);
    }

    // 5. DELETE (Sil) -> localhost:8080/api/users/1
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        // Silme işleminde geriye veri dönmediği için burası aynen kalır
        userService.deleteUser(id);
    }
}