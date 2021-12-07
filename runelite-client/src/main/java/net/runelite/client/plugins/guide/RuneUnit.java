package net.runelite.client.plugins.guide;

import java.util.*;

import javaGOAP.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;

@Slf4j
public class RuneUnit extends GoapUnit {


    Observer obChar;
    Queue<GoapAction> unitPlan;
    Queue<Skill> priorityQueue;
    Hashtable<String, Integer> metadata;
    public Hashtable<Skill, Integer> goalStatTable;
    Boolean isPlanFound = false;

    public RuneUnit(Observer obs, Hashtable<Skill, Integer> goalStatTable, Client c) {
        this.obChar = obs;
        this.goalStatTable = goalStatTable;
        obChar.setStats();
        initWorldState(obChar.pc);
        initGoalState(goalStatTable);
        initActions();
    }

    private void initWorldState(Character character) {
        for (Skill s : Skill.values()) {
            this.addWorldState(new GoapState(-1, s.toString(), character.levels.get(s)));
        }
    }

    public void updateWorldState(Character character) {
        HashSet<GoapState> copy = new HashSet<GoapState>();
        for (GoapState state : getWorldState()) {
            copy.add(state);
        }
        if (getWorldState() != null) {
            log.info("Length: " + copy.size());
            for (int i = 0; i < copy.size(); i++) {
                // log.info(state.toString());
                this.removeWorldState((GoapState)copy.toArray()[i]);
            }
            initWorldState(character);
        }
    }

    public void initGoalState(Hashtable<Skill, Integer> goalStatTable) {
        this.goalStatTable = goalStatTable;
        if (this.goalStatTable != null) {
            for (Skill s : Skill.values()) {
                if (goalStatTable.get(s) != this.obChar.pc.levels.get(s) &&
                        goalStatTable.get(s) != null) {
                    this.addGoalState(new GoapState(10, s.toString(), goalStatTable.get(s)));
                }
            }
        }
    }

    public void initActions() {
        for (Skill s : Skill.values()) {
            if (goalStatTable.get(s) != null) {
                for (int i = obChar.pc.levels.get(s) + 1; i <= goalStatTable.get(s); i++) {
                    this.addAvailableAction(new CheckStat(this.obChar.pc, s, i));
                }
            }
        }
    }

    public void updateActions() {
        HashSet<GoapAction> copy = new HashSet<GoapAction>();
        for (GoapAction g : getAvailableActions()) {
            copy.add(g);
        }
        for (GoapAction g : copy) {
            removeAvailableAction(g);
        }
        updateObserver();
        initActions();
    }

    public void clearGoalState() {
        List<GoapState> copy = new ArrayList<GoapState>();
        for (GoapState g : getGoalState()) {
            copy.add(g);
        }
        for (GoapState g : copy) {
            this.removeGoalState(g.effect);
        }
    }

    public void updateObserver() {
        this.obChar.setStats();
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
        System.out.println("sizeof plan: " + this.unitPlan.size());
    }

    @Override
    public void goapPlanFound(Queue<GoapAction> plan) {
        unitPlan = plan;
        isPlanFound = true;
        System.out.println("Plan Found");
        for (GoapAction action : this.unitPlan) {
            System.out.println(action);
        }
        System.out.println("sizeof plan: " + this.unitPlan.size());
    }

    public Boolean planFound() { return isPlanFound; }

    @Override
    public boolean moveTo(Object arg0) {
        // Changes the FSM directly
        return false;
    }

    @Override
    public void update() {
        // Called by the Agent.update() method
        this.obChar.setStats();
        updateWorldState(this.obChar.pc);
    }

    public void setStats(Character c) {
        this.obChar.pc = c;
    }

    public void setStats(Character c, Hashtable<String, Integer> metadata) {
        this.obChar.pc = c;
        this.metadata = metadata;
    }
}