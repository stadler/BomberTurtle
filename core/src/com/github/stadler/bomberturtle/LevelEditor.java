package com.github.stadler.bomberturtle;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static com.github.stadler.bomberturtle.EntityType.getEntityTypeForChar;

public class LevelEditor {

    public static final int BLOCK_SIZE = 50;
    private static final int LEVEL_HEIGHT = 12;
    private static final int LEVEL_WIDTH = 16;
    private final Camera camera;
    private final Texture wallTexture;
    private final Texture playerTexture;
    private final Texture enemyTexture;

    LevelEditor(Camera camera, Texture wallTexture, Texture playerTexture, Texture enemyTexture) {
        this.camera = camera;
        this.wallTexture = wallTexture;
        this.playerTexture = playerTexture;
        this.enemyTexture = enemyTexture;
    }

    public Level loadLevel(String levelPath) {
        List<String> levelLines;
        try {
            levelLines = Files.readAllLines(Paths.get(levelPath));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read level: " + levelPath, e);
        }
        validateRows(levelLines, levelPath);
        return createLevel(levelLines);
    }

    private Level createLevel(List<String> rows) {
        Level level = new Level();
        for (int rowNr = 1; rowNr <= LEVEL_HEIGHT; rowNr++) {
            String row = rows.get(11 - (rowNr - 1));
            for (int colNr = 1; colNr <= LEVEL_WIDTH; colNr++) {
                char currentChar = row.charAt(colNr - 1);
                int startX = (colNr - 1) * BLOCK_SIZE;
                int startY = (rowNr - 1) * BLOCK_SIZE;
                switch (getEntityTypeForChar(currentChar)) {
                    case EntityType.WALL -> level.walls.add(
                            new Entity("Wall",
                                    EntityType.WALL,
                                    wallTexture,
                                    createRectangle(BLOCK_SIZE, BLOCK_SIZE, startX, startY),
                                    new Vector2(0, 0)));
                    case EntityType.PLAYER -> level.players.add(
                            new Entity("Player" + (level.players.size() + 1),
                                    EntityType.PLAYER,
                                    playerTexture,
                                    createRectangle(BLOCK_SIZE - 1, BLOCK_SIZE - 1, startX, startY),
                                    new Vector2(0, 0)));
                    case EntityType.ENEMY -> level.enemies.add(
                            new Entity("Enemy" + (level.enemies.size() + 1),
                                    EntityType.ENEMY,
                                    enemyTexture,
                                    createRectangle(BLOCK_SIZE - 1, BLOCK_SIZE - 1, startX, startY),
                                    new Vector2(0, 0)));
                }
            }
        }
        return level;
    }

    private void validateRows(List<String> rows, String levelFile) {
        if (rows.size() != LEVEL_HEIGHT) {
            throw new IllegalStateException("Invalid rows in level: " + levelFile + ". Expected " + LEVEL_HEIGHT + " got: " + rows.size());
        }
        for (int rowNr = 0; rowNr < LEVEL_HEIGHT; rowNr++) {
            String row = rows.get(rowNr);
            if (row.length() != LEVEL_WIDTH) {
                throw new IllegalStateException("Invalid column length in level: " + levelFile + " at line " + rowNr + 1 + ". Expected " + LEVEL_WIDTH + " got: " + row.length());
            }
            for (int colNr = 0; colNr < LEVEL_WIDTH; colNr++) {
                char currentChar = row.charAt(colNr);
                if (Arrays.stream(EntityType.values())
                        .noneMatch(validEntityType -> validEntityType.entityCharacter == currentChar)) {
                    throw new IllegalStateException("Invalid character in level: " + levelFile + " at row: " + rowNr + 1 + " col: " + colNr + 1);
                }
            }
        }
    }

    private Rectangle createRectangle(int width, int height, int startX, int startY) {
        Rectangle rectangle = new Rectangle();
        rectangle.width = width;
        rectangle.height = height;
        rectangle.x = (startX) >= 0 ? startX : camera.viewportWidth + startX - rectangle.width;
        rectangle.y = (startY) >= 0 ? startY : camera.viewportHeight + startY - rectangle.height;
        return rectangle;
    }

}
