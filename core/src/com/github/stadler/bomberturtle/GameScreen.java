package com.github.stadler.bomberturtle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.stadler.bomberturtle.entities.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.badlogic.gdx.Input.Keys;
import static com.github.stadler.bomberturtle.Constants.*;

public class GameScreen implements Screen {

    private final Random random = new Random();

    private final BomberTurtleGame game;
    private final OrthographicCamera camera;
    private final LevelEditor levelEditor;
    private final Sound sound1;
    private final Sound sound2;

    private Sound currentSound;
    private Level level;
    private int levelNr = START_LEVEL;
    private LocalTime startTime;
    private LocalTime levelFinishedTime = null;
    private boolean gameWon = false;
    private int randomOffset;

    private final List<Explosion> explosions = new ArrayList<>();

    public GameScreen(final BomberTurtleGame game) {
        this.game = game;

        sound1 = Gdx.audio.newSound(Gdx.files.internal("audio/Cedi Nr. 3 - kurz.ogg"));
        sound2 = Gdx.audio.newSound(Gdx.files.internal("audio/Hit the Note.ogg"));
        currentSound = sound1;

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        // Load level
        levelEditor = new LevelEditor(camera, game.wallTexture, game.playerTextures, game.enemyTexture, game.darthVaderTexture, game.poopTexture);
        level = levelEditor.loadLevel(LEVEL_PATH + "level1.bt", game.getSelectedPlayers());
        initializeNewGame();
    }

    private void initializeNewGame() {
        startTime = LocalTime.now();
        gameWon = false;
        levelFinishedTime = null;
        level = levelEditor.loadLevel(LEVEL_PATH + "level" + levelNr + ".bt", game.getSelectedPlayers());
        randomOffset = random.nextInt(game.getSelectedPlayers());
        switchSound();
    }

