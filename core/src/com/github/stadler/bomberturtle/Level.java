package com.github.stadler.bomberturtle;

import com.github.stadler.bomberturtle.entities.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Level {
    List<Entity> walls = new ArrayList<>();
    List<PlayerEntity> players = new ArrayList<>();
    List<MovableEntity> enemies = new ArrayList<>();
    List<BombEntity> miniBombs = new ArrayList<>();

    Duration calculateLevelTime() {
        Optional<MovableEntity> bossOptional = findBoss();
        if (bossOptional.isPresent() && bossOptional.get().getEntityType() == EntityType.DARTH_VADER) {
            return Duration.of(60, ChronoUnit.SECONDS);
        } else if (bossOptional.isPresent() && bossOptional.get().getEntityType() == EntityType.POOP) {
            return Duration.of(120, ChronoUnit.SECONDS);
        }
        return Duration.of(15, ChronoUnit.SECONDS);
    }

    Optional<MovableEntity> findBoss() {
        return enemies.stream()
                .filter(movableEntity -> movableEntity instanceof BossEntity)
                .findFirst();
    }


}
