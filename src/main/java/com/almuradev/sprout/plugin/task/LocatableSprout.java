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
package com.almuradev.sprout.plugin.task;

import com.almuradev.sprout.api.crop.Sprout;
import com.almuradev.sprout.api.util.Int21TripleHashed;
import com.almuradev.sprout.plugin.crop.SimpleSprout;

public class LocatableSprout {
    private final long location;
    private final Sprout sprout;

    public LocatableSprout(final long location, final SimpleSprout sprout) {
        this.location = location;
        this.sprout = sprout;
    }

    public LocatableSprout(final int x, final int y, final int z, final SimpleSprout sprout) {
        this(Int21TripleHashed.key(x, y, z), sprout);
    }

    public long getLocation() {
        return location;
    }

    public Sprout getSprout() {
        return sprout;
    }
}
