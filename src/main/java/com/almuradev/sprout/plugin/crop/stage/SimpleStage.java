package com.almuradev.sprout.plugin.crop.stage;

import com.almuradev.sprout.api.Stage;

public class SimpleStage implements Stage {
	private final String customName;
	private final int growthTicks;

	public SimpleStage(final String customName, final int growthTicks) {
		this.customName = customName;
		this.growthTicks = growthTicks;
	}

	@Override
	public String getCustomName() {
		return customName;
	}

	@Override
	public int getGrowthTicks() {
		return growthTicks;
	}
}
