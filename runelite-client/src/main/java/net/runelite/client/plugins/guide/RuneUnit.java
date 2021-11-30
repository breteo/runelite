package net.runelite.client.plugins.guide;

import java.util.Hashtable;
import java.util.Queue;
import javaGOAP.*;
import net.runelite.api.Skill;


public class RuneUnit extends GoapUnit {

    Character unitChar;
    Queue<GoapAction> unitPlan;
    Queue<Skill> priorityQueue;
    public RuneUnit(Character character) {
        this.unitChar = character;
        initWorldState(character);
        initActions();
    }

    private void initWorldState(Character character) {
        for (Skill s : Skill.values()) {
            this.addWorldState(new GoapState(-1, s.toString(), character.levels.get(s)));
        }
    }

    public void updateWorldState(Character character) {
        for(GoapState state : getWorldState()) {
            this.removeWorldState(state);
        }
        initWorldState(character);
    }

    public void initGoalState(Hashtable<Skill, Integer> goalStatTable) {
        for (Skill s : Skill.values()) {
            if (goalStatTable.get(s) != this.unitChar.levels.get(s) &&
                goalStatTable.get(s) != null) {
                this.addGoalState(new GoapState(10, s.toString(), goalStatTable.get(s)));
            }
        }
    }

    public void initActions() {
        for (Skill s : Skill.values()) {
            this.addAvailableAction(new CheckStat(this.unitChar, s, null));
        }
    }

    @Override
    public void goapPlanFailed(Queue<GoapAction> arg0) {
        System.out.println("Plan Failed.");
    }

    public Queue<GoapAction> putGoapPlan(Queue<GoapAction> plan) {
        return plan;
    }

    @Override
    public void goapPlanFinished() {
        System.out.println("Plan Finished.");
    }

    @Override
    public void goapPlanFound(Queue<GoapAction> plan) {
        unitPlan = putGoapPlan(plan);
        System.out.println("Plan Found");
    }

    @Override
    public boolean moveTo(Object arg0) {
        // Changes the FSM directly
        return false;
    }

    @Override
    public void update() {
        // Called by the Agent.update() method

    }

    public void setStats(Character c) {
        this.unitChar = c;
    }
}