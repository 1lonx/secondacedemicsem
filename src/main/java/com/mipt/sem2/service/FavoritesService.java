package com.mipt.sem2.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FavoritesService {
  private static final String SESSION_FAVORITES_KEY = "favoriteTaskIds";

  @SuppressWarnings("unchecked")
  public Set<Long> getFavorites(HttpSession session) {
    Set<Long> favorites = (Set<Long>) session.getAttribute(SESSION_FAVORITES_KEY);
    if (favorites == null) {
      favorites = new HashSet<>();
      session.setAttribute(SESSION_FAVORITES_KEY, favorites);
    }
    return favorites;
  }

  public void addFavorite(HttpSession session, Long taskId) {
    Set<Long> favorites = getFavorites(session);
    favorites.add(taskId);
  }

  public void removeFavorite(HttpSession session, Long taskId) {
    Set<Long> favorites = getFavorites(session);
    favorites.remove(taskId);
  }

  public boolean isFavorite(HttpSession session, Long taskId) {
    return getFavorites(session).contains(taskId);
  }
}