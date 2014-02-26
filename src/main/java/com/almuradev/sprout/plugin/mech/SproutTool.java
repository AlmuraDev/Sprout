package com.almuradev.sprout.plugin.mech;

import com.almuradev.sprout.api.mech.Tool;

public class SproutTool implements Tool {
    private final String name;
    private final boolean isRequired;
    private final boolean isBonus;
    private final int bonusAmount;

    public SproutTool(String name, boolean isRequired, boolean isBonus, int bonusAmount) {
        this.name = name;
        this.isRequired = isRequired;
        this.isBonus = isBonus;
        this.bonusAmount = bonusAmount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isRequired() {
        return isRequired;
    }

    @Override
    public boolean isBonus() {
        return isBonus;
    }

    @Override
    public int getBonusAmount() {
        return bonusAmount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SproutTool)) {
            return false;
        }

        final SproutTool other = (SproutTool) obj;
        return other.getName().equals(name);
    }

    @Override
    public String toString() {
        return "Tool{name= " + name + ", isRequired= " + isRequired + ", isBonus= " + isBonus + ", bonusAmount= " + bonusAmount + "}";
    }
}
