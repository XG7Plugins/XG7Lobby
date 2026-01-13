package com.xg7plugins.xg7lobby.help.form;

import com.xg7plugins.XG7Plugins;

import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.modules.xg7geyserforms.forms.SimpleForm;
import com.xg7plugins.utils.item.impl.BookItem;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.cumulus.util.FormImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XG7LobbyHelpForm extends SimpleForm {
    public XG7LobbyHelpForm() {
        super(
                "lobby-help-form",
                "lang:[help.form.title]",
                XG7Lobby.getInstance(),
                Collections.emptyList()
        );
    }

    @Override
    public String content(Player player) {

        ConfigSection lang = XG7Plugins.getAPI().langManager().getLangByPlayer(XG7Lobby.getInstance(), player).getSecond().getLangConfiguration();

        List<String> content = lang.getList("help.form.content", String.class).orElse(Collections.emptyList());


        return String.join("\n", content);
    }

    @Override
    public List<ButtonComponent> buttons(Player player) {

        List<ButtonComponent> buttons = new ArrayList<>();

        buttons.add(ButtonComponent.of("lang:[help.form.about]", FormImage.Type.URL, "https://cdn-icons-png.freepik.com/512/6388/6388052.png"));
        buttons.add(ButtonComponent.of("lang:[help.form.commands]", FormImage.Type.URL, "https://a.slack-edge.com/80588/img/services/slash-commands_512.png"));
        buttons.add(ButtonComponent.of("lang:[help.form.collaborators]", FormImage.Type.URL, "https://www.google.com/url?sa=i&url=https%3A%2F%2Ft3.ftcdn.net%2Fjpg%2F01%2F09%2F84%2F42%2F360_F_109844239_A7MdQSDf4y1H80cfvHZuSa0zKBkZ68S7.jpg&psig=AOvVaw0Fdl6WbgqHFNSH5yvMMgiN&ust=1768316106309000&source=images&cd=vfe&opi=89978449"));

        return buttons;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {
        switch (result.clickedButtonId()) {
            case 1:
                XG7Lobby.getInstance().getHelpMessenger().getForm().getForm("commands").send(player);
                return;
            case 0:
                ConfigSection lang = XG7Plugins.getAPI().langManager().getLangByPlayer(XG7Lobby.getInstance(), player).getSecond().getLangConfiguration();

                BookItem bookItem = BookItem.newBook();

                List<List<String>> pages = BookItem.convertTextToBookPages(Text.format((String) lang.get("help.about"))
                        .replace("discord", "discord.gg/jfrn8w92kF")
                        .replace("github", "github.com/DaviXG7")
                        .replace("website", "xg7plugins.com")
                        .replace("version", XG7Plugins.getInstance().getVersion())
                );

                for (List<String> lines : pages) {
                    bookItem.addPage(String.join("", lines));
                }

                bookItem.openBook(player);
                return;
            case 2:
                XG7Lobby.getInstance().getHelpMessenger().getForm().getForm("lobby-collaborators-help").send(player);
        }
    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {

    }

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {

    }
}
