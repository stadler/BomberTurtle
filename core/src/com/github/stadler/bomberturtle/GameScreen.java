package com.github.stadler.bomberturtle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static com.badlogic.gdx.Input.Keys;
import static com.github.stadler.bomberturtle.LevelEditor.BLOCK_SIZE;

public class GameScreen implements Screen {

    public static final float SOUND_VOLUME = 0.5f;
    public static final int TOTAL_LEVELS = 3;
    private static final Duration GAME_TIME = Duration.of(10, ChronoUnit.SECONDS);
    public static final float MOVE_AMOUNT = BLOCK_SIZE / 10f;
    public static final Vector2 MOVE_LEFT = new Vector2(-MOVE_AMOUNT, 0);
    public static final Vector2 MOVE_RIGHT = new Vector2(MOVE_AMOUNT, 0);
    public static final Vector2 MOVE_DOWN = new Vector2(0, -MOVE_AMOUNT);
    public static final Vector2 MOVE_UP = new Vector2(0, MOVE_AMOUNT);

    private final BomberTurtleGame game;
    private final OrthographicCamera camera;
    private final LevelEditor levelEditor;
    private final Texture wallTexture;
    private final Texture playerTexture;
    private final Texture enemyTexture;
    private final Sound sound1;
    private final Sound sound2;
    private Sound currentSound;
    private Level level;
    private int levelNr = 1;
    private LocalTime startTime;
    private LocalTime levelFinishedTime = null;
    private boolean gameWon = false;
    private boolean gameLost = false;


    public GameScreen(final BomberTurtleGame game) {
        this.game = game;

        sound1 = Gdx.audio.newSound(Gdx.files.internal("audio/Cedi Nr. 3 - kurz.ogg"));
        sound2 = Gdx.audio.newSound(Gdx.files.internal("audio/Hit the Note.ogg"));
        currentSound = sound1;

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // Textures
        wallTexture = new Texture("wall.png");
        playerTexture = new Texture("Bombe.png");
        enemyTexture = new Texture("Schilki.png");

        // Load level
        levelEditor = new LevelEditor(camera, wallTexture, playerTexture, enemyTexture);
        level = levelEditor.loadLevel("levels/level1.bt");
        initializeNewGame();
    }

    private void initializeNewGame() {
        startTime = LocalTime.now();
        gameWon = false;
        gameLost = false;
        levelFinishedTime = null;
        level = levelEditor.loadLevel("levels/level" + levelNr + ".bt");
        switchSound();
    }

    @Override
    public void render(float deltaTime) {
        // clear the screen with a dark blue color. The arguments to clear are the red, green
        // blue and alpha component in the range [0,1] of the color to be used to clear the screen.
        ScreenUtils.clear(0.9f, 0.7f, 0.9f, 1);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        level.players.forEach(player ->
                game.batch.draw(player.texture(), player.rectangle().x, player.rectangle().y, player.rectangle().width, player.rectangle().height));
        level.enemies.forEach(enemy ->
                game.batch.draw(enemy.texture(), enemy.rectangle().x, enemy.rectangle().y, enemy.rectangle().width, enemy.rectangle().height));
        level.walls.forEach(wall ->
                game.batch.draw(wall.texture(), wall.rectangle().x, wall.rectangle().y, wall.rectangle().width, wall.rectangle().height));
        game.textFont.draw(game.batch, "Zeit: " + calculateRemainingSeconds(), 100, camera.viewportHeight);

        if (!isLevelFinished() && calculateRemainingSeconds() <= 0) {
            gameWon = true;
            levelFinishedTime = LocalTime.now();
        }
        if (isLevelFinished()) {
            game.titleFont.draw(game.batch, gameWon ? "Gewonnen!" : "Game Over", 300, 300);
            if (gameWon) {
                game.textFont.draw(game.batch, levelNr < TOTAL_LEVELS
                        ? "Ab zu Level Nr." + (levelNr + 1)
                        : "Du hast alle Levels geschafft!", 350, 200);
            }
            if (levelFinishedTime.plusSeconds(2).isBefore(LocalTime.now()) && Gdx.input.isKeyPressed(Keys.ANY_KEY)) {
                if (gameWon && levelNr < TOTAL_LEVELS) {
                    levelNr++;
                    initializeNewGame();
                } else {
                    game.setScreen(new MainMenuScreen(game));
                    dispose();
                }
            }
        }
        game.batch.end();

        if (!isLevelFinished()) {
            handleInputs();
        }
    }

    private boolean isLevelFinished() {
        return levelFinishedTime != null;
    }

    private long calculateRemainingSeconds() {
        LocalTime endTime = startTime.plus(GAME_TIME);
        Duration remainingTime = Duration.between(isLevelFinished() ? levelFinishedTime : LocalTime.now(), endTime);
        return remainingTime.toSeconds();
    }

