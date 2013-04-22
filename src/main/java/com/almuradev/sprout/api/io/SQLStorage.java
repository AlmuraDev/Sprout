package com.almuradev.sprout.api.io;

import com.almuradev.sprout.api.crop.Sprout;

public interface SQLStorage {
	public SQLStorage add(String world, int x, int y, int z, Sprout sprout);

	public SQLStorage remove(String world, int x, int y, int z, Sprout sprout);

	public SQLStorage clear(String world);
}
