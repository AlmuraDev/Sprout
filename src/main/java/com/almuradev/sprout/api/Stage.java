package com.almuradev.sprout.api;

/**
 * Immutable object which binds a custom block's name to growth ticks.
 */
public interface Stage {
	public String getCustomName();

	public int getGrowthTicks();
}
