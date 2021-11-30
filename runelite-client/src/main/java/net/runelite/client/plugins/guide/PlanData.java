package net.runelite.client.plugins.guide;

import javaGOAP.GoapAgent;
import javaGOAP.GoapState;
import javaGOAP.GoapUnit;
import javaGOAP.IGoapUnit;
import net.runelite.api.Skill;

import java.util.Hashtable;
import java.util.ArrayList;

public class PlanData {

    GoapAgent planAgent;
    GoapUnit unit;
    ArrayList<String> totalWorldState = new ArrayList<String>();
    ArrayList<String> totalGoalState = new ArrayList<String>();

    public PlanData(GoapUnit unit) {
        this.unit = unit;

        for(GoapState s : unit.getGoalState()) {
            String str = s.toString();
            totalGoalState.add(str);
        }

        for(GoapState s : unit.getWorldState()) {
            String str = s.toString();
            totalWorldState.add(str);
        }
    }

    public String getData() {
        StringBuilder dataFormatter = new StringBuilder("");
        dataFormatter.append("WORLD STATE: \n");
        for (String str : totalWorldState) {
            dataFormatter.append(str + "\n");
        }
        dataFormatter.append("GOAL STATE: \n");
        for (String str : totalGoalState) {
            dataFormatter.append(str + "\n");
        }
        return dataFormatter.toString();
    }
}
