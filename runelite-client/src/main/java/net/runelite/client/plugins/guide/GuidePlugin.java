package net.runelite.client.plugins.guide;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
        name = "Guide",
        description = "Add extra information to the friend and ignore lists"
)
public class GuidePlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private GuideConfig config;

    @Override
    protected void startUp() throws Exception
    {
        System.out.println("Example started!");
    }

    @Override
    protected void shutDown() throws Exception
    {
        System.out.println("Example stopped!");
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
        {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
        }
    }

    @Provides
    GuideConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GuideConfig.class);
    }
}

