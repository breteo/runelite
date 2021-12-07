package net.runelite.client.plugins.guide;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;

import javaGOAP.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.RuneScapeProfileChanged;
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
@Slf4j
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
    boolean initFlag = false;
    Character player;
    Observer observer;
    RuneUnit runeunit;
    String mob_data = "";
    String last_data = "";
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
    public void onRuneScapeProfileChanged(RuneScapeProfileChanged event) {
        if(!initFlag) {
            log.warn("Logged In");
            goalSkills = new Hashtable<Skill, Integer>();

            goalLevel = config.intConfig();
            focus = config.enumConfig();

            // we then set the runeunit to the initial player data (one skill at level 1)
            // runeunit = new RuneUnit(observer.pc, goalSkills, client);
            // runeunit.initGoalState(goalSkills);

            agent = new RuneAgent(client, goalSkills);

            // get the current combat level
            player_combat_level = Experience.getCombatLevel(
                    client.getRealSkillLevel(Skill.ATTACK),
                    client.getRealSkillLevel(Skill.STRENGTH),
                    client.getRealSkillLevel(Skill.DEFENCE),
                    client.getRealSkillLevel(Skill.HITPOINTS),
                    client.getRealSkillLevel(Skill.MAGIC),
                    client.getRealSkillLevel(Skill.RANGED),
                    client.getRealSkillLevel(Skill.PRAYER));
            initFlag = true;
        }
    }
    @Subscribe
    public void onSessionOpen(SessionOpen event)
    {

    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (agent != null) {
            if (!((RuneUnit) agent.assignedGoapUnit).planFound()) {
                agent.update();
            }

            skills = new ArrayList<Skill>();


            ((RuneUnit) agent.assignedGoapUnit).goalStatTable = goalSkills;

            // String data = "plan found: " + ((RuneUnit)agent.assignedGoapUnit).putGoapPlan().toString();
            String data = "current action: " + ((RuneAgent) agent).fsmTop() + "\n" + "worldState size: " + ((RuneUnit) agent.assignedGoapUnit).getWorldState().size();
            for (GoapAction g : ((RuneUnit) agent.assignedGoapUnit).putGoapPlan()) {
                data = data.concat(g.toString() + "\n");
            }
            data = data.concat("\n" + "plan found: " + ((RuneUnit) agent.assignedGoapUnit).planFound() + "\n" +
                    "Goal Level: " + goalLevel + "\n" + "focus: " + focus + "\n" + "Best Mob: " + mob_data + "\n");

            panel.setStats(data);
        }
        // check if the player has moved outside
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) throws JSONException {
        /*goalLevel = config.intConfig();
        focus = config.enumConfig();
        */
        goalLevel = config.intConfig();
        focus = config.enumConfig();
        skills = new ArrayList<Skill>();
        goalSkills = new Hashtable<Skill, Integer>();
        switch (focus) {
            case Melee:
                goalSkills.put(Skill.ATTACK, goalLevel);
                goalSkills.put(Skill.STRENGTH, goalLevel);
                skills.add(Skill.ATTACK);
                skills.add(Skill.STRENGTH);
                break;
            case Ranged:
                goalSkills.put(Skill.RANGED, goalLevel);
                skills.add(Skill.RANGED);
                break;
            case Magic:
                goalSkills.put(Skill.MAGIC, goalLevel);
                skills.add(Skill.MAGIC);
                break;
        }
        if (agent != null) {
            ((RuneAgent) agent).updatePlan(goalSkills);
            agent.update();
        }
        mob_data = bestMob(guide);
        log.info("Config Changed!");
    }

    @Subscribe
    public void onStatChanged(StatChanged event) {
        if (ticks > 23) {
            if (agent != null) {
                agent.update();
            }


            int hitpoints = client.getRealSkillLevel(Skill.HITPOINTS);
            agent.update();

            // based on new data, update the main plugin view
            log.warn("Stat Changed! " + event.getSkill());
        } else {
            ticks++;
        }
    }

    private String bestMob(JsonParser json) throws JSONException {
        String focusStr = "";
        String levelStr = "";
        String outString = "";
        Skill currentTrainedSkill = null;
        if (agent != null) {
            if (agent.getPlan().peek() instanceof CheckStat) {
                currentTrainedSkill = ((CheckStat) Objects.requireNonNull(agent.getPlan().peek())).skill;
            } else {
                return "";
            }
        }
        if (focus != null) {
            switch (focus) {
                case Melee:
                    focusStr = "melee";
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
        }
        log.info("Finding best Mob");
        if (!focusStr.isEmpty() && !levelStr.isEmpty()) {
            ArrayList<String> arr;
            arr = json.getAllInfoKeys(focusStr, levelStr);
            if (arr.size() == 5) {
                outString = outString.concat("Spell to use: " + json.getSpell(focusStr, levelStr) + "\n");
                outString = outString.concat("Mob: " + json.getName(focusStr, levelStr, arr.get(1)) + "\n");
                outString = outString.concat("Location: " + json.getLocation(focusStr, levelStr, arr.get(2)) + "\n");
                outString = outString.concat("HP: " + json.getHitpoints(focusStr, levelStr, arr.get(3)) + "\n");
                outString = outString.concat("Combat Level: " + json.getLevel(focusStr, levelStr, arr.get(4)) + "\n");
                outString = outString.concat("\n");
            } else {
                outString = outString.concat("Mob: " + json.getName(focusStr, levelStr, arr.get(0)) + "\n");
                outString = outString.concat("Location: " + json.getLocation(focusStr, levelStr, arr.get(1)) + "\n");
                outString = outString.concat("HP: " + json.getHitpoints(focusStr, levelStr, arr.get(2)) + "\n");
                outString = outString.concat("Combat Level: " + json.getLevel(focusStr, levelStr, arr.get(3)) + "\n");
                outString = outString.concat("\n");
            }
        }
        return outString;
    }
}


