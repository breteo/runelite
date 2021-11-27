package net.runelite.client.plugins.guide;
import javaGOAP.*;

public class GenericAction extends GoapAction {

    public GenericAction(Object target) {
        super(target);
        this.addPrecondition(0, "level up melee", false);
        this.addEffect(0, "level up melee", true);

        // TODO Auto-generated constructor stub
    }

    @Override // precondition required to perform the action
    protected boolean checkProceduralPrecondition(IGoapUnit arg0) {
        // TODO Auto-generated method stub
        return true;
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
        System.out.println("Increased melee level");
        return true;
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
        return true;
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

}