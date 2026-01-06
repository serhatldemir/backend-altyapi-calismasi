package com.staj.backend_gorev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staj.backend_gorev.dto.UserDTO;
import com.staj.backend_gorev.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean; 
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {
    //springboota bana hazırlamıs oldugun mockmvcyi bul getir hazırla demek
    @Autowired
    private MockMvc mockMvc; 

    //service katmaninı taklit eder
    @MockitoBean
    private UserService userService;
   //userDTO yu aldik,jsona çevirdik bu islemi yapar alttaki
    @Autowired
    private ObjectMapper objectMapper; 

    @Test
    public void testCreateUser_Integration() throws Exception {
        // Gönderecegimiz veri
        UserDTO requestDto = new UserDTO();
        requestDto.setName("Mehmet");
        requestDto.setSurname("Demir");
        // Beklediğimiz cevap 
        UserDTO responseDto = new UserDTO();
        responseDto.setId(10L);
        responseDto.setName("Mehmet");
        responseDto.setSurname("Demir");
        //Biri kayıt isterse ,bu resp.DTO yu don veritabanina gitme 
        when(userService.createUser(any(UserDTO.class))).thenReturn(responseDto);

        // İstek Atma ve Kontrol
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))) 
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.name").value("Mehmet")) 
                .andExpect(jsonPath("$.id").value(10)); 
    }
}