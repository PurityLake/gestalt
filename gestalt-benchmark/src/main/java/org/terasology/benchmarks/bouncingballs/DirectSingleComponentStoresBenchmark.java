/*
 * Copyright 2019 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.benchmarks.bouncingballs;

import org.terasology.benchmarks.bouncingballs.arraystore.ArrayComponentStore;
import org.terasology.benchmarks.bouncingballs.common.Location;
import org.terasology.benchmarks.bouncingballs.common.Physics;
import org.terasology.entitysystem.component.ComponentManager;

import java.util.Random;

class DirectSingleComponentStoresBenchmark implements Benchmark {
    ArrayComponentStore<Location> locationStore;
    ArrayComponentStore<Physics> physicsStore;
    long bounces;


    @Override
    public String getName() {
        return "Direct with Single Component Stores benchmark";
    }

    @Override
    public void setup() {
        ComponentManager componentManager = new ComponentManager();
        locationStore = new ArrayComponentStore<>(componentManager.getType(Location.class),10000);
        physicsStore = new ArrayComponentStore<>(componentManager.getType(Physics.class), 10000);

        Random random = new Random();

        for (int i = 0; i < 10000; ++i) {
            Location location = new Location();
            location.setX(400 * random.nextFloat() - 200);
            location.setY(random.nextFloat() * 100);
            location.setZ(400 * random.nextFloat() - 200);
            Physics physics = new Physics();
            physics.setVelocityY(random.nextFloat() * 10 - 5);
            locationStore.set(i, location);
            physicsStore.set(i, physics);
        }
    }

    @Override
    public void cleanup() {
        System.out.println("Bounces: " + bounces);
        locationStore = null;
        physicsStore = null;
    }

    @Override
    public void doFrame(float delta) {
        Physics physics = new Physics();
        Location location = new Location();
        for (int id = 0; id < 10000; ++id) {
            if (physicsStore.get(id, physics) && locationStore.get(id, location)) {
                physics.setVelocityY(physics.getVelocityY() - 10.0f * delta);
                float y = location.getY();
                y = y + physics.getVelocityY() * delta;
                if (y < 0) {
                    y *= -1;
                    physics.setVelocityY(physics.getVelocityY() * -1);
                    bounces++;
                }
                location.setY(y);
                physicsStore.set(id, physics);
                locationStore.set(id, location);
            }

        }
    }

}
