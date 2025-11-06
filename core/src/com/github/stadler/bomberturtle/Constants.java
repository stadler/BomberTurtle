package com.github.stadler.bomberturtle;

import com.badlogic.gdx.graphics.Color;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class Constants {

    public static final int VIEWPORT_WIDTH = 800;
    public static final int VIEWPORT_HEIGHT = 600;

    public static final Color START_SCREEN_COLOR = new Color(0.9f, 0.9f, 0.9f, 1);
    public static final Color BACKGROUND_COLOR = new Color(0.7f, 0.8f, 0.9f, 1);

    public static final float SOUND_VOLUME = 0.5f;
    public static final float SOUND_PITCH = 1.5f;

    public static final int START_LEVEL = 1;
    private static final int HIGHEST_LEVEL = 6;
    public static final int TOTAL_LEVELS = (HIGHEST_LEVEL - START_LEVEL) + 1;
    public static final int LEVEL_HEIGHT = 12;
    public static final int LEVEL_WIDTH = 16;
    public static final Duration GAME_TIME = Duration.of(15, ChronoUnit.SECONDS);

    public static final int BLOCK_SIZE = 50;
    public static final float MOVE_AMOUNT = BLOCK_SIZE / 20f;

    public static final int BOMB_SIZE = 20;
    public static final Duration BOMB_DELAY = Duration.ofSeconds(1);

}
