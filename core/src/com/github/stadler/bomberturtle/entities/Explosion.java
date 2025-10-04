package com.github.stadler.bomberturtle.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import lombok.Getter;

public class Explosion {

    private static final int EXPLOSION_SIZE = 100;

    private final Animation<TextureRegion> animation;

    @Getter
    private boolean remove = false;
    @Getter
    private final Rectangle rectangle;
    private float stateTime = 0.0f;

    public Explosion(Rectangle rectangle) {
        this.rectangle = rectangle;
        Texture texture = new Texture("animations/explosion.png");
        TextureRegion[] textureRegion = TextureRegion.split(texture, (int) rectangle.width, (int) rectangle.height)[0];
        animation = new Animation<>(0.2f, textureRegion);
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        if (animation.isAnimationFinished(stateTime)) {
            remove = true;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(animation.getKeyFrame(stateTime),
                rectangle.x,
                rectangle.y);
    }
}
