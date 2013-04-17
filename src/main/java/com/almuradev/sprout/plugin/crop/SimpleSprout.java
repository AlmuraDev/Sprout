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
	public Sprout addStage(int level, Stage stage) {
		if (stage == null) {
			throw new IllegalArgumentException("Stage cannot be null!");
		}
		stages.put(level, stage);
		return this;
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
		long time = dispersedTime;
		for (Map.Entry<Integer, Stage> entry : stages.entrySet()) {
			final Integer key = entry.getKey();
			final Stage value = entry.getValue();
			prior = value;
			time += value.getGrowthTicks();
			if (key == 0) {
				 if (time > currentTime) {
					break;
				} else {
					continue;
				}
			}

			if (time < currentTime) {
				continue;
			} else if (time > currentTime) {
				break;
			}
		}
		return prior;
	}

	@Override
	public Stage getNextStage(long currentTime) {
		Stage prior = null;
		long time = dispersedTime;
		for (Map.Entry<Integer, Stage> entry : stages.entrySet()) {
			final Integer key = entry.getKey();
			final Stage value = entry.getValue();
			prior = value;
			time += value.getGrowthTicks();
			if (key == 0) {
				if (time < currentTime) {
					break;
				} else {
					continue;
				}
			}

			if (time < currentTime) {
				continue;
			} else if (time > currentTime) {
				break;
			}
		}
		return prior;
	}

	@Override
	public Stage removeStage(int level) {
		return stages.remove(level);
	}

	@Override
	public Map<Integer, Stage> getStages() {
		return Collections.unmodifiableMap(stages);
	}

	@Override
	public long getDispersedTime() {
		return dispersedTime;
	}

	protected void setDispersedTime(long dispersedTime) {
		this.dispersedTime = dispersedTime;
	}
}
