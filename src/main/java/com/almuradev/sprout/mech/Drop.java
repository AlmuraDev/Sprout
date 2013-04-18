package com.almuradev.sprout.mech;

import java.io.Serializable;

public interface Drop extends Serializable {
    public String getIdentifier();

    public int getAmount();
}
