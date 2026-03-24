package com.mipt.sem2;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FavoritesServiceTest {

  @Autowired
  private FavoritesService favoritesService;

  private HttpSession session;

  @BeforeEach
  void setUp() {
    session = new MockHttpSession();
  }

  @Test
  void getFavorites_WithNoFavorites_ShouldReturnEmptySet() {
    Set<Long> favorites = favoritesService.getFavorites(session);
    assertNotNull(favorites);
    assertTrue(favorites.isEmpty());
  }

  @Test
  void addFavorite_ShouldAddTaskToFavorites() {
    favoritesService.addFavorite(session, 1L);
    Set<Long> favorites = favoritesService.getFavorites(session);
    assertTrue(favorites.contains(1L));
    assertEquals(1, favorites.size());
  }

  @Test
  void addFavorite_MultipleTasks_ShouldAddAll() {
    favoritesService.addFavorite(session, 1L);
    favoritesService.addFavorite(session, 2L);
    favoritesService.addFavorite(session, 3L);

    Set<Long> favorites = favoritesService.getFavorites(session);
    assertEquals(3, favorites.size());
    assertTrue(favorites.contains(1L));
    assertTrue(favorites.contains(2L));
    assertTrue(favorites.contains(3L));
  }

  @Test
  void addFavorite_DuplicateTask_ShouldNotDuplicate() {
    favoritesService.addFavorite(session, 1L);
    favoritesService.addFavorite(session, 1L);

    Set<Long> favorites = favoritesService.getFavorites(session);
    assertEquals(1, favorites.size());
    assertTrue(favorites.contains(1L));
  }

  @Test
  void removeFavorite_ShouldRemoveTaskFromFavorites() {
    favoritesService.addFavorite(session, 1L);
    favoritesService.addFavorite(session, 2L);

    favoritesService.removeFavorite(session, 1L);

    Set<Long> favorites = favoritesService.getFavorites(session);
    assertEquals(1, favorites.size());
    assertFalse(favorites.contains(1L));
    assertTrue(favorites.contains(2L));
  }

  @Test
  void removeFavorite_WithNonExistingTask_ShouldNotThrow() {
    favoritesService.addFavorite(session, 1L);
    favoritesService.removeFavorite(session, 999L);

    Set<Long> favorites = favoritesService.getFavorites(session);
    assertEquals(1, favorites.size());
    assertTrue(favorites.contains(1L));
  }

  @Test
  void isFavorite_ShouldReturnCorrectStatus() {
    assertFalse(favoritesService.isFavorite(session, 1L));

    favoritesService.addFavorite(session, 1L);
    assertTrue(favoritesService.isFavorite(session, 1L));
    assertFalse(favoritesService.isFavorite(session, 2L));
  }
}
