package net.runelite.client.plugins.guide;

import javaGOAP.DefaultGoapPlanner;
import javaGOAP.GoapAgent;
import javaGOAP.IGoapPlanner;
import javaGOAP.IGoapUnit;
import net.runelite.api.Client;
import net.runelite.api.Skill;

import java.util.Hashtable;

public class RuneAgent extends GoapAgent {


    public RuneAgent(Client client, Hashtable<Skill, Integer> gskills) {
        super(new RuneUnit(new Observer(new Character(), client), gskills, client));
    }

    @Override
    protected IGoapPlanner generatePlannerObject() {
        return new RunePlanner();
    }

    public void updateObserver() {
        ((RuneUnit)this.assignedGoapUnit).updateObserver();
    }

    public RuneUnit getUnit() { return (RuneUnit) this.getAssignedGoapUnit(); }

}
