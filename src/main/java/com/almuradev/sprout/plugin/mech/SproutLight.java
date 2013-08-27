package com.almuradev.sprout.plugin.mech;

import com.almuradev.sprout.api.mech.Light;

public class SproutLight implements Light {
	private final int minimumBlockLight, maximumBlockLight;

	public SproutLight(int minimumBlockLight, int maximumBlockLight) {
		this.minimumBlockLight = minimumBlockLight;
		this.maximumBlockLight = maximumBlockLight;
	}

	@Override
	public int getMinimumBlockLight() {
		return minimumBlockLight;
	}

	@Override
	public int getMaximumBlockLight() {
		return maximumBlockLight;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SproutLight)) {
			return false;
		}

		final SproutLight other = (SproutLight) obj;
		return minimumBlockLight == other.minimumBlockLight && maximumBlockLight == other.maximumBlockLight;
	}

	@Override
	public String toString() {
		return "Light{minimumBlockLight= " + minimumBlockLight + ", maximumBlockLight= " + maximumBlockLight + "}";
	}
}
