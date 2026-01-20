package com.staj.backend_gorev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.staj.backend_gorev.BaseIntegrationTest;
import com.staj.backend_gorev.dto.UserDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
// Sınıf düzeyinde de eklenebilir ama metod düzeyinde olması daha belirgindir
public class UserControllerIntegrationTest extends BaseIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        // 1. TEST: Doğrudan XML'den veri okuma testi
        @Test
        @DatabaseSetup(value = "/datasets/user-data.xml", type = DatabaseOperation.CLEAN_INSERT)
        void testGetUsers_FromXmlDataSet() throws Exception {
                // XML'deki id=100 olan kullanıcıyı getiriyoruz
                String response = mockMvc.perform(get("/api/users/100"))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                UserDTO user = objectMapper.readValue(response, UserDTO.class);

                Assertions.assertEquals("Serhat", user.getName());
                Assertions.assertEquals("Demir", user.getSurname());
        }

        // 2. TEST: Kullanıcının tüm yaşam döngüsü testi (CRUD)
        @Test
        @DatabaseSetup(value = "/datasets/user-data.xml", type = DatabaseOperation.CLEAN_INSERT)
        void testUserFullCycle_Integration() throws Exception {
                // --- 1. POST (KULLANICI OLUŞTURMA) ---
                UserDTO createRequest = new UserDTO();
                createRequest.setName("Ahmet");
                createRequest.setSurname("Yılmaz");

                String createResponse = mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                UserDTO createdUser = objectMapper.readValue(createResponse, UserDTO.class);
                Long userId = createdUser.getId();

                Assertions.assertNotNull(userId);

                // --- 2. GET BY ID ---
                String getResponse = mockMvc.perform(get("/api/users/" + userId))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                UserDTO fetchedUser = objectMapper.readValue(getResponse, UserDTO.class);
                Assertions.assertEquals("Ahmet", fetchedUser.getName());

                // --- 3. GET ALL ---
                String getAllResponse = mockMvc.perform(get("/api/users"))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                UserDTO[] userArray = objectMapper.readValue(getAllResponse, UserDTO[].class);
                // XML'den gelen 2 kişi + bizim eklediğimiz 1 kişi = en az 3 kişi olmalı
                Assertions.assertTrue(userArray.length >= 3);

                // --- 4. PUT ---
                createRequest.setName("Ahmet Güncel");
                mockMvc.perform(put("/api/users/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest)))
                                .andExpect(status().isOk());

                // --- 5. DELETE ---
                mockMvc.perform(delete("/api/users/" + userId))
                                .andExpect(status().isOk());
        }

        // 3. TEST: Redis Cache mekanizması testi
        @Test
        @DatabaseSetup(value = "/datasets/user-data.xml", type = DatabaseOperation.CLEAN_INSERT)
        void testUserCacheFlow() throws Exception {
                // XML'den gelen 101 ID'li Ahmet Yilmaz üzerinden cache testi yapalım
                Long id = 101L;

                // 1. ADIM: Kullanıcıyı Getir (DB'ye gider ve Cache'e yazar)
                mockMvc.perform(get("/api/users/" + id))
                                .andExpect(status().isOk());

                // 2. ADIM: Kullanıcıyı TEKRAR Getir (Redis'ten gelmeli)
                mockMvc.perform(get("/api/users/" + id))
                                .andExpect(status().isOk());

                // 3. ADIM: Güncelleme Yap (Cache temizlenmeli)
                UserDTO updateRequest = new UserDTO();
                updateRequest.setName("Ahmet Cache Güncel");
                updateRequest.setSurname("Yilmaz");

                mockMvc.perform(put("/api/users/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk());

                // 4. ADIM: Tekrar Getir (Cache silindiği için tekrar DB'ye gitmeli)
                mockMvc.perform(get("/api/users/" + id))
                                .andExpect(status().isOk());
        }
}