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
