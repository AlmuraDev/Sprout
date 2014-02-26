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
package com.almuradev.sprout.plugin.io.table;

import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

@Table ("sprouts")
public class Sprouts {
    @Id
    private int id;
    @Field
    private String world;
    @Field
    private long location;
    @Field
    private String sprout;
    @Field
    private int age;
    @Field
    private boolean stillGrowing;

    public Sprouts() {
    }

    public Sprouts(String world, long location, String sprout, int age, boolean stillGrowing) {
        this.world = world;
        this.location = location;
        this.sprout = sprout;
        this.age = age;
        this.stillGrowing = stillGrowing;
    }

    public int getId() {
        return id;
    }

    public String getWorld() {
        return world;
    }

    public long getLocation() {
        return location;
    }

    public String getSprout() {
        return sprout;
    }

    public void setSprout(String sprout) {
        this.sprout = sprout;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isStillGrowing() {
        return stillGrowing;
    }

    public void setStillGrowing(boolean stillGrowing) {
        this.stillGrowing = stillGrowing;
    }
}
