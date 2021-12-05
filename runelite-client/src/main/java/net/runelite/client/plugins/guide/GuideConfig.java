package net.runelite.client.plugins.guide;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("guide")
public interface GuideConfig extends Config
{
    enum CombatEnum
    {
        Attack,
        Strength,
        Defence,
        Magic,
        Ranged
    }

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

    @ConfigItem
            (
                    position = 1,
                    keyName = "booleanConfig",
                    name = "Warn when hitpoints are low",
                    description = "Alert when hitpoints below 10%"
            )
    default boolean booleanConfig() { return false; }

    @ConfigItem(
            position = 2,
            keyName = "enumConfig",
            name = "Combat Focus",
            description = "Choose which combat discipline to focus"
    )
    default CombatEnum enumConfig() { return CombatEnum.Attack; }

    @ConfigItem(
         position = 3,
         keyName = "intConfig",
         name = "Specified Level",
         description = "Specify a specific level"
    )
    default int intConfig() { return 1; }
}