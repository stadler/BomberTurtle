package com.github.stadler.bomberturtle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameScreen implements Screen {

    private final BomberTurtleGame game;
    private final Texture wallImg;
    private final Texture bombeImg;
    private final Texture schilkiImg;
    private final OrthographicCamera camera;
    private final Level level;


    public GameScreen(final BomberTurtleGame game) {
        this.game = game;

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // Textures
        wallImg = new Texture("wall.png");
        schilkiImg = new Texture("Schilki.png");
        bombeImg = new Texture("Bombe.png");

        // Load level
        level = new LevelEditor().loadLevel("levels/level1.bt", camera.viewportWidth, camera.viewportHeight);
    }

    @Override
    public void render(float v) {
        // clear the screen with a dark blue color. The arguments to clear are the red, green
        // blue and alpha component in the range [0,1] of the color to be used to clear the screen.
        ScreenUtils.clear(1, 1, 1, 1);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(bombeImg, level.bombe.x, level.bombe.y, level.bombe.width, level.bombe.height);
        game.batch.draw(schilkiImg, level.schilki.x, level.schilki.y, level.schilki.width, level.schilki.height);
        level.walls.forEach(wall -> {
            game.batch.draw(wallImg, wall.x, wall.y, wall.width, wall.height);
        });
        game.batch.end();

        handleInputs();
    }

    private void handleInputs() {
        // process user input
        handleDirectionsForEntity(level.bombe, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.DOWN, Input.Keys.UP);
        handleDirectionsForEntity(level.schilki, Input.Keys.A, Input.Keys.D, Input.Keys.S, Input.Keys.W);
    }

    private void handleDirectionsForEntity(Rectangle entity, int left, int right, int down, int up) {
        float moveAmount = 200 * Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(left)) {
            moveIfNotInWall(entity, -moveAmount, 0);
        }
        if (Gdx.input.isKeyPressed(right)) {
            moveIfNotInWall(entity, moveAmount, 0);
        }
        if (Gdx.input.isKeyPressed(down)) {
            moveIfNotInWall(entity, 0, -moveAmount);
        }
        if (Gdx.input.isKeyPressed(up)) {
            moveIfNotInWall(entity, 0, moveAmount);
        }

        // make sure the bucket stays within the screen bounds
        if (entity.x < 0) entity.x = 0;
        if (entity.x > camera.viewportWidth - entity.width) entity.x = camera.viewportWidth - entity.width;
        if (entity.y < 0) entity.y = 0;
        if (entity.y > camera.viewportHeight - entity.height) entity.y = camera.viewportHeight - entity.height;
    }

    private void moveIfNotInWall(Rectangle entity, float moveX, float moveY) {
        float newX = entity.x + moveX;
        float newY = entity.y + moveY;
        Optional<Rectangle> anyObstacle = level.walls.stream()
                .filter(wall -> (
                        newX + entity.width > wall.x
                                && newX < wall.x + wall.width
                                && newY + entity.height > wall.y
                                && newY < wall.y + wall.height))
                .findAny();
        if (anyObstacle.isEmpty()) {
            entity.x = newX;
            entity.y = newY;
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        bombeImg.dispose();
        schilkiImg.dispose();
        wallImg.dispose();
    }
}
