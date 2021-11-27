package net.runelite.client.plugins.guide;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.client.events.SessionOpen;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

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
    public void onSessionOpen(SessionOpen event)
    {
        // update notes
        String data = config.notesData();
        panel.setNotes(data);
    }
}


