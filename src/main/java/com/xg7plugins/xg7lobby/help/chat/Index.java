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
                TextComponentBuilder.of(Text.fromLang(sender, XG7Lobby.getInstance(), "help-in-chat.about").join().getText())
                        .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7lobby help about"))
                        .hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, "Click to see about the plugins"))
                        .build()
        );

        components.add(Text.format(" "));
        components.add(TextComponentBuilder.of(Text.fromLang(sender, XG7Lobby.getInstance(), "help-in-chat.lang").join().getText())
                .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7plugins lang"))
                .build());
        components.add(TextComponentBuilder.of(Text.fromLang(sender, XG7Lobby.getInstance(), "help-in-chat.tasks").join().getText())
                .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7lobby help selector-guide"))
                .build());
        components.add(TextComponentBuilder.of(Text.fromLang(sender, XG7Lobby.getInstance(), "help-in-chat.commands").join().getText())
                .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7lobby help command-page1"))
                .build());
        components.add(TextComponentBuilder.of(Text.fromLang(sender, XG7Lobby.getInstance(), "help-in-chat.menus-guide").join().getText())
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
