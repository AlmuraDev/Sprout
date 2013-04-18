package com.almuradev.sprout.api.io;

import java.util.Collection;

import com.almuradev.sprout.crop.Sprout;

public interface SproutRegistry {
	public Sprout add(Sprout sprout);

	public boolean contains(String identifier);

	public Sprout get(String identifier);

	public Sprout remove(String identifier);

	public Collection<Sprout> getAll();
}
