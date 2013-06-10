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
package com.almuradev.sprout.plugin.thread;

import java.util.concurrent.LinkedBlockingDeque;

import com.almuradev.sprout.plugin.SproutPlugin;
import com.almuradev.sprout.plugin.io.SimpleSQLStorage;
import com.almuradev.sprout.plugin.task.GrowthTask;

public class SaveThread extends Thread {
	public final LinkedBlockingDeque<GrowthTask.SproutInfo> QUEUE = new LinkedBlockingDeque();
	final SproutPlugin plugin;
	final String world;

	public SaveThread(final SproutPlugin plugin, final String world) {
		super ("Save Thread - " + world);
		setDaemon(true);
		this.plugin = plugin;
		this.world = world;
	}

	@Override
	public void run() {
		while (!this.isInterrupted()) {
			try {
				final GrowthTask.SproutInfo sprout = QUEUE.take();
				((SimpleSQLStorage) plugin.getStorage()).add(world, sprout.getLocation(), sprout.getSprout());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
