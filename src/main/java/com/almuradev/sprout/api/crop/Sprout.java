/*
 * This file is part of Sprout.
 *
 * © 2013 AlmuraDev <http://www.almuradev.com/>
 * Sprout is licensed under the GNU General Public License.
 *
 * Sprout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sprout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License. If not,
 * see <http://www.gnu.org/licenses/> for the GNU General Public License.
 */
package com.almuradev.sprout.api.crop;

import java.util.Collection;
import java.util.Map;

import com.almuradev.sprout.api.mech.Drop;
import com.almuradev.sprout.api.mech.Fertilizer;
import com.almuradev.sprout.api.mech.Light;
import com.almuradev.sprout.api.mech.VariableHolder;

public interface Sprout {
    public String getName();

    public String getItemSource();

    public String getBlockSource();

    public String getPlacementSource();

    public String getToolSource();

    public int getDamage();

    public Fertilizer getFertilizerSource();

    public Light getLight();

    public Stage getStage(int level);

    public Stage getStage(String name);

    public Stage getCurrentStage();

    public boolean isFullyGrown();

    public Map<Integer, Stage> getStages();

    public Collection<Drop> getDrops();

    public int getBonusChance();

    public Collection<Drop> getBonus();

    public int getAge();

    public VariableHolder getVariables();
}
