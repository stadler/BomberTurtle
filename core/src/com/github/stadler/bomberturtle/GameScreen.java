package com.github.stadler.bomberturtle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.badlogic.gdx.Input.Keys;
import static com.github.stadler.bomberturtle.LevelEditor.BLOCK_SIZE;

public class GameScreen implements Screen {

    public static final float SOUND_VOLUME = 0.5f;
    public static final int TOTAL_LEVELS = 4;
    private static final Duration GAME_TIME = Duration.of(15, ChronoUnit.SECONDS);
    public static final float MOVE_AMOUNT = BLOCK_SIZE / 10f;
    private final BomberTurtleGame game;
    private final OrthographicCamera camera;
    private final LevelEditor levelEditor;
    private final Sound sound1;
    private final Sound sound2;
    private final Random random = new Random();
    private Sound currentSound;
    private Level level;
    private int levelNr = 1;
    private LocalTime startTime;
    private LocalTime levelFinishedTime = null;
    private boolean gameWon = false;
    private int randomOffset;

    public GameScreen(final BomberTurtleGame game) {
        this.game = game;

        sound1 = Gdx.audio.newSound(Gdx.files.internal("audio/Cedi Nr. 3 - kurz.ogg"));
        sound2 = Gdx.audio.newSound(Gdx.files.internal("audio/Hit the Note.ogg"));
        currentSound = sound1;

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // Load level
        levelEditor = new LevelEditor(camera, game.wallTexture, game.playerTextures, game.enemyTexture);
        level = levelEditor.loadLevel("levels/level1.bt");
        initializeNewGame();
    }

    private void initializeNewGame() {
        startTime = LocalTime.now();
        gameWon = false;
        levelFinishedTime = null;
        level = levelEditor.loadLevel("levels/level" + levelNr + ".bt");
        randomOffset = random.nextInt(3);
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

        level.players.forEach(this::drawEntity);
        level.enemies.forEach(this::drawEntity);
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
            if (levelFinishedTime.plusSeconds(1).isBefore(LocalTime.now())
                    && Gdx.input.isKeyPressed(Keys.ANY_KEY)) {
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

    private void drawEntity(Entity entity) {
        game.batch.draw(new Sprite(entity.texture()),
                entity.rectangle().x, entity.rectangle().y,
                entity.rectangle().width / 2, entity.rectangle().height / 2f,
                entity.rectangle().width, entity.rectangle().height, 1, 1, calculateDegree(entity.lastMove()));
    }

    static float calculateDegree(Vector2 vector2) {
        return (float) (Math.atan2(vector2.y, vector2.x) * 180 / Math.PI) - 90;
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
        if (enemyNr % 2 == 0) {
            moveWithRandomDirection(enemy, enemyNr);
        } else {
            moveWithJaegerInstinct(enemy, enemyNr);
        }
    }

    private void moveWithJaegerInstinct(Entity enemy, int enemyNr) {
        Entity pray = level.players.get((randomOffset + enemyNr - 1) % 3);
        Vector2 nextMove = new Vector2(0, 0);
        if (enemy.rectangle().x < pray.rectangle().x) {
            nextMove.x = MOVE_AMOUNT;
        } else if (enemy.rectangle().x > pray.rectangle().x) {
            nextMove.x = -MOVE_AMOUNT;
        }
        if (enemy.rectangle().y < pray.rectangle().y) {
            nextMove.y = MOVE_AMOUNT;
        } else if (enemy.rectangle().y > pray.rectangle().y) {
            nextMove.y = -MOVE_AMOUNT;
        }
        doFirstPossibleMove(enemy,
                Stream.concat(calculatePermutations(nextMove),
                                Stream.concat(Stream.of(enemy.lastMove()),
                                        createRandomMoves().stream()))
                        .toArray(Vector2[]::new));
    }

    private void moveWithRandomDirection(Entity enemy, int enemyNr) {
        List<Vector2> alternativeMoves = createRandomMoves();
        // Change path from time to time
        if (!((LocalTime.now().getSecond() + enemyNr) % 2 == 0
                && Instant.now().get(ChronoField.MILLI_OF_SECOND) < 40)) {
            alternativeMoves.addFirst(enemy.lastMove());
        }
        doFirstPossibleMove(enemy, alternativeMoves.toArray(Vector2[]::new));
    }

    private static List<Vector2> createRandomMoves() {
        Vector2 baseMove = new Vector2(MOVE_AMOUNT, 0);
        List<Vector2> alternativeMoves = IntStream.range(0, 8)
                .mapToObj(i -> new Vector2(baseMove).rotateDeg(i * 45))
                .collect(Collectors.toList());
        Collections.shuffle(alternativeMoves);
        return alternativeMoves;
    }

    private void handleDirectionsForEntity(Entity entity, KeyBinding keyBinding) {
        // Must be factor of block size
        Vector2 nextMove = new Vector2(0, 0);
        if (Gdx.input.isKeyPressed(keyBinding.left())) {
            nextMove.x = -MOVE_AMOUNT;
        }
        if (Gdx.input.isKeyPressed(keyBinding.right())) {
            nextMove.x = MOVE_AMOUNT;
        }
        if (Gdx.input.isKeyPressed(keyBinding.up())) {
            nextMove.y = MOVE_AMOUNT;
        }
        if (Gdx.input.isKeyPressed(keyBinding.down())) {
            nextMove.y = -MOVE_AMOUNT;
        }
        // Move also if only one direction of the complete move works
        doFirstPossibleMove(entity, calculatePermutations(nextMove).toArray(Vector2[]::new));
    }

    private static Stream<Vector2> calculatePermutations(Vector2 move) {
        return Stream.of(new Vector2(move.x, move.y), new Vector2(move.x, 0), new Vector2(0, move.y));
    }

    private void doFirstPossibleMove(Entity entity, Vector2... moves) {
        Arrays.stream(moves)
                .filter(move -> isMovePossible(entity, move))
                .findFirst()
                .ifPresent(move -> moveEntity(entity, move));
    }

    private boolean isMovePossible(Entity entity, Vector2 move) {
        Rectangle rectangle = entity.rectangle();
        Rectangle newRectangle = new Rectangle(rectangle).setPosition(rectangle.getPosition(new Vector2()).add(move));
        Rectangle viewport = new Rectangle(-1, -1, camera.viewportWidth + 1, camera.viewportHeight + 1);
        return move.len() > 0f
                && viewport.contains(newRectangle)
                && level.walls
                .stream()
                .noneMatch(wall -> newRectangle.overlaps(wall.rectangle()));
    }

    private void moveEntity(Entity entity, Vector2 move) {
        entity.rectangle().x += move.x;
        entity.rectangle().y += move.y;
        entity.lastMove().set(new Vector2(move.x, move.y));
        if (entity.entityType() == EntityType.PLAYER) {
            Gdx.app.debug(entity.name(),
                    "New position x: " + entity.rectangle().x
                            + ", y: " + entity.rectangle().y
                            + " after move: " + move);
        }

        checkForOtherEntities(entity);
    }

    private void checkForOtherEntities(Entity entity) {
        List<Entity> others = List.of();
        if (entity.entityType() == EntityType.ENEMY) {
            others = level.players;
        } else if (entity.entityType() == EntityType.PLAYER) {
            others = level.enemies;
        }
        if (others.stream()
                .anyMatch(otherEntity -> entity.rectangle().overlaps(otherEntity.rectangle()))) {
            levelFinishedTime = LocalTime.now();
        }
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
        sound1.dispose();
        sound2.dispose();
    }

}
