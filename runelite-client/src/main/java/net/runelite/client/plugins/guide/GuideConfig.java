package net.runelite.client.plugins.guide;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("guide")
public interface GuideConfig extends Config
{

    String[] stuff = {
            "magic", "ranged", "melee"
    };

    @ConfigItem(
            keyName = "greeting",
            name = "Welcome Greeting",
            description = "The message to show to the user when they login"
    )
    default String greeting()
    {
        return "Hello";
    }
}