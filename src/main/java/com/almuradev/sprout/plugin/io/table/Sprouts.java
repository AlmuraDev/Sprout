package com.almuradev.sprout.plugin.io.table;

import com.almuradev.sprout.api.crop.Sprout;
import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;

@Table("sprouts")
public class Sprouts {
	@Id
	private long key;

	@Field
	private String world;

	@Field
	private Sprout sprout;

	public Sprouts() {

	}

	public Sprouts(String world, Sprout sprout) {
		this.world = world;
		this.sprout = sprout;
	}

	public long getKey() {
		return key;
	}

	public String getWorld() {
		return world;
	}

	public Sprout getSprout() {
		return sprout;
	}

	public void setSprout(Sprout sprout) {
		this.sprout = sprout;
	}

	public void setWorld(String world) {
		this.world = world;
	}
}
