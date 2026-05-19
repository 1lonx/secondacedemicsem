package com.mipt.sem2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class PreferencesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void getViewPreference_WithNoCookie_ShouldReturnDefault() throws Exception {
    mockMvc.perform(get("/api/preferences/view"))
        .andExpect(status().isOk())
        .andExpect(content().string("compact"))
        .andExpect(header().exists("X-API-Version"));
  }

  @Test
  void getViewPreference_WithCookie_ShouldReturnStoredValue() throws Exception {
    mockMvc.perform(get("/api/preferences/view")
            .cookie(new jakarta.servlet.http.Cookie("viewPreference", "detailed")))
        .andExpect(status().isOk())
        .andExpect(content().string("detailed"));
  }

  @Test
  void setViewPreference_ShouldSetCookie() throws Exception {
    mockMvc.perform(post("/api/preferences/view").param("mode", "detailed"))
        .andExpect(status().isOk())
        .andExpect(cookie().exists("viewPreference"))
        .andExpect(cookie().value("viewPreference", "detailed"))
        .andExpect(cookie().maxAge("viewPreference", 60 * 60 * 24 * 365))
        .andExpect(cookie().path("viewPreference", "/"))
        .andExpect(header().exists("X-API-Version"));
  }

  @Test
  void setViewPreference_WithCompactMode_ShouldSetCompactCookie() throws Exception {
    mockMvc.perform(post("/api/preferences/view").param("mode", "compact"))
        .andExpect(status().isOk())
        .andExpect(cookie().value("viewPreference", "compact"));
  }

  @Test
  void setViewPreference_WithDifferentModes_ShouldUpdateCookie() throws Exception {
    mockMvc.perform(post("/api/preferences/view").param("mode", "compact"))
        .andExpect(status().isOk());

    mockMvc.perform(post("/api/preferences/view").param("mode", "detailed"))
        .andExpect(status().isOk())
        .andExpect(cookie().value("viewPreference", "detailed"));
  }
}
