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
package com.almuradev.sprout.plugin.crop;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.almuradev.sprout.api.crop.Sprout;
import com.almuradev.sprout.api.crop.Stage;
import com.almuradev.sprout.api.mech.Drop;

public class SimpleSprout implements Sprout {
	private final String name;
	private final Map<Integer, Stage> stages;
	private final Collection<Drop> drops;
	private final String blockSource;
	private final String itemSource;
	private int age = 0;

	public SimpleSprout(String name, String blockSource, String itemSource, Map<Integer, Stage> stages, Collection<Drop> drops) {
		if (name == null || name.isEmpty() || itemSource == null || itemSource.isEmpty() || blockSource == null || blockSource.isEmpty()) {
			throw new IllegalArgumentException("Specified identifier , item or block source(s) is/are null!");
		}

		this.name = name;
		this.blockSource = blockSource;
		this.itemSource = itemSource;
		this.stages = stages == null ? Collections.<Integer, Stage>emptyMap() : stages;
		this.drops = drops == null ? Collections.<Drop>emptyList() : drops;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getBlockSource() {
		return blockSource;
	}

	@Override
	public String getItemSource() {
		return itemSource;
	}

	@Override
	public Stage getStage(int level) {
		return stages.get(level);
	}

	@Override
	public Stage getStage(String name) {
		for (Map.Entry<Integer, Stage> entry : stages.entrySet()) {
			final Stage stage = entry.getValue();

			if (stage.getName().equals(name)) {
				return stage;
			}
		}
		return null;
	}

	@Override
	public Stage getCurrentStage() {
		Stage prior = null;
		for (Map.Entry<Integer, Stage> entry : stages.entrySet()) {
			final Stage value = entry.getValue();
			if (value.getGrowthInterval() >= age) {
				break;
			}
			prior = value;
		}
		return prior;
	}

	@Override
	public Map<Integer, Stage> getStages() {
		return Collections.unmodifiableMap(stages);
	}

	@Override
	public Collection<Drop> getDrops() {
		return Collections.unmodifiableCollection(drops);
	}

	@Override
	public int getAge() {
		return age;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SimpleSprout)) {
			return false;
		}

		final SimpleSprout other = (SimpleSprout) obj;
		return other.getName().equals(name) && other.getBlockSource().equals(blockSource) && other.getItemSource().equals(itemSource) && other.getDrops().equals(drops) && other.getStages().equals(stages);
	}

	@Override
	public String toString() {
		return "Sprout{name= " + name + ", blockSource= " + blockSource + ", itemSource= " + itemSource + ", drops= {" + drops.toString() + "}, stages= {" + stages.toString() + "}}";
	}

	public void grow(int amount) {
		age += amount;
	}
}
