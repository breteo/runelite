package net.runelite.client.plugins.guide;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.inject.Inject;

import javaGOAP.*;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.SessionOpen;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import org.json.JSONException;

@PluginDescriptor(
        name = "Guides",
        description = "Enable the Notes panel",
        tags = {"panel"},
        loadWhenOutdated = true
)
public class GuidePlugin extends Plugin
{
    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private GuideConfig config;

    private GuidePanel panel;
    private NavigationButton navButton;

    @Inject
    private Client client;

    GuideConfig.CombatEnum focus;
    int goalLevel;

    Integer ticks = 0;

    Character player;
    Observer observer;
    RuneUnit runeunit;
    GoapAgent agent;
    Hashtable<Skill, Integer> goalSkills;
    Hashtable<String, Boolean> pdOptions = new Hashtable<String, Boolean>();
    JsonParser guide = new JsonParser();
    ArrayList<Skill> skills = new ArrayList<Skill>();
    int player_combat_level;

    public GuidePlugin() throws FileNotFoundException, JSONException {
    }

    @Provides
    GuideConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GuideConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        panel = injector.getInstance(GuidePanel.class);
        panel.init(config);

        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "notes_icon.png");

        // observer updates the character with new skill data

        goalSkills = new Hashtable<Skill, Integer>();
        goalSkills.put(Skill.ATTACK, 0);
        goalSkills.put(Skill.STRENGTH, 0);
        goalSkills.put(Skill.RANGED, 0);
        goalSkills.put(Skill.DEFENCE, 0);

        // we then set the runeunit to the initial player data (one skill at level 1)
        // runeunit = new RuneUnit(observer.pc, goalSkills, client);
        // runeunit.initGoalState(goalSkills);

        agent = new RuneAgent(client, goalSkills);
        ((RuneAgent)agent).updateObserver();

        // get the current combat level
        player_combat_level = Experience.getCombatLevel(
                client.getRealSkillLevel(Skill.ATTACK),
                client.getRealSkillLevel(Skill.STRENGTH),
                client.getRealSkillLevel(Skill.DEFENCE),
                client.getRealSkillLevel(Skill.HITPOINTS),
                client.getRealSkillLevel(Skill.MAGIC),
                client.getRealSkillLevel(Skill.RANGED),
                client.getRealSkillLevel(Skill.PRAYER));

        navButton = NavigationButton.builder()
                .tooltip("Guides")
                .icon(icon)
                .priority(11)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown()
    {
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    public void onSessionOpen(SessionOpen event)
    {

    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!((RuneUnit)agent.assignedGoapUnit).planFound()) {
            agent.update();
        }
        // String data = "plan found: " + ((RuneUnit)agent.assignedGoapUnit).putGoapPlan().toString();
        String data = "current action: " + ((RuneAgent)agent).fsmTop();
        data = data.concat("\n" + ((RuneUnit)agent.assignedGoapUnit).putGoapPlan().toString() + "\n" + "plan found: " + ((RuneUnit)agent.assignedGoapUnit).planFound());
        panel.setStats(data);
        // check if the player has moved outside
    }

    public void onConfigChanged(ConfigChanged event) {
        goalLevel = config.intConfig();
        focus = config.enumConfig();
        skills = new ArrayList<Skill>();
        switch(focus) {
            case Melee:
                goalSkills.replace(Skill.ATTACK, goalLevel);
                goalSkills.replace(Skill.STRENGTH, goalLevel);
                skills.add(Skill.ATTACK);
                skills.add(Skill.STRENGTH);
                break;
            case Ranged:
                goalSkills.replace(Skill.RANGED, goalLevel);
                skills.add(Skill.RANGED);
                break;
            case Magic:
                goalSkills.replace(Skill.MAGIC, goalLevel);
                skills.add(Skill.MAGIC);
                break;
        }
        System.out.println("Plan Changed from config.");
    }

    @Subscribe
    public void onStatChanged(StatChanged event) throws JSONException {
        agent.update();
        String data = "";
        int hitpoints = client.getRealSkillLevel(Skill.HITPOINTS);

        data = bestMob(guide);
        panel.setStats(data);
        // based on new data, update the main plugin view
    }

    private String bestMob(JsonParser json) throws JSONException {
        String focusStr = "";
        String levelStr = "";
        String outString = "";
        Skill currentTrainedSkill = null;
        if (agent.getPlan().peek() instanceof CheckStat) {
            currentTrainedSkill = ((CheckStat)agent.getPlan().peek()).skill;
        } else {
            return "";
        }

        switch(focus) {
            case Melee:
                focusStr  = "melee";
                if (client.getRealSkillLevel(currentTrainedSkill) < 20) {
                    levelStr = "upToTwentyGuide";
                } else if (client.getRealSkillLevel(currentTrainedSkill) < 40) {
                    levelStr = "upToFourtyGuide";
                } else if (client.getRealSkillLevel(currentTrainedSkill) < 99) {
                    levelStr = "upToNinetyNineGuide";
                }
                break;
            case Ranged:
                focusStr = "ranged";
                if (client.getRealSkillLevel(currentTrainedSkill) < 20) {
                    levelStr = "upToTwentyGuide";
                } else if (client.getRealSkillLevel(currentTrainedSkill) < 25) {
                    levelStr = "upToTwentyFiveGuide";
                } else if (client.getRealSkillLevel(currentTrainedSkill) < 56) {
                    levelStr = "upToFiftySixGuide";
                } else if (client.getRealSkillLevel(currentTrainedSkill) < 99) {
                    levelStr = "upToNinetyNineGuide";
                }
                break;
            case Magic:
                focusStr = "magic";
                if (client.getRealSkillLevel(currentTrainedSkill) < 17) {
                    levelStr = "upToSeventeenGuide";
                } else if (client.getRealSkillLevel(currentTrainedSkill) < 33) {
                    levelStr = "upToThirtyThreeGuide";
                } else if (client.getRealSkillLevel(currentTrainedSkill) < 99) {
                    levelStr = "upToNinetyNineGuide";
                }
                break;
        }
        ArrayList<String> arr;
        arr = json.getAllInfoKeys(focusStr, levelStr);
        for (String str : arr) {
            outString.concat( "Mob: " + json.getName(focusStr, levelStr, str) + "\n");
            outString.concat( "Location: " + json.getLocation(focusStr, levelStr, str) + "\n");
            outString.concat( "HP: " + json.getHitpoints(focusStr, levelStr, str) + "\n");
            outString.concat( "Combat Level: " + json.getLevel(focusStr, levelStr, str) + "\n");
            outString.concat("\n");
        }
        return outString;
    }
}


