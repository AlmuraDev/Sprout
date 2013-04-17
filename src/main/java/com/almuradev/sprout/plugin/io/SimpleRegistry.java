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
package com.almuradev.sprout.plugin.io;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.almuradev.sprout.api.Sprout;
import com.almuradev.sprout.api.io.Registry;
import com.almuradev.sprout.api.util.TInt21TripleObjectHashMap;

public class SimpleRegistry implements Registry {
	private static final HashMap<String, TInt21TripleObjectHashMap> REGISTRIES = new HashMap<>();

	@Override
	public Sprout add(String world, int x, int y, int z, Sprout sprout) {
		if (world == null || world.isEmpty()) {
			throw new NullPointerException("Specified world is null!");
		}
		TInt21TripleObjectHashMap REGISTRY = REGISTRIES.get(world);
		if (REGISTRY == null) {
			REGISTRY = new TInt21TripleObjectHashMap();
			REGISTRIES.put(world, REGISTRY);
		}
		return (Sprout) REGISTRY.put(x, y, z, sprout);
	}

	@Override
	public boolean contains(String world, int x, int y, int z) {
		if (world == null || world.isEmpty()) {
			throw new NullPointerException("Specified world is null!");
		}
		final TInt21TripleObjectHashMap REGISTRY = REGISTRIES.get(world);
		if (REGISTRY == null) {
			return false;
		}
		return REGISTRY.containsKey(x, y, z);
	}

	@Override
	public Sprout get(String world, int x, int y, int z) {
		if (world == null || world.isEmpty()) {
			throw new NullPointerException("Specified world is null!");
		}
		final TInt21TripleObjectHashMap REGISTRY = REGISTRIES.get(world);
		if (REGISTRY == null) {
			return null;
		}
		return (Sprout) REGISTRY.get(x, y, z);
	}

	@Override
	public Sprout remove(String world, int x, int y, int z) {
		if (world == null || world.isEmpty()) {
			throw new NullPointerException("Specified world is null!");
		}
		final TInt21TripleObjectHashMap REGISTRY = REGISTRIES.get(world);
		if (REGISTRY == null) {
			return null;
		}
		return (Sprout) REGISTRY.remove(x, y, z);
	}

	@Override
	public TInt21TripleObjectHashMap getRegistryFor(String world) {
		return REGISTRIES.get(world);
	}

	@Override
	public Map<String, TInt21TripleObjectHashMap> getAll() {
		return Collections.unmodifiableMap(REGISTRIES);
	}
}
