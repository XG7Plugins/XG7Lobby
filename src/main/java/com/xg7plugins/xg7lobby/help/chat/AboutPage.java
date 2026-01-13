package com.xg7plugins.xg7lobby.help.chat;

import com.xg7plugins.XG7Plugins;

import com.xg7plugins.help.chat.HelpChatPage;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.component.ClickEvent;
import com.xg7plugins.utils.text.component.TextComponentBuilder;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class AboutPage implements HelpChatPage {

    @Override
    public List<Text> getComponents(CommandSender sender) {

        List<Text> components = new ArrayList<>();

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));

        components.add(Text.fromLang(sender, XG7Lobby.getInstance(), "help.about")
                .replace("discord", "discord.gg/jfrn8w92kF")
                .replace("github", "github.com/DaviXG7")
                .replace("website", "xg7plugins.com")
                .replace("version", XG7Plugins.getInstance().getVersion())
                .replaceLiteral("<endp>", "<br>")
        );

        components.add(Text.format(" "));

        components.add(
                TextComponentBuilder.of(Text.fromLang(sender, XG7Lobby.getInstance(), "help.chat.back").getText())
                        .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7lobby help"))
                        .build()
        );
        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));


        return components;
    }

    @Override
    public String getId() {
        return "about";
    }

}
