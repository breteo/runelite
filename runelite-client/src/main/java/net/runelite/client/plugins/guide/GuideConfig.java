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
            keyName = "showWorldOnLogin",
            name = "Show world on login",
            description = "Shows world number on friend login notifications"
    )
    default boolean showWorldOnLogins()
    {
        return false;
    }

    @ConfigItem(
            position = 1,
            keyName = "quickLookup",
            name = "Hotkey lookup (Alt + Left click)",
            description = "Configures whether to enable the hotkey lookup for GE searches"
    )
    default boolean quickLookup()
    {
        return true;
    }

    @ConfigSection(
            position = 2,
            name = "Quest Filters",
            description = "Determines which quests should be shown via the selected filter(s)"
    )
    String filterSection = "filterSection";

    @ConfigItem(
            keyName = "orderListBy",
            name = "Quest order",
            description = "Configures which way to order the quest list",
            position = 3,
            section = filterSection
    )
    default String[] orderListBy()
    {
        return stuff;
    }
}