package com.almuradev.sprout.plugin.mech;

import com.almuradev.sprout.mech.Drop;

public class SproutDrop implements Drop {

    private final String identifier;
    private final int amount;

    public SproutDrop(String identifier, int amount) {
        this.identifier = identifier;
        this.amount = amount;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public int getAmount() {
        return amount;
    }
}
