package com.github.stadler.bomberturtle;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.stadler.bomberturtle.entities.Entity;
import com.github.stadler.bomberturtle.entities.EntityType;
import com.github.stadler.bomberturtle.entities.MovableEntity;
import com.github.stadler.bomberturtle.entities.PlayerEntity;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static com.github.stadler.bomberturtle.Constants.*;
import static com.github.stadler.bomberturtle.entities.EntityType.getEntityTypeForChar;

public class LevelEditor {

    private final Camera camera;
    private final Texture wallTexture;
    private final List<Texture> playerTextures;
    private final Texture enemyTexture;
    private final Texture darthVaderTexture;
    private final Texture poopTexture;

    LevelEditor(Camera camera, Texture wallTexture, List<Texture> playerTextures, Texture enemyTexture, Texture darthVaderTexture, Texture poopTexture) {
        this.camera = camera;
        this.wallTexture = wallTexture;
        this.playerTextures = playerTextures;
        this.enemyTexture = enemyTexture;
        this.darthVaderTexture = darthVaderTexture;
        this.poopTexture = poopTexture;
    }

    public Level loadLevel(String levelPath, int selectedPlayers) {
        List<String> levelLines;
        try {
            levelLines = Files.readAllLines(Paths.get(levelPath));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read level: " + levelPath, e);
        }
        validateRows(levelLines, levelPath);
        return createLevel(levelLines, selectedPlayers);
    }

    private Level createLevel(List<String> rows, int selectedPlayers) {
        EntityType[][] levelEntityTypes = new EntityType[LEVEL_HEIGHT][LEVEL_WIDTH];
        for (int rowNr = 0; rowNr < LEVEL_HEIGHT; rowNr++) {
            String row = rows.get(11 - (rowNr));
            for (int colNr = 0; colNr < LEVEL_WIDTH; colNr++) {
                char currentChar = row.charAt(colNr);
                levelEntityTypes[rowNr][colNr] = getEntityTypeForChar(currentChar);
            }
        }

        int currentPlayers = 0;
        Level level = new Level();
        for (int rowNr = 0; rowNr < LEVEL_HEIGHT; rowNr++) {
            for (int colNr = 0; colNr < LEVEL_WIDTH; colNr++) {
                EntityType entityType = levelEntityTypes[rowNr][colNr];
                switch (entityType) {
                    case EntityType.WALL -> level.walls.add(
                            new Entity("Wall",
                                    EntityType.WALL,
                                    wallTexture,
                                    createRectangle(BLOCK_SIZE, BLOCK_SIZE, getStartX(colNr), getStartY(rowNr))));
                    case EntityType.PLAYER -> {
                        if (currentPlayers < selectedPlayers) {
                            currentPlayers++;
                            level.players.add(
                                    new PlayerEntity("Player" + (level.players.size() + 1),
                                            EntityType.PLAYER,
                                            getPlayerTexture(level.players.size() + 1),
                                            createRectangle(BLOCK_SIZE - 1, BLOCK_SIZE - 1, getStartX(colNr), getStartY(rowNr)),
                                            new Vector2(0, 0),
                                            Instant.now().minus(BOMB_DELAY)));
                        }
                    }
                    case EntityType.ENEMY -> level.enemies.add(
                            new MovableEntity("Enemy" + (level.enemies.size() + 1),
                                    EntityType.ENEMY,
                                    enemyTexture,
                                    createRectangle(BLOCK_SIZE - 1, BLOCK_SIZE - 1, getStartX(colNr), getStartY(rowNr)),
                                    new Vector2(0, 0)));
                    case EntityType.DARTH_VADER -> {
                        if (isBottomLeft(levelEntityTypes, rowNr, colNr, EntityType.DARTH_VADER)) {
                            level.enemies.add(
                                    new MovableEntity("Vader" + (level.enemies.size() + 1),
                                            EntityType.DARTH_VADER,
                                            darthVaderTexture,
                                            createRectangle((2 * BLOCK_SIZE) - 1, (2 * BLOCK_SIZE) - 1, getStartX(colNr), getStartY(rowNr)),
                                            new Vector2(0, 0)));
                        }
                    }
                    case EntityType.POOP -> {
                        if (isBottomLeft(levelEntityTypes, rowNr, colNr, EntityType.POOP)) {
                            level.enemies.add(
                                    new MovableEntity("Poop" + (level.enemies.size() + 1),
                                            EntityType.POOP,
                                            poopTexture,
                                            createRectangle((3 * BLOCK_SIZE) - 1, (3 * BLOCK_SIZE) - 1, getStartX(colNr), getStartY(rowNr)),
                                            new Vector2(0, 0)));
                        }
                    }
                }
            }
        }
        return level;
    }

    private static boolean isBottomLeft(EntityType[][] levelEntityTypes, int rowNr, int colNr, EntityType entityType) {
        return (rowNr == 0 || levelEntityTypes[rowNr - 1][colNr] != entityType)
                && (colNr ==0 || levelEntityTypes[rowNr][colNr - 1] != entityType);
    }

    private static int getStartY(int rowNr) {
        return (rowNr) * BLOCK_SIZE;
    }

    private static int getStartX(int colNr) {
        return (colNr) * BLOCK_SIZE;
    }

    private Texture getPlayerTexture(int index) {
        return playerTextures.get(index % 3);
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
                        .noneMatch(validEntityType -> validEntityType.getEntityCharacter() == currentChar)) {
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
