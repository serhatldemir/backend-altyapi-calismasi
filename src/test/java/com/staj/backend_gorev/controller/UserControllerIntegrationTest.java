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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Her metoddan sonra context'i temizler
public class UserControllerIntegrationTest extends BaseIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        // --- DBUNIT TESTİ ---
        @Test
        @DatabaseSetup("/datasets/user-data.xml")
        void testGetUsers_FromXmlDataSet() throws Exception {
                // XML'de id=100 yaptığımız için burada 100 numaralı ID'yi çağırıyoruz
                String response = mockMvc.perform(get("/api/users/100"))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                UserDTO user = objectMapper.readValue(response, UserDTO.class);

                // Doğrulama: Veriler XML'den mi gelmiş?
                Assertions.assertEquals("Serhat", user.getName());
                Assertions.assertEquals("Demir", user.getSurname());
        }

        // --- MEVCUT ENTEGRASYON TESTLERİ ---
        @Test
        // CLEAN_INSERT: Önce tabloyu boşaltır, sonra XML'deki verileri ekler.
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

                // --- 5. DELETE (SİLME VE DOĞRULAMA) ---
                mockMvc.perform(delete("/api/users/" + userId))
                                .andExpect(status().isOk());

                // Silindiğini doğrula (404 Beklentisi)
                mockMvc.perform(get("/api/users/" + userId))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testUserCacheFlow() throws Exception {
                // 1. ADIM: Kullanıcı Oluştur
                UserDTO user = new UserDTO();
                user.setName("CacheTest");
                user.setSurname("User");

                String response = mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                Long id = objectMapper.readValue(response, UserDTO.class).getId();

                // 2. ADIM: Kullanıcıyı Getir (DB'ye gider)
                mockMvc.perform(get("/api/users/" + id))
                                .andExpect(status().isOk());

                // 3. ADIM: Kullanıcıyı TEKRAR Getir (Redis'ten gelmeli)
                mockMvc.perform(get("/api/users/" + id))
                                .andExpect(status().isOk());

                // 4. ADIM: Güncelleme Yap (Cache temizlenmeli)
                user.setName("UpdatedCacheName");
                mockMvc.perform(put("/api/users/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                                .andExpect(status().isOk());

                // 5. ADIM: Tekrar Getir (Tekrar DB'ye gitmeli)
                mockMvc.perform(get("/api/users/" + id))
                                .andExpect(status().isOk());
        }
}