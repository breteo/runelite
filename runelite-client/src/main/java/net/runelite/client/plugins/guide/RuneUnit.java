package net.runelite.client.plugins.guide;

import java.util.Queue;
import javaGOAP.*;



public class RuneUnit extends GoapUnit {

    Character unitChar;
    public RuneUnit(Character character) {
        this.unitChar = character;
    }

    @Override
    public void goapPlanFailed(Queue<GoapAction> arg0) {
        System.out.println("Plan Failed.");
    }

    @Override
    public void goapPlanFinished() {
        System.out.println("Plan Finished.");
    }

    @Override
    public void goapPlanFound(Queue<GoapAction> arg0) {
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