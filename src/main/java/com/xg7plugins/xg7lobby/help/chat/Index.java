package com.xg7plugins.xg7lobby.help.chat;

import com.xg7plugins.help.chat.HelpChatPage;
import com.xg7plugins.utils.text.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Index implements HelpChatPage {

    @Override
    public List<Text> getComponents(CommandSender sender) {

        List<Text> components = new ArrayList<>();

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));
        components.add(Text.fromLang(sender, XG7Lobby.getInstance(), "help.chat.title"));
        components.add(Text.format(" "));
        components.add(Text.format(Component.text(Text.fromLang(sender, XG7Lobby.getInstance(), "help.chat.content").getText())
                .clickEvent(ClickEvent.suggestCommand( "/xg7lobby help about"))
                ));

        components.add(Text.format(" "));
        components.add(Text.format(Component.text(Text.fromLang(sender, XG7Lobby.getInstance(), "help.chat.lang").getText())
                .clickEvent(ClickEvent.suggestCommand( "/xg7plugins lang"))
                ));
        components.add(Text.format(Component.text(Text.fromLang(sender, XG7Lobby.getInstance(), "help.chat.commands").getText())
                .clickEvent(ClickEvent.suggestCommand( "/xg7lobby help commands-page1"))
                ));
        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));

        return components;
    }

    @Override
    public String getId() {
        return "index";
    }
}
