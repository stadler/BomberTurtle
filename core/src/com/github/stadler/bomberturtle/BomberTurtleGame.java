package com.github.stadler.bomberturtle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class BomberTurtleGame extends Game {

    ShapeRenderer shape;
    SpriteBatch batch;
    BitmapFont titleFont;
    BitmapFont textFont;
    Texture wallTexture;
    List<Texture> playerTextures;
    Texture enemyTexture;
    Texture miniBombTexture;
    @Getter
    @Setter
    private int selectedPlayers = 1;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_INFO);

        shape = new ShapeRenderer();
        batch = new SpriteBatch();

        // Textures
        wallTexture = new Texture("wall.png");
        playerTextures = List.of(
                new Texture("Bombe_blau.png"),
                new Texture("Bombe_grün.png"),
                new Texture("Bombe_rot.png"));
        enemyTexture = new Texture("Schilki.png");
        miniBombTexture = new Texture("MiniBombe.png");

        // Fonts
        titleFont = generateFont("fonts/Silkscreen/Silkscreen-Bold.ttf", 30);
        textFont = generateFont("fonts/Silkscreen/Silkscreen-Regular.ttf", 20);

        this.setScreen(new MainMenuScreen(this));
    }

    private BitmapFont generateFont(String path, int size) {
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
        fontParameter.size = size;
        fontParameter.shadowColor = Color.DARK_GRAY;
        return fontGenerator.generateFont(fontParameter);
    }

    @Override
    public void render() {
        super.render(); // important!
    }

    @Override
    public void dispose() {
        batch.dispose();
        shape.dispose();

        playerTextures.forEach(Texture::dispose);
        enemyTexture.dispose();
        wallTexture.dispose();
        miniBombTexture.dispose();

        titleFont.dispose();
        textFont.dispose();
    }
}
