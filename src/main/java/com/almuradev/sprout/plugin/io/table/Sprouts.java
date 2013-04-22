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
package com.almuradev.sprout.plugin.io.table;

import com.almuradev.sprout.api.crop.Sprout;
import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

@Table("sprouts")
public class Sprouts {
	@Id
	private int id;

	@Field
	private String world;

	@Field
	private int x;

	@Field
	private int y;

	@Field
	private int z;

	@Field
	private Sprout sprout;

	public Sprouts() {

	}

	public Sprouts(String world, int x, int y, int z, Sprout sprout) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.sprout = sprout;
	}

	public int getId() {
		return id;
	}

	public String getWorld() {
		return world;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public Sprout getSprout() {
		return sprout;
	}
}
