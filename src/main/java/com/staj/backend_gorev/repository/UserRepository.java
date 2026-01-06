package com.staj.backend_gorev.repository;

import com.staj.backend_gorev.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // ŞU AN İÇİ BOŞ JPAYA BIRAKTIM :)
    
   
    
}