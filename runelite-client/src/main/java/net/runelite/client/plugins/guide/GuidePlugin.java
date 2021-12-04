package net.runelite.client.plugins.guide;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.List;
import javax.inject.Inject;

import javaGOAP.DefaultGoapAgent;
import javaGOAP.GoapState;
import javaGOAP.GoapUnit;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
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
import javaGOAP.GoapAgent;

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

    Integer ticks = 0;

    Character player;
    Observer observer;
    RuneUnit runeunit;
    GoapAgent agent;
    Hashtable<Skill, Integer> goalSkills;
    Hashtable<String, Boolean> pdOptions = new Hashtable<String, Boolean>();
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
        goalSkills.put(Skill.ATTACK, 25);

        pdOptions.put("worldState", false);
        pdOptions.put("goalState", false);
        pdOptions.put("actionPlan", false);
        pdOptions.put("availableActions", true);

        // we then set the runeunit to the initial player data (one skill at level 1)
        // runeunit = new RuneUnit(observer.pc, goalSkills, client);
        // runeunit.initGoalState(goalSkills);

        agent = new RuneAgent(client, goalSkills);
        ((RuneAgent)agent).updateObserver();
        agent.update();
        agent.update();


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
        /*if (!((RuneUnit)agent.assignedGoapUnit).planFound()) {
            agent.update();
        }*/
        // String data = "plan found: " + ((RuneUnit)agent.assignedGoapUnit).putGoapPlan().toString();
        String data = "current action: " + ((RuneAgent)agent).fsmTop();
        panel.setStats(data);
        // check if the player has moved outside
    }

    @Subscribe
    public void onStatChanged(StatChanged event) {
        agent.update();
        // based on new data, update the main plugin view
    }
}


