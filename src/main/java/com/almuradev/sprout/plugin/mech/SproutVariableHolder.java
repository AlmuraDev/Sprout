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
	private final boolean dropItemSourceOnGrassBreak;

	public SproutVariableHolder() {
		this(true, true);
	}

	public SproutVariableHolder(final boolean allowsFertilization, final boolean dropItemSourceOnGrassBreak) {
		this.allowFertilization = allowsFertilization;
		this.dropItemSourceOnGrassBreak = dropItemSourceOnGrassBreak;
	}

	@Override
	public boolean allowFertilization() {
		return allowFertilization;
	}

	@Override
	public boolean dropItemSourceOnGrassBreak() {
		return dropItemSourceOnGrassBreak;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SproutVariableHolder)) {
			return false;
		}

		final SproutVariableHolder other = (SproutVariableHolder) obj;
		return other.allowFertilization == allowFertilization && other.dropItemSourceOnGrassBreak == dropItemSourceOnGrassBreak;
	}

	@Override
	public String toString() {
		return "Variables{allowsFertilization= " + allowFertilization + ", dropItemSourceOnGrassBreak= " + dropItemSourceOnGrassBreak + "}";
	}
}
