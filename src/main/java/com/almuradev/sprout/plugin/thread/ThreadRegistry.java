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

import java.util.LinkedList;
import java.util.List;

public class ThreadRegistry {
	private static final List<Thread> THREADS = new LinkedList<>();

	public ThreadRegistry() {

	}

	public static void add(Thread thread) {
		THREADS.add(thread);
	}

	public static Thread get(final String world) {
		for (final Thread thread : THREADS) {
			if (thread.getName().equalsIgnoreCase("Save Thread - " + world)) {
				return thread;
			}
		}
		return null;
	}

	public static void remove(final String world) {
		for (final Thread thread : THREADS) {
			if (thread.getName().equalsIgnoreCase("Save Thread - " + world) && !thread.isInterrupted()) {
				thread.interrupt();
				THREADS.remove(thread);
			}
		}
	}

	public static void start(final String world) {
		final Thread thread = get(world);
		if (thread == null) {
			return;
		}
		if (!thread.isAlive()) {
			thread.start();
		}
	}
}
