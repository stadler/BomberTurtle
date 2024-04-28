package com.github.stadler.bomberturtle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.time.LocalDateTime;
import java.util.Optional;

public class GameScreen implements Screen {

    private final BomberTurtleGame game;
    private final Texture wallImg;
    private final Texture bombeImg;
    private final Texture schilkiImg;
    private final OrthographicCamera camera;
    private Level level;
    private int levelNr = 1;
    private LocalDateTime gameFinishedTs = null;
    private final Sound sound;


    public GameScreen(final BomberTurtleGame game) {
        this.game = game;

        sound = Gdx.audio.newSound(Gdx.files.internal("audio/Cedi Nr. 3 - kurz.ogg"));

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
        ScreenUtils.clear(0.8f, 0.8f, 0.8f, 1);

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
        if (gameFinishedTs != null) {
            game.titleFont.draw(game.batch, "Gewonnen!", 300, 300);
        }
        game.batch.end();

        handleInputs();
    }

    private void handleInputs() {
        if (gameFinishedTs != null) {
            if (gameFinishedTs.plusSeconds(1).isBefore(LocalDateTime.now())) {
                if (levelNr == 1) {
                    levelNr++;
                    gameFinishedTs = null;
                    level = new LevelEditor().loadLevel("levels/level2.bt", camera.viewportWidth, camera.viewportHeight);
                } else {
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                }
            }
            return;
        }
        // process user input
        handleDirectionsForEntity(level.bombe, Input.Keys.LEFT, Input.Keys.RIGHT, Input.Keys.DOWN, Input.Keys.UP);
        handleDirectionsForEntity(level.schilki, Input.Keys.A, Input.Keys.D, Input.Keys.S, Input.Keys.W);
    }

    private void handleDirectionsForEntity(Rectangle entity, int left, int right, int down, int up) {
        // Must be factor of block size
        float moveAmount = 5f;

        if (Gdx.input.isKeyPressed(left)) moveIfPossible(entity, new Vector2(-moveAmount, 0));
        if (Gdx.input.isKeyPressed(right)) moveIfPossible(entity, new Vector2(moveAmount, 0));
        if (Gdx.input.isKeyPressed(down)) moveIfPossible(entity, new Vector2(0, -moveAmount));
        if (Gdx.input.isKeyPressed(up)) moveIfPossible(entity, new Vector2(0, moveAmount));

        // make sure the bucket stays within the screen bounds
        if (entity.x < 0) entity.x = 0;
        if (entity.x > camera.viewportWidth - entity.width) entity.x = camera.viewportWidth - entity.width;
        if (entity.y < 0) entity.y = 0;
        if (entity.y > camera.viewportHeight - entity.height) entity.y = camera.viewportHeight - entity.height;
    }

    private void moveIfPossible(Rectangle entity, Vector2 move) {
        Optional<Rectangle> anyWall = level.walls.stream()
                .filter(wall -> doEntitiesCollide(entity, wall, move))
                .findAny();
        if (anyWall.isEmpty()) {
            entity.x += move.x;
            entity.y += move.y;
            System.out.println("New position x: " + entity.x + ", y: " + entity.y +  " after move: " + move);
        }

        Rectangle otherEntity = level.getOtherEntity(entity);
        if (doEntitiesCollide(entity, otherEntity, move)) {
            gameFinishedTs = LocalDateTime.now();
        }

    }

    private static boolean doEntitiesCollide(Rectangle entity, Rectangle otherEntity, Vector2 move) {
        float newX = entity.x + move.x;
        float newY = entity.y + move.y;
        return newX + entity.width > otherEntity.x
                && newX < otherEntity.x + otherEntity.width
                && newY + entity.height > otherEntity.y
                && newY < otherEntity.y + otherEntity.height;
    }

    @Override
    public void show() {
        long soundId = sound.play(0.5f);
        sound.setLooping(soundId, true);
        sound.setPitch(soundId, 2);
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {
        sound.pause();
    }

    @Override
    public void resume() {
        sound.resume();
    }

    @Override
    public void hide() {
        sound.pause();
    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        bombeImg.dispose();
        schilkiImg.dispose();
        wallImg.dispose();
        sound.dispose();
    }
}
