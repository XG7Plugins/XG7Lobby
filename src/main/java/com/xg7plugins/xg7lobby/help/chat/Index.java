package com.xg7plugins.xg7lobby.help.chat;

import com.xg7plugins.help.chat.HelpChatPage;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.component.ClickEvent;
import com.xg7plugins.utils.text.component.HoverEvent;
import com.xg7plugins.utils.text.component.TextComponentBuilder;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Index implements HelpChatPage {

    @Override
    public List<Text> getComponents(CommandSender sender) {

        List<Text> components = new ArrayList<>();

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));
        components.add(Text.format("lang:[help.chat.title]"));
        components.add(Text.format(" "));
        components.add(
                TextComponentBuilder.of("lang:[help.chat.content]")
                        .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7lobby help about"))
                        .hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, "Click to see about the plugins"))
                        .build()
        );

        components.add(Text.format(" "));
        components.add(TextComponentBuilder.of("lang:[help.chat.lang]")
                .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7plugins lang"))
                .build());
        components.add(TextComponentBuilder.of("lang:[help.chat.selector-guide]")
                .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7lobby help selector-guide"))
                .build());
        components.add(TextComponentBuilder.of("lang:[help.chat.commands]")
                .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7lobby help command-page1"))
                .build());
        components.add(TextComponentBuilder.of("lang:[help.chat.custom-commands-guide]")
                .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7lobby help custom-commands-guide"))
                .build());
        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));

        return components;
    }

    @Override
    public String getId() {
        return "index";
    }
}
