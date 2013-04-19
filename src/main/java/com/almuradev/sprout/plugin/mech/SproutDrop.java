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
package com.almuradev.sprout.plugin.mech;

import com.almuradev.sprout.api.mech.Drop;

public class SproutDrop implements Drop {
	private final String name;
	private final int amount;

	public SproutDrop(String name, int amount) {
		this.name = name;
		this.amount = amount;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SproutDrop)) {
			return false;
		}

		final SproutDrop other = (SproutDrop) obj;
		return other.getName().equals(name);
	}

	@Override
	public String toString() {
		return "Drop{name= " + name + ", amount= " + amount + "}";
	}
}
