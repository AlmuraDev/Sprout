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
package com.almuradev.sprout.api.io;

import java.util.Collection;

import com.almuradev.sprout.api.crop.Sprout;

public interface SproutRegistry {
    public Sprout add(Sprout sprout);

    public SproutRegistry addAll(Collection<Sprout> sprouts);

    public boolean contains(String name);

    public Sprout find(String itemSource);

    public Sprout get(int index);

    public Sprout get(String name);

    public int size();

    public Sprout remove(String name);

    public Collection<Sprout> getAll();
}
