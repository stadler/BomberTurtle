package com.github.stadler.bomberturtle;

import com.badlogic.gdx.math.Rectangle;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class LevelEditor {

    private static final int BLOCK_SIZE = 50;
    private static final int LEVEL_HEIGHT = 12;
    private static final int LEVEL_WIDTH = 16;
    private float levelWidth;
    private float levelHeight;

    public Level loadLevel(String levelPath, float width, float height) {
        this.levelWidth = width;
        this.levelHeight = height;
        List<String> levelLines = null;
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
                switch (currentChar) {
                    case 'x' -> level.walls.add(createEntity(BLOCK_SIZE, BLOCK_SIZE, startX, startY));
                    case 's' -> level.schilki = createEntity(BLOCK_SIZE-1, BLOCK_SIZE-1, startX, startY);
                    case 'b' -> level.bombe = createEntity(BLOCK_SIZE-1, BLOCK_SIZE-1, startX, startY);
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
                if (currentChar != '.'
                        && currentChar != 'x'
                        && currentChar != 's'
                        && currentChar != 'b') {
                    throw new IllegalStateException("Invalid character in level: " + levelFile + " at row: " + rowNr + 1 + " col: " + colNr + 1);
                }
            }
        }
    }

    private Rectangle createEntity(int width, int height, int startX, int startY) {
        Rectangle entity = new Rectangle();
        entity.width = width;
        entity.height = height;
        entity.x = (startX) >= 0 ? startX : levelWidth + startX - entity.width;
        entity.y = (startY) >= 0 ? startY : levelHeight + startY - entity.height;
        return entity;
    }
}