    private void handleInputs() {
        // process user input
        KeyBinding keyBindingPlayer1 = new KeyBinding(Keys.LEFT, Keys.RIGHT, Keys.UP, Keys.DOWN);
        KeyBinding keyBindingPlayer2 = new KeyBinding(Keys.A, Keys.D, Keys.W, Keys.S);
        KeyBinding keyBindingPlayer3 = new KeyBinding(Keys.G, Keys.J, Keys.Y, Keys.H);
        handleDirectionsForEntity(level.players.get(0), keyBindingPlayer1);
        if (level.players.size() > 1) {
            handleDirectionsForEntity(level.players.get(1), keyBindingPlayer2);
        }
        if (level.players.size() > 2) {
            handleDirectionsForEntity(level.players.get(2), keyBindingPlayer3);
        }
        List<Entity> enemies = level.enemies;
        for (int enemyNr = 0; enemyNr < enemies.size(); enemyNr++) {
            moveEnemy(enemies.get(enemyNr), enemyNr + 1);
        }
    }

    private void switchSound() {
        currentSound.stop();
        if (levelNr % 2 == 0) {
            currentSound = sound2;
        } else {
            currentSound = sound1;
        }
        long soundId = currentSound.play(SOUND_VOLUME);
        currentSound.setLooping(soundId, true);
        currentSound.setPitch(soundId, 2);
    }

    private void moveEnemy(Entity enemy, int enemyNr) {
        int currentDirection = (LocalTime.now().getSecond() / 2 + enemyNr) % 4;
        if (currentDirection < 1) {
            doFirstPossibleMove(enemy, MOVE_RIGHT, MOVE_DOWN, MOVE_LEFT, MOVE_UP);
        } else if (currentDirection < 2) {
            doFirstPossibleMove(enemy, MOVE_DOWN, MOVE_RIGHT, MOVE_UP, MOVE_LEFT);
        } else if (currentDirection < 3) {
            doFirstPossibleMove(enemy, MOVE_UP, MOVE_LEFT, MOVE_DOWN, MOVE_RIGHT);
        } else {
            doFirstPossibleMove(enemy, MOVE_LEFT, MOVE_UP, MOVE_RIGHT, MOVE_DOWN);
        }
    }

    private void doFirstPossibleMove(Entity entity, Vector2... moves) {
        Arrays.stream(moves)
                .filter(move -> isMovePossible(entity, move))
                .findFirst()
                .ifPresent(move -> moveIfPossible(entity, move));
    }

    private void handleDirectionsForEntity(Entity entity, KeyBinding keyBinding) {
        // Must be factor of block size
        if (Gdx.input.isKeyPressed(keyBinding.left())) moveIfPossible(entity, MOVE_LEFT);
        if (Gdx.input.isKeyPressed(keyBinding.right())) moveIfPossible(entity, MOVE_RIGHT);
        if (Gdx.input.isKeyPressed(keyBinding.up())) moveIfPossible(entity, MOVE_UP);
        if (Gdx.input.isKeyPressed(keyBinding.down())) moveIfPossible(entity, MOVE_DOWN);
    }

    private void moveIfPossible(Entity entity, Vector2 move) {
        if (isMovePossible(entity, move)) {
            entity.rectangle().x += move.x;
            entity.rectangle().y += move.y;
            if (entity.entityType() == EntityType.PLAYER) {
                Gdx.app.debug(entity.name(),
                        "New position x: " + entity.rectangle().x
                                + ", y: " + entity.rectangle().y
                                + " after move: " + move);
            }
        }

        List<Entity> others = List.of();
        if (entity.entityType() == EntityType.ENEMY) {
            others = level.players;
        } else if (entity.entityType() == EntityType.PLAYER) {
            others = level.enemies;
        }
        if (others.stream()
                .anyMatch(otherEntity -> doEntitiesCollide(entity.rectangle(), otherEntity.rectangle(), move))) {
            gameLost = true;
            levelFinishedTime = LocalTime.now();
        }

    }

    private boolean isMovePossible(Entity entity, Vector2 move) {
        Rectangle rectangle = entity.rectangle();
        Rectangle newRectangle = new Rectangle(rectangle).setPosition(rectangle.getPosition(new Vector2()).add(move));
        Rectangle viewport = new Rectangle(-1, -1, camera.viewportWidth + 1, camera.viewportHeight + 1);
        return viewport.contains(newRectangle)
                && level.walls
                .stream()
                .noneMatch(wall -> newRectangle.overlaps(wall.rectangle()));
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
    public void resize(int i, int i1) {

    }

    @Override
    public void show() {
        currentSound.resume();
    }

    @Override
    public void pause() {
        currentSound.pause();
    }

    @Override
    public void resume() {
        currentSound.resume();
    }

    @Override
    public void hide() {
        currentSound.pause();
    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        playerTexture.dispose();
        enemyTexture.dispose();
        wallTexture.dispose();
        sound1.dispose();
        sound2.dispose();
    }

}
