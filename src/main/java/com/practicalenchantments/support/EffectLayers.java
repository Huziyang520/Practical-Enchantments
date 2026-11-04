// 按实体记账的层数仓库：每层自带剩余 tick，先进先出。

package com.practicalenchantments.support;

import net.minecraft.world.entity.LivingEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EffectLayers {
    private static final Map<UUID, List<Integer>> LAYERS = new ConcurrentHashMap<>();

    private EffectLayers() {
    }

    public static int count(LivingEntity entity) {
        if (entity == null) {
            return 0;
        }
        List<Integer> list = LAYERS.get(entity.getUUID());
        return list == null ? 0 : list.size();
    }

    public static void push(LivingEntity entity, int ticks) {
        if (entity == null) {
            return;
        }
        LAYERS.computeIfAbsent(entity.getUUID(), key -> new ArrayList<>()).add(Math.max(1, ticks));
    }

    public static int maxRemaining(LivingEntity entity) {
        if (entity == null) {
            return 0;
        }
        List<Integer> list = LAYERS.get(entity.getUUID());
        if (list == null) {
            return 0;
        }
        int max = 0;
        for (int value : list) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static int tickDown(LivingEntity entity) {
        if (entity == null) {
            return 0;
        }
        List<Integer> list = LAYERS.get(entity.getUUID());
        if (list == null) {
            return 0;
        }
        int alive = 0;
        for (int i = 0; i < list.size(); ) {
            int remaining = list.get(i) - 1;
            if (remaining <= 0) {
                list.remove(i);
            } else {
                list.set(i, remaining);
                i++;
                alive++;
            }
        }
        if (list.isEmpty()) {
            LAYERS.remove(entity.getUUID());
        }
        return alive;
    }

    public static int drainFirst(LivingEntity entity, int count) {
        if (entity == null || count <= 0) {
            return 0;
        }
        List<Integer> list = LAYERS.get(entity.getUUID());
        if (list == null) {
            return 0;
        }
        int total = 0;
        for (int taken = 0; taken < count && !list.isEmpty(); taken++) {
            total += list.remove(0);
        }
        if (list.isEmpty()) {
            LAYERS.remove(entity.getUUID());
        }
        return total;
    }

    public static void clear(LivingEntity entity) {
        if (entity != null) {
            LAYERS.remove(entity.getUUID());
        }
    }
}
