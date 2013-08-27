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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.almuradev.sprout.api.crop.Sprout;
import com.almuradev.sprout.api.crop.Stage;
import com.almuradev.sprout.api.mech.Drop;
import com.almuradev.sprout.api.mech.Fertilizer;
import com.almuradev.sprout.api.mech.Light;
import com.almuradev.sprout.api.mech.VariableHolder;
import com.almuradev.sprout.plugin.mech.SproutVariableHolder;

public class SimpleSprout implements Sprout {
	private final String name;
	private final Map<Integer, Stage> stages;
	private final Collection<Drop> drops;
	private final String blockSource;
	private final String itemSource;
	private final String placementSource;
	private final Fertilizer fertilizerSource;
	private final Light light;
	private final int damage;
	private int age = 0;
	private final VariableHolder variable;
	//Fertilization
	private final Map<Stage, Integer> fertilizerUsed;
	//Optimizations
	private boolean fullyGrown;

	public SimpleSprout(String name, String blockSource, String itemSource, String placementSource, int damage, Fertilizer fertilizerSource, Light light, Map<Integer, Stage> stages, Collection<Drop> drops) {
		this(name, blockSource, itemSource, placementSource, damage, fertilizerSource, light, stages, drops, new SproutVariableHolder());
	}

	public SimpleSprout(String name, String blockSource, String itemSource, String placementSource, int damage, Fertilizer fertilizerSource, Light light, Map<Integer, Stage> stages, Collection<Drop> drops, VariableHolder variable) {
		if (name == null || name.isEmpty() || itemSource == null || itemSource.isEmpty() || blockSource == null || blockSource.isEmpty()) {
			throw new IllegalArgumentException("Specified identifier , item or block source(s) is/are null!");
		}

		this.name = name;
		this.blockSource = blockSource;
		this.itemSource = itemSource;
		this.placementSource = placementSource;
		this.damage = damage;
		this.fertilizerSource = fertilizerSource;
		this.light = light;
		this.stages = stages == null ? Collections.<Integer, Stage>emptyMap() : stages;
		this.drops = drops == null ? Collections.<Drop>emptyList() : drops;
		this.variable = variable;
		this.fertilizerUsed = new LinkedHashMap<>();
		fullyGrown = false;
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
	public String getPlacementSource() {
		return placementSource;
	}

	@Override
	public int getDamage() {
		return damage;
	}

	@Override
	public Fertilizer getFertilizerSource() {
		return fertilizerSource;
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

		final Stage last = getLastStage();
		//Stage n (last stage)
		if (age >= last.getGrowthRequired()) {
			return last;
		}

		Stage middle = null;

		//Stage 0-n (some middle stage)
		for (Map.Entry<Integer, Stage> entry : stages.entrySet()) {

			if (age >= entry.getValue().getGrowthRequired()) {
				continue;
			}

			if (entry.getKey() >= 2) {
				middle = getPreviousStage(entry.getKey());
			} else {
				middle = entry.getValue();
			}
			break;
		}
		return middle;
	}

	@Override
	public boolean isFullyGrown() {
		return fullyGrown;
	}

	@Override
	public Light getLight() {
		return light;
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
	public VariableHolder getVariables() {
		return variable;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SimpleSprout)) {
			return false;
		}

		final SimpleSprout other = (SimpleSprout) obj;
		return other.getName().equals(name);
	}

	@Override
	public String toString() {
		return "Sprout{name= " + name + ", blockSource= " + blockSource + ", itemSource= " + itemSource + ", placementSource= " + placementSource + ", drops= {" + drops + "}, fertilizer= " + fertilizerSource + ", light= " + light + ", stages= {" + stages + "}, " + variable + ", fullyGrown= " + fullyGrown + "}";
	}

	public void setFullyGrown(boolean fullyGrown) {
		this.fullyGrown = fullyGrown;
	}

	public Stage getNextStage() {
		final Stage current = getCurrentStage();
		if (current == null) {
			return null;
		}
		//Find the current id
		Integer id = null;
		for (Map.Entry<Integer, Stage> entry : stages.entrySet()) {
			if (entry.getValue().equals(current)) {
				id = entry.getKey();
				break;
			}
		}

		if (id == null) {
			return null;
		}
		//Find the next id
		for (Map.Entry<Integer, Stage> entry : stages.entrySet()) {
			if (entry.getKey() == (id + 1)) {
				return entry.getValue();
			}
		}

		return null;
	}

	public Stage getFirstStage() {
		final List<Map.Entry<Integer, Stage>> entryList = new LinkedList(stages.entrySet());
		return entryList.get(0).getValue();
	}

	public Stage getPreviousStage(int current) {
		final List<Map.Entry<Integer, Stage>> entryList = new LinkedList(stages.entrySet());
		return entryList.get(current - 2).getValue();
	}

	public Stage getLastStage() {
		final List<Map.Entry<Integer, Stage>> entryList = new LinkedList(stages.entrySet());
		return entryList.get(entryList.size() - 1).getValue();
	}

	public boolean isOnLastStage() {
		final Stage current = getCurrentStage();
		if (current == null) {
			return false;
		}

		return getLastStage().equals(current);
	}

	public void grow(int amount) {
		age += amount;
	}

	public void grow(Stage stage) {
		age = stage.getGrowthRequired() + 1;
	}

	public void incrementFertilizerCount(Stage stage) {
		Integer value = fertilizerUsed.get(stage);
		if (value == null) {
			value = new Integer(1);
		} else {
			value = new Integer(value.intValue() + 1);
		}
		fertilizerUsed.put(stage, value);
	}

	public int getFertilizerCount(Stage stage) {
		if (fertilizerUsed.containsKey(stage)) {
			return fertilizerUsed.get(stage).intValue();
		}
		return 0;
	}
}
