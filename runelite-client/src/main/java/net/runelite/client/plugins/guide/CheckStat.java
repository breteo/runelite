package net.runelite.client.plugins.guide;

import javaGOAP.GoapAction;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import net.runelite.api.Skill;

import java.util.HashSet;

public class CheckStat extends GoapAction implements Comparable<CheckStat> {

    Skill skill;
    int levelTo;
    boolean satisfied;

    public CheckStat(Object target, Skill skill, int levelTo) {
        super(target);
        this.skill = skill;
        this.levelTo = levelTo;
        this.satisfied = false;
        this.addPrecondition(0, skill.toString(), this.levelTo - 1);
        this.addEffect(0, skill.toString(), this.levelTo);
    }

    @Override // precondition required to perform the action
    protected boolean checkProceduralPrecondition(IGoapUnit arg0) {
        // check to see if skill has leveled up
        if (((Character) this.target).levels.get(skill) >= levelTo) {
            System.out.println("Current Level: " + ((Character) this.target).levels.get(skill));
            System.out.println("Satisfied: " + satisfied);
            satisfied = true;
            return true;
        }
        System.out.println("Satisfied: " + satisfied);
        satisfied = false;
        return false;
    }

    @Override
    // base cost of the action
    protected float generateBaseCost(IGoapUnit arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    // cost relative to the target
    protected float generateCostRelativeToTarget(IGoapUnit arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    // return true when action is done
    protected boolean isDone(IGoapUnit arg0) {
        // TODO Auto-generated method stub
        return satisfied;
    }

    @Override
    // Is only called when requiresInRange() returns true. Return true if the Unit is in range of the target.
    protected boolean isInRange(IGoapUnit arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected boolean performAction(IGoapUnit arg0) {
        // verify if the skill has been leveled up
        return satisfied;
    }

    @Override
    // Return true if the action requires the Unit to be in a certain range to the target itself.
    protected boolean requiresInRange(IGoapUnit arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    // General reset function called when isDone() returns true.
    protected void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public String toString() {
        String pc = "", ef = "";

        HashSet<GoapState> preconditions = getPreconditions();
        HashSet<GoapState> effects = getEffects();
        for (GoapState s : preconditions) {
            pc += (s.toString() + ", ");
        }
        for (GoapState s : effects) {
            ef += (s.toString() + ", ");
        }
        return skill.toString() + " to " + levelTo + ": " + "Preconditions: " + "\n" + pc + "\n" + "Effects: " + "\n" + ef + "\n";

    }

    @Override
    public int compareTo(CheckStat o) {
        if (this.levelTo > o.levelTo) {
            return 1;
        } else if (this.levelTo < o.levelTo) {
            return -1;
        } else {
            return 0;
        }

    }
}