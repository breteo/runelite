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

    private PlanData pd;

    Integer ticks = 0;

    Character player;
    Observer observer;
    RuneUnit runeunit;
    GoapAgent agent;
    Hashtable<Skill, Integer> goalSkills;
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
        player = new Character();

        // observer updates the character with new skill data
        observer = new Observer(player, client);
        // we then set the runeunit to the initial player data (one skill at level 1)
        runeunit = new RuneUnit(observer.pc);

        goalSkills = new Hashtable<Skill, Integer>();
        goalSkills.put(Skill.ATTACK, 25);

        runeunit.initGoalState(goalSkills);

        agent = new DefaultGoapAgent(runeunit);



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
        observer.setStats();
        runeunit.updateWorldState(observer.pc);
        pd = new PlanData(runeunit);
        // String data = observer.pc.levels.toString();
        String data = pd.getData();
        panel.setStats(data);
    }

    @Subscribe
    public void onStatChanged(StatChanged event) {
        // This call will cause a freeze on startup, place it in onStatChanged
        // agent.setAssignedGoapUnit(runeunit);
        // agent.update();
    }
}


