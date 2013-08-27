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

import com.almuradev.sprout.api.crop.Stage;
import com.almuradev.sprout.api.mech.Fertilizer;
import com.almuradev.sprout.api.mech.Light;

public class SimpleStage implements Stage {
	private final String name;
	private final int growthRequired;
	private final int growthChance;
	private final Fertilizer fertilizer;
	private final Light light;
	private final int damage;

	public SimpleStage(final String name, final int growthRequired, final int growthChance, final Fertilizer fertilizer, final Light light, int damage) {
		this.name = name;
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
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SimpleStage)) {
			return false;
		}

		final SimpleStage other = (SimpleStage) obj;
		return other.getName().equals(name);
	}

	@Override
	public String toString() {
		return "Stage{name= " + name + ", growthRequired= " + growthRequired + ", growthChance= " + growthChance + ", fertilizer= " + fertilizer + ", light= " + light + ", damage= " + damage + "}";
	}
}
