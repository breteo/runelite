package net.runelite.client.plugins.guide;

import javaGOAP.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

public class RunePlanner implements IGoapPlanner {

    GoapState goalState;
    IGoapUnit goapUnit;
    GraphNode startNode;
    List<GraphNode> endNodes;
    @Override
    public Queue<GoapAction> plan(IGoapUnit goapUnit) {

        Queue<GoapAction> createdPlan = null;
        this.goapUnit = goapUnit;
        this.startNode = new GraphNode(null);
        this.endNodes = new ArrayList<GraphNode>();

        try {
            sortGoalStates();
        } catch(Exception e) {}

        return null;
    }


    protected List<GoapState> sortGoalStates() {
        if (this.goapUnit.getGoalState().size() > 1) {
            this.goapUnit.getGoalState().sort(new Comparator<GoapState>() {
                 @Override
                public int compare(GoapState o1, GoapState o2) {
                        return o2.importance.compareTo(o1.importance);
                    }
            });
        }
        return this.goapUnit.getGoalState();
    }
}

