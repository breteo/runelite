package net.runelite.client.plugins.guide;

import java.util.Hashtable;
import java.util.Queue;
import javaGOAP.*;
import net.runelite.api.Skill;


public class RuneUnit extends GoapUnit {

    Character unitChar;
    Queue<GoapAction> unitPlan;
    Queue<Skill> priorityQueue;
    Hashtable<String, Integer> metadata;
    Hashtable<Skill, Integer> goalStatTable;
    public RuneUnit(Character character, Hashtable<Skill, Integer> goalStatTable) {
        this.unitChar = character;
        this.goalStatTable = goalStatTable;
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
            try {
                this.removeWorldState(state);
            } catch (Exception e) {
                System.out.println("OOPS...");
            }
        }
        initWorldState(character);
    }

    public void initGoalState(Hashtable<Skill, Integer> goalStatTable) {
        this.goalStatTable = goalStatTable;
        for (Skill s : Skill.values()) {
            if (goalStatTable.get(s) != this.unitChar.levels.get(s) &&
                goalStatTable.get(s) != null) {
                this.addGoalState(new GoapState(10, s.toString(), goalStatTable.get(s)));
            }
        }
    }

    public void initActions() {
        for (Skill s : Skill.values()) {
            if (goalStatTable.get(s) != null) {
                for (int i = unitChar.levels.get(s) + 1; i <= goalStatTable.get(s); i++) {
                    this.addAvailableAction(new CheckStat(this.unitChar, s, i));
                }
            }
        }
    }

    @Override
    public void goapPlanFailed(Queue<GoapAction> arg0) {
        System.out.println("Plan Failed.");
    }

    public Queue<GoapAction> putGoapPlan() {
        return this.unitPlan;
    }

    @Override
    public void goapPlanFinished() {
        System.out.println("Plan Finished.");
    }

    @Override
    public void goapPlanFound(Queue<GoapAction> plan) {
        unitPlan = plan;
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

    public void setStats(Character c, Hashtable<String, Integer> metadata) {
        this.unitChar = c;
        this.metadata = metadata;
    }
}