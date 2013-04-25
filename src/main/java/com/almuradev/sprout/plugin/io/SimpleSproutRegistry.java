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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.almuradev.sprout.api.crop.Sprout;
import com.almuradev.sprout.api.io.SproutRegistry;

public class SimpleSproutRegistry implements SproutRegistry {
	private List<Sprout> sprouts = new ArrayList<>();

	@Override
	public Sprout add(Sprout sprout) {
		if (sprout == null) {
			throw new IllegalArgumentException("Sprout is null!");
		}
		sprouts.add(sprout);
		return sprout;
	}

	@Override
	public SproutRegistry addAll(Collection<Sprout> sprouts) {
		if (sprouts == null) {
			throw new IllegalArgumentException("Sprouts is null!");
		}
		this.sprouts.addAll(sprouts);
		return this;
	}

	@Override
	public boolean contains(String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name is null or empty!");
		}
		for (Sprout sprout : sprouts) {
			if (sprout.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Sprout get(String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name is null or empty!");
		}
		for (Sprout sprout : sprouts) {
			if (sprout.getName().equals(name)) {
				return sprout;
			}
		}
		return null;
	}

	@Override
	public int size() {
		return sprouts.size();
	}

	@Override
	public Sprout find(String itemSource) {
		if (itemSource == null || itemSource.isEmpty()) {
			throw new IllegalArgumentException("Item source is null or empty!");
		}
		for (Sprout sprout : sprouts) {
			if (sprout.getItemSource().endsWith(itemSource)) {
				return sprout;
			}
		}
		return null;
	}

	@Override
	public Sprout get(int index) {
		if (index > sprouts.size()) {
			return null;
		}

		return sprouts.get(index);
	}

	@Override
	public Sprout remove(String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name is null or empty!");
		}

		final Iterator<Sprout> sproutIterator = sprouts.iterator();
		Sprout toRemove = null;
		while (sproutIterator.hasNext()) {
			toRemove = sproutIterator.next();
			if (toRemove.getName().equals(name)) {
				sproutIterator.remove();
			}
		}
		return toRemove;
	}

	@Override
	public Collection<Sprout> getAll() {
		return Collections.unmodifiableCollection(sprouts);
	}
}
