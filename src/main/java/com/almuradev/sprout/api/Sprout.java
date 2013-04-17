package com.almuradev.sprout.api;

import java.io.Serializable;
import java.util.Map;

public interface Sprout extends Serializable {
	public String getIdentifier();

	public Sprout addStage(int level, Stage stage);

	public Stage getStage(int level);

	public Stage getStage(String customName);

	public Stage getCurrentStage(long currentTime);

	public Stage getNextStage(long currentTime);

	public Stage removeStage(int level);

	public Map<Integer, Stage> getStages();

	public long getDispersedTime();
}
