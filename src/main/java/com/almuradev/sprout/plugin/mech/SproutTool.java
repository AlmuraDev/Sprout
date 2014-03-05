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

import com.almuradev.sprout.api.mech.Tool;

public class SproutTool implements Tool {
    private final String name;
    private final boolean isRequired;
    private final boolean isBonus;
    private final int bonusAmount;

    public SproutTool(String name, boolean isRequired, boolean isBonus, int bonusAmount) {
        this.name = name;
        this.isRequired = isRequired;
        this.isBonus = isBonus;
        this.bonusAmount = bonusAmount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isRequired() {
        return isRequired;
    }

    @Override
    public boolean isBonus() {
        return isBonus;
    }

    @Override
    public int getBonusAmount() {
        return bonusAmount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SproutTool)) {
            return false;
        }

        final SproutTool other = (SproutTool) obj;
        return other.getName().equals(name);
    }

    @Override
    public String toString() {
        return "Tool{name= " + name + ", isRequired= " + isRequired + ", isBonus= " + isBonus + ", bonusAmount= " + bonusAmount + "}";
    }
}
