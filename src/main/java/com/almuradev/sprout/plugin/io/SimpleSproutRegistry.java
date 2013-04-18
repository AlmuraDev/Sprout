package com.almuradev.sprout.plugin.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.almuradev.sprout.api.io.SproutRegistry;
import com.almuradev.sprout.crop.Sprout;

public class SimpleSproutRegistry implements SproutRegistry {
	private final List<Sprout> sprouts = new ArrayList<>();

	@Override
	public Sprout add(Sprout sprout) {
		if (sprout == null) {
			throw new IllegalArgumentException("Sprout is null!");
		}
		sprouts.add(sprout);
		return sprout;
	}

	@Override
	public boolean contains(String identifier) {
		for (Sprout sprout : sprouts) {
			if (sprout.getIdentifier().equals(identifier)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Sprout get(String identifier) {
		for (Sprout sprout : sprouts) {
			if (sprout.getIdentifier().equals(identifier)) {
				return sprout;
			}
		}
		return null;
	}

	@Override
	public Sprout remove(String identifier) {
		if (identifier == null || identifier.isEmpty()) {
			throw new IllegalArgumentException("Identifier is null or empty!");
		}

		final Iterator<Sprout> sproutIterator = sprouts.iterator();
		Sprout toRemove = null;
		while (sproutIterator.hasNext()) {
			toRemove = sproutIterator.next();
			if (toRemove.getIdentifier().equals(identifier)) {
				sproutIterator.remove();
			}
		}
		return toRemove;
	}

	@Override
	public Collection<Sprout> getAll() {
		return Collections.unmodifiableCollection(sprouts);
	}
}
