package net.runelite.client.plugins.guide;

import javaGOAP.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Deque;
import net.runelite.api.Client;
import net.runelite.api.Skill;

import java.util.*;

@Slf4j
public class RunePlanner implements IGoapPlanner {

    GoapState goalState;
    GraphNode startNode;
    List<GraphNode> endNodes;
    Queue<GoapAction> createdPlan;
    @Override
    public Queue<GoapAction> plan(IGoapUnit goapUnit) {
        log.warn("initializing planner.");
        RuneUnit runit = (RuneUnit) goapUnit;
        Observer obs = runit.obChar;
        createdPlan = new PriorityQueue<GoapAction>();
        List<GoapState> goalState = goapUnit.getGoalState();
        HashSet<GoapState> worldState = goapUnit.getWorldState();
        HashSet<GoapAction> actions = goapUnit.getAvailableActions();
        Hashtable<String, Skill> strToSkill = new Hashtable<String, Skill>();

        strToSkill.put("ATTACK", Skill.ATTACK);
        strToSkill.put("STRENGTH", Skill.STRENGTH);
        strToSkill.put("DEFENCE", Skill.DEFENCE);
        strToSkill.put("HITPOINTS", Skill.HITPOINTS);
        strToSkill.put("MAGIC", Skill.MAGIC);




        // for each item in the goal table, get the world state, then add
        // actions that move the world state towards the goal of the plan


        Integer currentLevel = runit.obChar.client.getRealSkillLevel(Skill.ATTACK);
        Integer goalLevel;
        Skill currentSkill;
        // System.out.println(currentLevel);
        if(!goalState.isEmpty()) {
            log.warn("GoalState is not empty");
            for (GoapState state : goalState) {
                goalLevel = (Integer) state.value;
                currentSkill = strToSkill.get(state.effect);
                for (GoapAction action : actions) {
                    CheckStat caction = (CheckStat) action;
                    if (currentLevel < goalLevel && caction.skill == currentSkill) {
                        createdPlan.add(action);
                    }
                }
            }
            sort();
        }

        return createdPlan;
    }

    public Boolean checkPreconditions(GoapAction check, Hashtable<String, Integer> worldState) {
        HashSet<GoapState> preconditions = check.getPreconditions();
        ArrayList<Boolean> finalCheck = new ArrayList<Boolean>();
        Boolean finalBool = false;

        for (GoapState g : preconditions) {
            if ((Integer)g.value == worldState.get(g.effect)) {
                finalCheck.add(true);
            } else {
                finalCheck.add(false);
            }
        }

        for (Boolean b : finalCheck) {
            if (!b) {
                finalBool = false;
                break;
            }
            finalBool = true;
        }
        return finalBool;
    }

    public void sort() {
        ArrayList<GoapAction> copy = new ArrayList<GoapAction>();
        for (GoapAction g: this.createdPlan) {
            copy.add(g);
        }
        copy.sort(null);
        this.createdPlan = new PriorityQueue<GoapAction>();
        for (GoapAction g : copy) {
            this.createdPlan.add(g);
        }
    }
}

