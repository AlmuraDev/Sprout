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

public class SimpleStage implements Stage {
	private final String name;
	private final int growthInterval;

	public SimpleStage(final String name, final int growthInterval) {
		this.name = name;
		this.growthInterval = growthInterval;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getGrowthInterval() {
		return growthInterval;
	}

	@Override
	public String toString() {
		return "Stage{name= " + name + ", growthInterval= " + growthInterval + "}";
	}
}
