package com.almuradev.sprout.plugin.task;

import com.almuradev.sprout.api.crop.Sprout;
import com.almuradev.sprout.api.util.Int21TripleHashed;
import com.almuradev.sprout.plugin.crop.SimpleSprout;

public class LocatableSprout {
	private final long location;
	private final Sprout sprout;

	public LocatableSprout(final long location, final SimpleSprout sprout) {
		this.location = location;
		this.sprout = sprout;
	}

	public LocatableSprout(final int x, final int y, final int z, final SimpleSprout sprout) {
		this(Int21TripleHashed.key(x, y, z), sprout);
	}

	public long getLocation() {
		return location;
	}

	public Sprout getSprout() {
		return sprout;
	}
}
