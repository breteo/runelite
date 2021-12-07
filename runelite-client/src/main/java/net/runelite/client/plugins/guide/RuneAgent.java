package net.runelite.client.plugins.guide;

import javaGOAP.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;

import java.util.Hashtable;

@Slf4j
public class RuneAgent extends GoapAgent {


    public RuneAgent(Client client, Hashtable<Skill, Integer> gskills) {
        super(new RuneUnit(new Observer(new Character(), client), gskills, client));
    }

    @Override
    protected IGoapPlanner generatePlannerObject() {
        System.out.println("New Planner");
        return new RunePlanner();
    }

    public void updatePlan(Hashtable<Skill, Integer> updated_gskills) {
        // update goal state
        log.info("Updating Plan");
        ((RuneUnit)this.assignedGoapUnit).clearGoalState();
        ((RuneUnit)this.assignedGoapUnit).initGoalState(updated_gskills);
        // update actions
        ((RuneUnit)this.assignedGoapUnit).updateActions();
        this.idleState = new IdleState(this.generatePlannerObject());
    }

    public void updateObserver() {
        ((RuneUnit)this.assignedGoapUnit).updateObserver();
    }

    public String fsmTop() {
        if (fsm.top instanceof RunActionState && ((RunActionState) fsm.top).currentActions.peek() != null)
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
