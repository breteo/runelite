package net.runelite.client.plugins.guide;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("guide")
public interface GuideConfig extends Config
{
    @ConfigItem(
            keyName = "notesData",
            name = "",
            description = "",
            hidden = true
    )
    default String notesData()
    {
        return "";
    }

    @ConfigItem(
            keyName = "notesData",
            name = "",
            description = ""
    )
    void notesData(String str);
}