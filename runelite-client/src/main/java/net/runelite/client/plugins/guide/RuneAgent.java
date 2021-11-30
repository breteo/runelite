package net.runelite.client.plugins.guide;

import javaGOAP.GoapAgent;
import javaGOAP.IGoapPlanner;
import javaGOAP.IGoapUnit;

public class RuneAgent extends GoapAgent{

    public RuneAgent(IGoapUnit assignedUnit) {
        super(assignedUnit);
    }
    @Override
    protected IGoapPlanner generatePlannerObject() {
        return new RunePlanner();
    }
}
