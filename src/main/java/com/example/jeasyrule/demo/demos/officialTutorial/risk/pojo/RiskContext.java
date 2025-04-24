package com.example.jeasyrule.demo.demos.officialTutorial.risk.pojo;

import java.util.EnumSet;
import java.util.Set;


public class RiskContext {
    private final Set<Behavior> behaviors = EnumSet.noneOf(Behavior.class);
    private final Set<RiskFeature> features = EnumSet.noneOf(RiskFeature.class);
    private RiskLevel level = null;
    private final Set<Action> actions = EnumSet.noneOf(Action.class);

    // getters
    public Set<Behavior> getBehaviors() {
        return behaviors;
    }

    public Set<RiskFeature> getFeatures() {
        return features;
    }

    public RiskLevel getLevel() {
        return level;
    }

    public Set<Action> getActions() {
        return actions;
    }

    // setters
    public void setLevel(RiskLevel level) {
        this.level = level;
    }
}
