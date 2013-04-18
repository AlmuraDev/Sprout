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
	private final String identifier;
	private final String source;
	private final Map<Integer, Stage> stages;
	private final Collection<Drop> drops;
	private long dispersedTime;

	public SimpleSprout(String identifier, String source, Map<Integer, Stage> stages, Collection<Drop> drops) {
		if (identifier == null || identifier.isEmpty() || source == null || source.isEmpty()) {
			throw new IllegalArgumentException("Specified identifier or source is null!");
		}

		this.identifier = identifier;
		this.source = source;
		this.stages = stages == null ? Collections.<Integer, Stage>emptyMap() : stages;
		this.drops = drops == null ? Collections.<Drop>emptyList() : drops;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public Stage getStage(int level) {
		return stages.get(level);
	}

	@Override
	public Stage getStage(String customName) {
		for (Map.Entry<Integer, Stage> entry : stages.entrySet()) {
			final Stage stage = entry.getValue();

			if (stage.getCustomName().equals(customName)) {
				return stage;
			}
		}
		return null;
	}

	@Override
	public Stage getCurrentStage(long currentTime) {
		Stage prior = null;
		long increment = dispersedTime;
		for (Map.Entry<Integer, Stage> entry : stages.entrySet()) {
			final Integer key = entry.getKey();
			final Stage value = entry.getValue();
			prior = value;
			increment += value.getGrowthTicks();
			if (key == 0) {
				if (increment > currentTime) {
					break;
				} else {
					continue;
				}
			}

			if (increment > currentTime) {
				break;
			}
		}
		return prior;
	}

	@Override
	public Stage getNextStage(long currentTime) {
		final Stage stage = getCurrentStage(currentTime);
		for (Map.Entry<Integer, Stage> entry : stages.entrySet()) {
			final Stage value = entry.getValue();
			if (stage.equals(value)) {
				return getStage(entry.getKey() + 1);
			}
		}
		return null;
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
	public long getDispersedTime() {
		return dispersedTime;
	}

	public void setDispersedTime(long dispersedTime) {
		this.dispersedTime = dispersedTime;
	}
}
