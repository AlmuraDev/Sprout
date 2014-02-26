package com.almuradev.sprout.api.mech;

public interface Tool {
    public String getName();

    public boolean isRequired();

    public boolean isBonus();

    public int getBonusAmount();
}
