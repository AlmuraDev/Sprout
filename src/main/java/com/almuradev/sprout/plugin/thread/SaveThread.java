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

import com.almuradev.sprout.api.util.Int21TripleHashed;
import com.almuradev.sprout.plugin.SproutPlugin;
import com.almuradev.sprout.plugin.crop.SimpleSprout;
import com.almuradev.sprout.plugin.io.SimpleSQLStorage;
import com.almuradev.sprout.plugin.task.LocatableSprout;

import org.bukkit.Location;

public class SaveThread extends Thread {
	public final LinkedBlockingDeque<LocatableSprout> ADD = new LinkedBlockingDeque<>();
	public final LinkedBlockingDeque<LocatableSprout> REMOVE = new LinkedBlockingDeque<>();
	final SproutPlugin plugin;
	final String world;

	public SaveThread(final SproutPlugin plugin, final String world) {
		super("Save Thread - " + world);
		this.plugin = plugin;
		this.world = world;
		setDaemon(true);
	}

	@Override
	public void run() {
		while (!this.isInterrupted()) {
			try {
				flush();
			} catch (InterruptedException ignore) {
				return;
			}
		}
	}

	public void add(final Location location, final SimpleSprout sprout) {
		if (location == null || sprout == null) {
			return;
		}
		final LocatableSprout dispersed = new LocatableSprout(Int21TripleHashed.key(location.getBlockX(), location.getBlockY(), location.getBlockZ()), sprout);
		if (ADD.contains(dispersed)) {
			ADD.remove(dispersed);
		}
		ADD.offer(dispersed);
	}

	public void add(final int x, final int y, final int z, final SimpleSprout sprout) {
		if (sprout == null) {
			return;
		}
		final LocatableSprout dispersed = new LocatableSprout(x, y, z, sprout);
		if (ADD.contains(dispersed)) {
			ADD.remove(dispersed);
		}
		ADD.offer(dispersed);
	}

	public void flush() throws InterruptedException {
		LocatableSprout flush;
		if (!ADD.isEmpty()) {
			flush = ADD.take();
			if (flush != null && !REMOVE.contains(flush)) {
				((SimpleSQLStorage) plugin.getStorage()).add(world, flush.getLocation(), flush.getSprout());
			}
		}
		if (!REMOVE.isEmpty()) {
			flush = REMOVE.take();
			if (flush != null) {
				((SimpleSQLStorage) plugin.getStorage()).remove(world, flush.getLocation());
			}
		}
		
		if (ADD.isEmpty() && REMOVE.isEmpty()) {			
			SaveThread.sleep(10000);
		}
	}

	public void remove(final Location location, final SimpleSprout sprout) {
		if (location == null || sprout == null) {
			return;
		}
		final LocatableSprout dispersed = new LocatableSprout(location.getBlockX(), location.getBlockY(), location.getBlockZ(), sprout);
		if (REMOVE.contains(dispersed)) {
			return;
		}
		REMOVE.offer(dispersed);
	}

	public void remove(long location, final SimpleSprout sprout) {
		if (sprout == null) {
			return;
		}
		final LocatableSprout dispersed = new LocatableSprout(location, sprout);
		if (REMOVE.contains(dispersed)) {
			return;
		}
		REMOVE.offer(dispersed);
	}

	public void clear() {
		ADD.clear();
	}
}