    @Override
    public void render(float deltaTime) {
        // clear the screen with a dark blue color. The arguments to clear are the red, green
        // blue and alpha component in the range [0,1] of the color to be used to clear the screen.
        ScreenUtils.clear(Constants.START_SCREEN_COLOR);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.shape.setProjectionMatrix(camera.combined);
        game.shape.begin(ShapeRenderer.ShapeType.Line);
        game.shape.setColor(Color.RED);

        level.players.forEach(this::drawEntity);
        level.enemies.forEach(this::drawEntity);
        level.walls.forEach(this::drawEntity);
        level.miniBombs.forEach(this::drawEntity);
        explosions.forEach(explosion -> {
            removeEntitiesInExplosion(explosion, level.players);
            removeEntitiesInExplosion(explosion, level.enemies);
            removeEntitiesInExplosion(explosion, level.walls);
            explosion.update(deltaTime);
            explosion.render(game.batch);
            drawDebugRectangle(explosion.getRectangle());
        });
        explosions.removeAll(
                explosions.stream()
                        .filter(Explosion::isRemove)
                        .toList());
        if (level.enemies.stream().noneMatch(Entity::isVisible)) {
            setGameFinished(true);
        }
        if (level.players.stream().noneMatch(Entity::isVisible)) {
            setGameFinished(false);
        }
        if (!isLevelFinished()) {
            long remainingSeconds = calculateRemainingSeconds(level);
            if (remainingSeconds <= 0) {
                setGameFinished(true);
            } else {
                game.textFont.draw(game.batch, "Zeit: " + remainingSeconds, 100, camera.viewportHeight - 10);
                drawBossHealth(level);
            }

        } else {
            game.titleFont.draw(game.batch, gameWon ? "Gewonnen!" : "Game Over", 300, 300);
            if (gameWon) {
                if (levelNr < TOTAL_LEVELS) {
                    game.textFont.draw(game.batch, "Ab zu Level Nr." + (levelNr + 1), 300, 200);
                } else {
                    game.textFont.draw(game.batch, "Du hast alle Levels geschafft!", 150, 200);
                }
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
        game.shape.end();
        game.batch.end();

        if (!isLevelFinished()) {
            handleInputs();
        }
    }

    private void drawBossHealth(Level level) {
        Optional<MovableEntity> boss = level.findBoss();
        if (boss.isPresent()) {
            int strength = boss.get().getStrength();
            String healthString = "Leben: ";
            for (int health = 0; health < strength; health++) {
                healthString += "<3 ";
            }
            game.textFont.draw(game.batch, healthString, 300, camera.viewportHeight - 10);
        }
    }

    private void drawDebugRectangle(Rectangle rectangle) {
        if (Constants.DRAW_DEBUG_RECTANGLE) {
            game.shape.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
    }

    private void setGameFinished(boolean hasWon) {
        gameWon = hasWon;
        if (levelFinishedTime == null) {
            levelFinishedTime = LocalTime.now();
        }
    }

    private <T extends Entity> void removeEntitiesInExplosion(Explosion explosion, List<T> entities) {
        entities.stream()
                .filter(entity ->
                        explosion.getRectangle().overlaps(entity.getRectangle())
                        && explosion.hasStarted())
                .forEach(entity -> {
                    entity.setStrength(entity.getStrength() - 1);
                    if (entity.getStrength() <= 0) {
                        entity.setVisible(false);
                    }
                });
    }

    private void drawEntity(Entity entity) {
        if (!entity.isVisible()) {
            return;
        }
        Vector2 lastMove = new Vector2(0f, 0f);
        if (entity instanceof MovableEntity me) {
            lastMove = me.getLastMove();
        }
        game.batch.draw(new Sprite(entity.getTexture()),
                entity.getRectangle().x, entity.getRectangle().y,
                entity.getRectangle().width / 2, entity.getRectangle().height / 2f,
                entity.getRectangle().width, entity.getRectangle().height,
                1, 1,
                calculateDegree(lastMove));

        drawDebugRectangle(entity.getRectangle());
    }

    static float calculateDegree(Vector2 vector2) {
        if (vector2 == null) {
            return 0;
        }
        return (float) (Math.atan2(vector2.y, vector2.x) * 180 / Math.PI) - 90;
    }

    private boolean isLevelFinished() {
        return levelFinishedTime != null;
    }

    private long calculateRemainingSeconds(Level level) {
        LocalTime endTime = startTime.plus(level.calculateLevelTime());
        Duration remainingTime = Duration.between(LocalTime.now(), endTime);
        return remainingTime.toSeconds();
    }

    private void handleInputs() {
        // process user input
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            System.exit(0);
        }
        KeyBinding keyBindingPlayer1 = new KeyBinding(Keys.LEFT, Keys.RIGHT, Keys.UP, Keys.DOWN, Keys.SHIFT_RIGHT, Keys.SLASH);
        KeyBinding keyBindingPlayer2 = new KeyBinding(Keys.A, Keys.D, Keys.W, Keys.S, Keys.Q, Keys.E);
        KeyBinding keyBindingPlayer3 = new KeyBinding(Keys.G, Keys.J, Keys.Y, Keys.H, Keys.T, Keys.U);
        handleInputsForPlayer(0, keyBindingPlayer1);
        handleInputsForPlayer(1, keyBindingPlayer2);
        handleInputsForPlayer(2, keyBindingPlayer3);
        List<MovableEntity> enemies = level.enemies;
        for (int enemyNr = 0; enemyNr < enemies.size(); enemyNr++) {
            moveEnemy(enemies.get(enemyNr), enemyNr + 1);
        }
    }

    private void handleInputsForPlayer(int playerNr, KeyBinding keyBindingPlayer2) {
        if (playerExists(playerNr)) {
            handleInputsForEntity(level.players.get(playerNr), keyBindingPlayer2);
        }
    }

    private boolean playerExists(int playerNr) {
        return level.players.size() > playerNr && level.players.get(playerNr) != null;
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
        currentSound.setPitch(soundId, SOUND_PITCH);
    }

    private void moveEnemy(MovableEntity enemy, int enemyNr) {
        if (enemyNr % 4 != 3) {
            moveWithRandomDirection(enemy, enemyNr);
        } else {
            moveWithJaegerInstinct(enemy, enemyNr);
        }
    }

    private void moveWithJaegerInstinct(MovableEntity enemy, int enemyNr) {
        int victimPlayerNr = (randomOffset + enemyNr - 1) % game.getSelectedPlayers();
        if (!playerExists(victimPlayerNr)) {
            victimPlayerNr = (victimPlayerNr + 1) % game.getSelectedPlayers();
        }
        Entity pray = level.players.get(victimPlayerNr);
        Vector2 nextMove = new Vector2(0, 0);
        if (enemy.getRectangle().x < pray.getRectangle().x) {
            nextMove.x = MOVE_AMOUNT;
        } else if (enemy.getRectangle().x > pray.getRectangle().x) {
            nextMove.x = -MOVE_AMOUNT;
        }
        if (enemy.getRectangle().y < pray.getRectangle().y) {
            nextMove.y = MOVE_AMOUNT;
        } else if (enemy.getRectangle().y > pray.getRectangle().y) {
            nextMove.y = -MOVE_AMOUNT;
        }
        doFirstPossibleMove(enemy,
                Stream.concat(calculatePermutations(nextMove),
                                Stream.concat(Stream.of(enemy.getLastMove()),
                                        createRandomMoves().stream()))
                        .toArray(Vector2[]::new));
    }

    private void moveWithRandomDirection(MovableEntity enemy, int enemyNr) {
        List<Vector2> alternativeMoves = createRandomMoves();
        // Change path from time to time
        if (!((LocalTime.now().getSecond() + enemyNr) % 2 == 0
                && Instant.now().get(ChronoField.MILLI_OF_SECOND) < 40)) {
            alternativeMoves.addFirst(enemy.getLastMove());
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

    private void handleInputsForEntity(PlayerEntity playerEntity, KeyBinding keyBinding) {
        if (!playerEntity.isVisible()) {
            return;
        }
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
        if (Gdx.input.isKeyPressed(keyBinding.dropBomb())) {
            if (playerEntity.getLastBomb().isBefore(Instant.now().minus(BOMB_DELAY))) {
                level.miniBombs.add(
                        new BombEntity("MiniBomb", EntityType.BOMB,
                                game.miniBombTexture,
                                calculateMiniBombRectangle(playerEntity.getRectangle()),
                                playerEntity));
                playerEntity.setLastBomb(Instant.now());
            }
        }
        if (Gdx.input.isKeyPressed(keyBinding.igniteBomb())) {
            List<BombEntity> bombsToIgnite = level.miniBombs.stream()
                    .filter(bomb -> bomb.getFromPlayer() == playerEntity)
                    .toList();
            explosions.addAll(bombsToIgnite.stream()
                    .map(bomb -> new Explosion(calculateExplosionRectangle(bomb.getRectangle())))
                    .toList());
            level.miniBombs.removeAll(bombsToIgnite);
        }
        // Move also if only one direction of the complete move works
        doFirstPossibleMove(playerEntity, calculatePermutations(nextMove).toArray(Vector2[]::new));
    }

    private static Rectangle calculateExplosionRectangle(Rectangle bombRectangle) {
        return new Rectangle(
                bombRectangle.x + (bombRectangle.width / 2) - BLOCK_SIZE,
                bombRectangle.y + (bombRectangle.height / 2) - BLOCK_SIZE,
                BLOCK_SIZE * 2,
                BLOCK_SIZE * 2);
    }

    private static Rectangle calculateMiniBombRectangle(Rectangle playerRectangle) {
        return new Rectangle(
                playerRectangle.x + (BLOCK_SIZE /2) - (BOMB_SIZE /2),
                playerRectangle.y + (BLOCK_SIZE /2) - (BOMB_SIZE /2),
                BOMB_SIZE,
                BOMB_SIZE);
    }

    private static Stream<Vector2> calculatePermutations(Vector2 move) {
        return Stream.of(new Vector2(move.x, move.y), new Vector2(move.x, 0), new Vector2(0, move.y));
    }

    private void doFirstPossibleMove(MovableEntity entity, Vector2... moves) {
        Arrays.stream(moves)
                .filter(move -> isMovePossible(entity, move))
                .findFirst()
                .ifPresent(move -> moveEntity(entity, move));
    }

    private boolean isMovePossible(Entity entity, Vector2 move) {
        Rectangle rectangle = entity.getRectangle();
        Rectangle newRectangle = new Rectangle(rectangle).setPosition(rectangle.getPosition(new Vector2()).add(move));
        ajustEntityRectangleOnScreenLeave(newRectangle);
        return move.len() > 0f
                && doesNotCollideWithWall(newRectangle);
    }

    private void ajustEntityRectangleOnScreenLeave(Rectangle newRectangle) {
        Rectangle viewport = new Rectangle(-1, -1, camera.viewportWidth + 1, camera.viewportHeight + 1);
        if (!viewport.contains(newRectangle)) {
            if (newRectangle.x < -1) {
                newRectangle.x += camera.viewportWidth;
            }
            if (newRectangle.x > camera.viewportWidth - newRectangle.width) {
                newRectangle.x -= camera.viewportWidth;
            }
            if (newRectangle.y < -1) {
                newRectangle.y += camera.viewportHeight;
            }
            if (newRectangle.y > camera.viewportHeight + 1) {
                newRectangle.y -= camera.viewportHeight;
            }
        }
    }

    private boolean doesNotCollideWithWall(Rectangle newRectangle) {
        return level.walls
                .stream()
                .filter(Entity::isVisible)
                .noneMatch(wall -> newRectangle.overlaps(wall.getRectangle()));
    }

    private void moveEntity(MovableEntity entity, Vector2 move) {
        entity.getRectangle().x += move.x;
        entity.getRectangle().y += move.y;
        ajustEntityRectangleOnScreenLeave(entity.getRectangle());
        entity.getLastMove().set(new Vector2(move.x, move.y));
        if (entity.getEntityType() == EntityType.PLAYER) {
            Gdx.app.debug(entity.getName(),
                    "New position x: " + entity.getRectangle().x
                            + ", y: " + entity.getRectangle().y
                            + " after move: " + move);
        }

        checkForOtherEntities(entity);
    }

    private void checkForOtherEntities(MovableEntity entity) {
        if (entity.isPlayer() && collidesWithOthers(entity)) {
            entity.setVisible(false);
        }
    }

    private boolean collidesWithOthers(MovableEntity entity) {
        List<? extends MovableEntity> others = getAdversariesOf(entity);
        return others.stream()
                .filter(Entity::isVisible)
                .anyMatch(otherEntity -> entity.getRectangle().overlaps(otherEntity.getRectangle()));
    }

    private List<? extends MovableEntity> getAdversariesOf(MovableEntity entity) {
        List<? extends MovableEntity> others = List.of();
        if (entity.isEnemy()) {
            others = level.players;
        } else if (entity.isPlayer()) {
            others = level.enemies;
        }
        return others.stream()
                .filter(Entity::isVisible)
                .collect(Collectors.toList());
    }

    @Override
    public void resize(int x, int y) {
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
