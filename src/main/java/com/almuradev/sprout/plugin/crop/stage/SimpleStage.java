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
package com.almuradev.sprout.plugin.crop.stage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.almuradev.sprout.api.crop.Stage;
import com.almuradev.sprout.api.mech.Fertilizer;
import com.almuradev.sprout.api.mech.Light;
import com.almuradev.sprout.api.mech.Tool;

public class SimpleStage implements Stage {
    private final String name;
    private final Collection<Tool> tools;
    private final int growthRequired;
    private final int growthChance;
    private final Fertilizer fertilizer;
    private final Light light;
    private final int damage;

    public SimpleStage(String name, int growthRequired, int growthChance, Fertilizer fertilizer, Light light, Collection<Tool> tools, int damage) {
        this.name = name;
        this.tools = tools;
        this.growthRequired = growthRequired;
        this.growthChance = growthChance;
        this.fertilizer = fertilizer;
        this.light = light;
        this.damage = damage;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public int getGrowthRequired() {
        return growthRequired;
    }

    @Override
    public int getGrowthChance() {
        return growthChance;
    }

    @Override
    public Fertilizer getFertilizer() {
        return fertilizer;
    }

    @Override
    public Light getLight() {
        return light;
    }

    @Override
    public Collection<Tool> getTools() {
        return Collections.unmodifiableCollection(tools);
    }

    @Override
    public Collection<Tool> getRequiredTools() {
        final List<Tool> tools = new ArrayList<>();
        for (Tool tool : this.tools) {
            if (tool.isRequired()) {
                tools.add(tool);
            }
        }
        return tools;
    }

    @Override
    public Collection<Tool> getBonusTools() {
        final List<Tool> tools = new ArrayList<>();
        for (Tool tool : this.tools) {
            if (tool.isBonus()) {
                tools.add(tool);
            }
        }
        return tools;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SimpleStage)) {
            return false;
        }

        final SimpleStage other = (SimpleStage) obj;
        return other.getName().equals(name);
    }

    @Override
    public String toString() {
        return "Stage{name= " + name + ", growthRequired= " + growthRequired + ", growthChance= " + growthChance + ", fertilizer= " + fertilizer + ", light= " + light + ", tools= {" + tools + "}, damage= " + damage + "}";
    }
}
