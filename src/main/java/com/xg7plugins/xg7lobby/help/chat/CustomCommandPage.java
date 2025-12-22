package com.xg7plugins.xg7lobby.help.chat;

import com.xg7plugins.XG7Plugins;

import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.help.chat.HelpChatPage;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.utils.text.component.ClickEvent;
import com.xg7plugins.utils.text.component.TextComponentBuilder;
import com.xg7plugins.xg7lobby.plugin.XG7LobbyLoader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomCommandPage implements HelpChatPage {

    @Override
    public List<Text> getComponents(CommandSender sender) {

        List<Text> components = new ArrayList<>();

        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));

        if (sender instanceof Player) {
            ConfigSection lang = XG7Plugins.getAPI().langManager().getLangByPlayer(XG7LobbyLoader.getInstance(), (Player) sender).join().getSecond().getLangConfiguration();

            String about = lang.getList("help.custom-commands-guide", String.class).orElse(new ArrayList<>()).stream().collect(Collectors.joining("\n"));

            components.add(Text.detectLangs(sender, XG7Plugins.getInstance(),about).join());
        } else {
            components.add(Text.format("Unable to access about text in console"));
        }

        components.add(Text.format(" "));
        components.add(
                TextComponentBuilder.of("lang:[help.chat.back]")
                .clickEvent(ClickEvent.of(ClickEvent.Action.SUGGEST_COMMAND, "/xg7lobby help"))
                        .build()
        );
        components.add(Text.format("&m-&9&m-&6&m------------------&e*&6&m------------------&9&m-&f&m-"));


        return components;
    }

    @Override
    public String getId() {
        return "custom-commands-guide";
    }
}
