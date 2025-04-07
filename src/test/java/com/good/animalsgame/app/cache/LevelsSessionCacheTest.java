package com.good.animalsgame.app.cache;

import com.good.animalsgame.app.repository.LevelRepository;
import com.good.animalsgame.exception.NoSuchRoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LevelsSessionCacheTest {

    private LevelsSessionCache levelsSessionCache;

    private Map<String, LevelRepository<?, ?>> levelRepositories = new HashMap<>();

    private final Map<Integer, List<Long>> roundsCache = new HashMap<>();

    @Mock
    private LevelRepository<?, ?> firstRoundRepository;

    @Mock
    private LevelRepository<?, ?> secondRoundRepository;

    @BeforeEach
    void setUp() {
        levelRepositories.put("1", firstRoundRepository);
        levelRepositories.put("2", secondRoundRepository);

        levelsSessionCache = new LevelsSessionCache(levelRepositories);
    }

    @Test
    void testGetRoundSize() throws NoSuchRoundException {
        when(firstRoundRepository.findLevelIds()).thenReturn(List.of(1L, 2L, 3L));
        levelsSessionCache.init();

        assertEquals(3, levelsSessionCache.getRoundSize(1));
    }

    @Test
    void testGetRoundSizeError() {
        when(firstRoundRepository.findLevelIds()).thenReturn(List.of(1L, 2L, 3L));
        levelsSessionCache.init();

        Exception e = assertThrows(NoSuchRoundException.class, () -> levelsSessionCache.getRoundSize(9));
        assertEquals("Не найден раунд 9!", e.getMessage());
    }

    @Test
    void testGetLevelId() throws NoSuchRoundException {
        when(firstRoundRepository.findLevelIds()).thenReturn(List.of(1L, 2L, 3L));
        levelsSessionCache.init();

        assertEquals(2L, levelsSessionCache.getLevelId(1, 1));
    }

    @Test
    void testGetLevelIdError() {
        when(firstRoundRepository.findLevelIds()).thenReturn(List.of(10L, 20L, 30L));
        levelsSessionCache.init();

        Exception e = assertThrows(NoSuchRoundException.class, () -> levelsSessionCache.getLevelId(9, 0));
        assertEquals("Не найден раунд 9!", e.getMessage());
    }

    @Test
    void testRemoveLevel() throws NoSuchRoundException {
        when(firstRoundRepository.findLevelIds()).thenReturn(new ArrayList<>(List.of(1L, 2L, 3L)));
        levelsSessionCache.init();

        levelsSessionCache.removeLevel(1, 1);
        assertEquals(2, levelsSessionCache.getRoundSize(1));
    }

    @Test
    void testRemoveLevelError() {
        when(firstRoundRepository.findLevelIds()).thenReturn(new ArrayList<>(List.of(1L, 2L, 3L)));
        levelsSessionCache.init();

        Exception e = assertThrows(NoSuchRoundException.class, () -> levelsSessionCache.removeLevel(9, 0));
        assertEquals("Не найден раунд 9!", e.getMessage());
    }
}