package com.almuradev.sprout.plugin.io;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.almuradev.sprout.api.Sprout;
import com.almuradev.sprout.api.io.Registry;
import com.almuradev.sprout.api.util.TInt21TripleObjectHashMap;

public class SimpleRegistry implements Registry {
	private static final HashMap<String, TInt21TripleObjectHashMap> REGISTRIES = new HashMap<>();

	@Override
	public Sprout add(String world, int x, int y, int z, Sprout sprout) {
		if (world == null || world.isEmpty()) {
			throw new NullPointerException("Specified world is null!");
		}
		TInt21TripleObjectHashMap REGISTRY = REGISTRIES.get(world);
		if (REGISTRY == null) {
			REGISTRY = new TInt21TripleObjectHashMap();
			REGISTRIES.put(world, REGISTRY);
		}
		return (Sprout) REGISTRY.put(x, y, z, sprout);
	}

	@Override
	public boolean contains(String world, int x, int y, int z) {
		if (world == null || world.isEmpty()) {
			throw new NullPointerException("Specified world is null!");
		}
		final TInt21TripleObjectHashMap REGISTRY = REGISTRIES.get(world);
		if (REGISTRY == null) {
			return false;
		}
		return REGISTRY.containsKey(x, y, z);
	}

	@Override
	public Sprout get(String world, int x, int y, int z) {
		if (world == null || world.isEmpty()) {
			throw new NullPointerException("Specified world is null!");
		}
		final TInt21TripleObjectHashMap REGISTRY = REGISTRIES.get(world);
		if (REGISTRY == null) {
			return null;
		}
		return (Sprout) REGISTRY.get(x, y, z);
	}

	@Override
	public Sprout remove(String world, int x, int y, int z) {
		if (world == null || world.isEmpty()) {
			throw new NullPointerException("Specified world is null!");
		}
		final TInt21TripleObjectHashMap REGISTRY = REGISTRIES.get(world);
		if (REGISTRY == null) {
			return null;
		}
		return (Sprout) REGISTRY.remove(x, y, z);
	}

	@Override
	public TInt21TripleObjectHashMap getRegistryFor(String world) {
		return REGISTRIES.get(world);
	}

	@Override
	public Map<String, TInt21TripleObjectHashMap> getAll() {
		return Collections.unmodifiableMap(REGISTRIES);
	}
}
