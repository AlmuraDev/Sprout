package com.almuradev.sprout.api.io;

import java.util.Map;

import com.almuradev.sprout.api.Sprout;
import com.almuradev.sprout.api.util.TInt21TripleObjectHashMap;

public interface Registry {
	public Sprout add(String world, int x, int y, int z, Sprout sprout);

	public boolean contains(String world, int x, int y, int z);

	public Sprout get(String world, int x, int y, int z);

	public Sprout remove(String world, int x, int y, int z);

	public TInt21TripleObjectHashMap getRegistryFor(String world);

	public Map<String, TInt21TripleObjectHashMap> getAll();
}
