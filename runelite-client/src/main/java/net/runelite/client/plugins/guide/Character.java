package net.runelite.client.plugins.guide;

import net.runelite.api.Skill;
import java.util.Hashtable;

public class Character {
    String name;
    Hashtable<Skill, Integer> levels = new Hashtable<Skill, Integer>();

    public Character() {
        name = "James";
        levels.put(Skill.ATTACK, 1);
    }

    public void initStats(int[] stats) {
        int mark = 0;
        for (Skill skill : Skill.values()) {
            levels.put(skill, stats[skill.ordinal()]);
        }

    }

    public boolean isSkillLeveledUp(Skill sk) {
        return false;
    }

}