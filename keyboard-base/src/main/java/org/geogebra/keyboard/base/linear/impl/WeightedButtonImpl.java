package org.geogebra.keyboard.base.linear.impl;

import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.ResourceType;
import org.geogebra.keyboard.base.impl.ButtonImpl;
import org.geogebra.keyboard.base.linear.WeightedButton;

public class WeightedButtonImpl extends ButtonImpl implements WeightedButton {

    private float weight;

    public WeightedButtonImpl(String resourceName, ResourceType resourceType, String actionName, ActionType actionType, float weight) {
        super(resourceName, resourceType, actionName, actionType);
        this.weight = weight;
    }

    @Override
    public float getWeight() {
        return weight;
    }
}
