package net.runelite.client.plugins.guide;

import javaGOAP.*;
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

    public String fsmTop() {
        if (fsm.top instanceof RunActionState)
            return "Action: " + ((RunActionState) fsm.top).currentActions.peek().toString();
        else if (fsm.top instanceof MoveToState)
            return "MoveToState";
        else if (fsm.top instanceof IdleState)
            return "IdleState";
        else
            return "This should not be reached";
    }

    public RuneUnit getUnit() { return (RuneUnit) this.getAssignedGoapUnit(); }

}
