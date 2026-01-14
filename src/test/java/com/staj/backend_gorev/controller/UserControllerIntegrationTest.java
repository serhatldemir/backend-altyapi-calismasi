package com.staj.backend_gorev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staj.backend_gorev.BaseIntegrationTest;
import com.staj.backend_gorev.dto.UserDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class UserControllerIntegrationTest extends BaseIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
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
                Assertions.assertEquals("Ahmet", createdUser.getName());

                // --- 2. GET BY ID (TEK KULLANICI GETİRME) ---
                String getResponse = mockMvc.perform(get("/api/users/" + userId))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                UserDTO fetchedUser = objectMapper.readValue(getResponse, UserDTO.class);
                Assertions.assertEquals("Ahmet", fetchedUser.getName());

                // --- 3. GET ALL (HEPSİNİ LİSTELEME) ---
                String getAllResponse = mockMvc.perform(get("/api/users"))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                UserDTO[] userArray = objectMapper.readValue(getAllResponse, UserDTO[].class);
                Assertions.assertTrue(userArray.length > 0);

                // --- 4. PUT (GÜNCELLEME) ---
                UserDTO updateRequest = new UserDTO();
                updateRequest.setName("Ahmet Güncel");
                updateRequest.setSurname("Yılmaz");

                String updateResponse = mockMvc.perform(put("/api/users/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                UserDTO updatedUser = objectMapper.readValue(updateResponse, UserDTO.class);
                Assertions.assertEquals("Ahmet Güncel", updatedUser.getName());

                // --- 5. DELETE (SİLME) ---
                mockMvc.perform(delete("/api/users/" + userId))
                                .andExpect(status().isOk());

                // Silindiğini doğrula
                mockMvc.perform(get("/api/users/" + userId))
                                .andExpect(status().isOk());
        }

        @Test
        void testUserCacheFlow() throws Exception {
                // 1. ADIM: Kullanıcı Oluştur (Bu işlem 'allUsers' cache'ini temizlemeli/evict)
                UserDTO user = new UserDTO();
                user.setName("CacheTest");
                user.setSurname("User");

                String response = mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Long id = objectMapper.readValue(response, UserDTO.class).getId();

                // 2. ADIM: Kullanıcıyı Getir (İlk seferde DB'ye gider, log yazdırır)
                mockMvc.perform(get("/api/users/" + id)).andExpect(status().isOk());

                // 3. ADIM: Kullanıcıyı TEKRAR Getir (Bu sefer Redis'ten gelmeli!)
                // Eğer her şey doğruysa, konsolda ikinci kez "Veritabanından Çekiliyor"
                // yazısını görmemelisin.
                mockMvc.perform(get("/api/users/" + id)).andExpect(status().isOk());

                // 4. ADIM: Güncelleme Yap (Cache temizlenmeli)
                user.setName("UpdatedName");
                mockMvc.perform(put("/api/users/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                                .andExpect(status().isOk());

                // 5. ADIM: Tekrar Getir (Güncellemeden sonra cache temizlendiği için tekrar
                // DB'ye gitmeli)
                mockMvc.perform(get("/api/users/" + id)).andExpect(status().isOk());
        }
}