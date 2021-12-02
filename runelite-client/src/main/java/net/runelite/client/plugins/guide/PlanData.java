package net.runelite.client.plugins.guide;

import javaGOAP.*;
import net.runelite.api.Skill;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Queue;

public class PlanData {

    GoapAgent planAgent;
    RuneUnit unit;
    ArrayList<String> totalWorldState = new ArrayList<String>();
    ArrayList<String> totalGoalState = new ArrayList<String>();
    ArrayList<String> actions = new ArrayList<String>();
    ArrayList<String> plan = new ArrayList<String>();
    Hashtable<String, Boolean> options;
    public PlanData(RuneUnit unit, GoapAgent planAgent, Hashtable<String, Boolean> opts) {
        this.unit = unit;
        this.planAgent = planAgent;
        options = opts;

        for(GoapState s : unit.getGoalState()) {
            String str = s.toString();
            totalGoalState.add(str);
        }

        for(GoapState s : unit.getWorldState()) {
            String str = s.toString();
            totalWorldState.add(str);
        }

        for(GoapAction a : unit.getAvailableActions()) {
            String str = a.toString();
            actions.add(str);
        }

        if (planAgent.getPlanner() != null) {
            Queue<GoapAction> queue = planAgent.getPlan();
            for (GoapAction a : queue) {
                String str = a.toString();
                plan.add(str);
            }
        }
    }

    public String getData() {
        StringBuilder dataFormatter = new StringBuilder("");

        if (options.get("worldState")) {
            dataFormatter.append("WORLD STATE: \n");
            for (String str : totalWorldState) {
                dataFormatter.append(str + "\n");
            }
        }

        if (options.get("goalState")) {
            dataFormatter.append("GOAL STATE: \n");
            for (String str : totalGoalState) {
                dataFormatter.append(str + "\n");
            }
        }

        if (options.get("actionPlan")) {
            dataFormatter.append("PLAN: \n");
            if (plan != null) {
                for (String str : plan) {
                    dataFormatter.append(str + "\n");
                }
            }
        }

        if (options.get("availableActions")) {
            dataFormatter.append("AVAILABLE ACTIONS: \n");
            for (String str : actions) {
                dataFormatter.append(str + "\n");
            }
        }

        return dataFormatter.toString();
    }
}
