package com.github.stadler.bomberturtle.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Getter;

public class Explosion {

    private final Animation<TextureRegion> animation;

    private static final float OFFSET = 50.0f;
    @Getter
    private boolean remove = false;
    private final float x;
    private final float y;
    private float stateTime = 0.0f;

    public Explosion(float x, float y) {
        this.x = x - OFFSET;
        this.y = y - OFFSET;
        Texture texture = new Texture("animations/explosion.png");
        TextureRegion[] textureRegion = TextureRegion.split(texture, 128, 128)[0];
        animation = new Animation<>(0.2f, textureRegion);
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        if (animation.isAnimationFinished(stateTime)) {
            remove = true;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(animation.getKeyFrame(stateTime), x, y);
    }
}
