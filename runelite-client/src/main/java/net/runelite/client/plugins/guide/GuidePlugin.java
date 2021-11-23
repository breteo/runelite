package net.runelite.client.plugins.guide;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ChatPlayer;
import net.runelite.api.Client;
import net.runelite.api.Friend;
import net.runelite.api.Ignore;
import net.runelite.api.MessageNode;
import net.runelite.api.NameableContainer;
import net.runelite.api.ScriptID;
import net.runelite.api.VarPlayer;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@PluginDescriptor(
        name = "Guide",
        description = "Add extra information to the friend and ignore lists"
)
public class GuidePlugin extends Plugin
{
    private static final int MAX_FRIENDS_P2P = 400;
    private static final int MAX_FRIENDS_F2P = 200;

    private static final int MAX_IGNORES_P2P = 400;
    private static final int MAX_IGNORES_F2P = 100;

    @Inject
    private Client client;

    @Inject
    private GuideConfig config;

    @Provides
    GuideConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GuideConfig.class);
    }

    @Override
    protected void shutDown()
    {
        final int world = client.getWorld();
        setFriendsListTitle("Friends List - World " + world);
        setIgnoreListTitle("Ignore List - World " + world);
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event)
    {
        if (event.getScriptId() == ScriptID.FRIENDS_UPDATE)
        {
            final int world = client.getWorld();
            final boolean isMember = client.getVar(VarPlayer.MEMBERSHIP_DAYS) > 0;
            final NameableContainer<Friend> friendContainer = client.getFriendContainer();
            final int friendCount = friendContainer.getCount();
            if (friendCount >= 0)
            {
                final int limit = isMember ? MAX_FRIENDS_P2P : MAX_FRIENDS_F2P;

                final String title = "Friends - W" +
                        world +
                        " (" +
                        friendCount +
                        "/" +
                        limit +
                        ")";

                setFriendsListTitle(title);
            }
        }
        else if (event.getScriptId() == ScriptID.IGNORE_UPDATE)
        {
            final int world = client.getWorld();
            final boolean isMember = client.getVar(VarPlayer.MEMBERSHIP_DAYS) > 0;
            final NameableContainer<Ignore> ignoreContainer = client.getIgnoreContainer();
            final int ignoreCount = ignoreContainer.getCount();
            if (ignoreCount >= 0)
            {
                final int limit = isMember ? MAX_IGNORES_P2P : MAX_IGNORES_F2P;

                final String title = "Ignores - W" +
                        world +
                        " (" +
                        ignoreCount +
                        "/" +
                        limit +
                        ")";

                setIgnoreListTitle(title);
            }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage message)
    {
        if (message.getType() == ChatMessageType.LOGINLOGOUTNOTIFICATION && config.showWorldOnLogins())
        {
            MessageNode messageNode = message.getMessageNode();
            // get the player name out of the notification
            String name = messageNode.getValue()
                    .substring(0, messageNode.getValue().indexOf(" "));
            ChatPlayer player = findFriend(name);

            if (player != null && player.getWorld() > 0)
            {
                messageNode
                        .setValue(messageNode.getValue() + String.format(" (World %d)", player.getWorld()));
            }
        }
    }

    private void setFriendsListTitle(final String title)
    {
        Widget friendListTitleWidget = client.getWidget(WidgetInfo.FRIEND_CHAT_TITLE);
        if (friendListTitleWidget != null)
        {
            friendListTitleWidget.setText(title);
        }
    }

    private void setIgnoreListTitle(final String title)
    {
        Widget ignoreTitleWidget = client.getWidget(WidgetInfo.IGNORE_TITLE);
        if (ignoreTitleWidget != null)
        {
            ignoreTitleWidget.setText(title);
        }
    }

    private ChatPlayer findFriend(String name)
    {
        NameableContainer<Friend> friendContainer = client.getFriendContainer();
        if (friendContainer != null)
        {
            String cleanName = Text.removeTags(name);
            return friendContainer.findByName(cleanName);
        }

        return null;
    }
}

