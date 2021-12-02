package net.runelite.client.plugins.guide;

import javaGOAP.DefaultGoapPlanner;
import javaGOAP.GoapAgent;
import javaGOAP.IGoapPlanner;
import javaGOAP.IGoapUnit;

import java.util.Hashtable;

public class RuneAgent extends GoapAgent {


    public RuneAgent(RuneUnit assignedUnit) {
        super(assignedUnit);
    }

    @Override
    protected IGoapPlanner generatePlannerObject() {
        return new DefaultGoapPlanner();
    }

    public RuneUnit getUnit() { return (RuneUnit) this.getAssignedGoapUnit(); }

    public void updateUnit(Character c, Hashtable<String, Integer> metadata) {
        RuneUnit rUnit = (RuneUnit) this.assignedGoapUnit;
        rUnit.setStats(c);
    }


}
