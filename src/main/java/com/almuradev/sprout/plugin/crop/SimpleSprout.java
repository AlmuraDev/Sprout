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

import java.util.Collections;
import java.util.Map;

import com.almuradev.sprout.api.Sprout;
import com.almuradev.sprout.api.Stage;

public class SimpleSprout implements Sprout {
	private final String identifier;
	private final Map<Integer, Stage> stages;
	private long dispersedTime;

	public SimpleSprout(String identifier, Map<Integer, Stage> stages) {
		if (identifier == null || identifier.isEmpty()) {
			throw new IllegalArgumentException("Specified identifier is null!");
		}

		this.identifier = identifier;
		this.stages = stages == null ? Collections.<Integer, Stage>emptyMap() : stages;
	}

	@Override
	public String getIdentifier() {
		return identifier;
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
	public long getDispersedTime() {
		return dispersedTime;
	}

	public void setDispersedTime(long dispersedTime) {
		this.dispersedTime = dispersedTime;
	}
}
