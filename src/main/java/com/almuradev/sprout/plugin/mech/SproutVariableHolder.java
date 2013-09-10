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
package com.almuradev.sprout.plugin.mech;

import com.almuradev.sprout.api.mech.VariableHolder;

public class SproutVariableHolder implements VariableHolder {
	private final boolean allowFertilization;
	private final boolean damagePlayer;
	private final boolean dropItemSourceOnGrassBreak;
	private final boolean ignoreLight;

	public SproutVariableHolder() {
		this(true, false, true, false);
	}

	public SproutVariableHolder(final boolean allowsFertilization, final boolean damagePlayer, final boolean dropItemSourceOnGrassBreak, final boolean ignoreLight) {
		this.allowFertilization = allowsFertilization;
		this.damagePlayer = damagePlayer;
		this.dropItemSourceOnGrassBreak = dropItemSourceOnGrassBreak;
		this.ignoreLight = ignoreLight;
	}

	@Override
	public boolean allowFertilization() {
		return allowFertilization;
	}

	@Override
	public boolean damagePlayer() {
		return damagePlayer;
	}

	@Override
	public boolean dropItemSourceOnGrassBreak() {
		return dropItemSourceOnGrassBreak;
	}

	@Override
	public boolean ignoreLight() {
		return ignoreLight;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SproutVariableHolder)) {
			return false;
		}

		final SproutVariableHolder other = (SproutVariableHolder) obj;
		return other.allowFertilization == allowFertilization && other.damagePlayer == damagePlayer && other.dropItemSourceOnGrassBreak == dropItemSourceOnGrassBreak && other.ignoreLight == ignoreLight;
	}

	@Override
	public String toString() {
		return "Variables{allowsFertilization= " + allowFertilization + ", damagePlayer= " + damagePlayer + ", dropItemSourceOnGrassBreak= " + dropItemSourceOnGrassBreak + ", ignoreLight= " + ignoreLight + "}";
	}
}
