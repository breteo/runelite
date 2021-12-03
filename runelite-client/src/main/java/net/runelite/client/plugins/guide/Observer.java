package net.runelite.client.plugins.guide;

import net.runelite.api.Client;
import net.runelite.api.Skill;
public class Observer {

    public Character pc;
    public Client client;
    public Observer(Character pc, Client client) {
        this.pc = pc;
        this.client = client;
        this.setStats();
    }

    public int[] getStats() {
        int[] skills = new int[Skill.values().length];
        for (Skill sk : Skill.values()) {
            skills[sk.ordinal()] = client.getRealSkillLevel(sk);
        }
        return skills;
    }

    public void setStats() {
        this.pc.initStats(getStats());
    }
}
