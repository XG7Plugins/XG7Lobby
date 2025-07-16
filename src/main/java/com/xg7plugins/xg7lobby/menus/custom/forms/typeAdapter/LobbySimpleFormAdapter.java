package com.xg7plugins.xg7lobby.menus.custom.forms.typeAdapter;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigTypeAdapter;
import com.xg7plugins.xg7lobby.menus.custom.forms.modal.LobbyModalForm;
import com.xg7plugins.xg7lobby.menus.custom.forms.simple.LobbySimpleForm;
import com.xg7plugins.xg7lobby.menus.custom.forms.simple.button.ImageType;
import com.xg7plugins.xg7lobby.menus.custom.forms.simple.button.LobbySimpleFormButton;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class LobbySimpleFormAdapter implements ConfigTypeAdapter<LobbySimpleForm> {
    @Override
    public LobbySimpleForm fromConfig(Config config, String path, Object... optionalArgs) {

        String id = config.get("id", String.class).orElseThrow(() -> new IllegalArgumentException("Missing id for LobbyModalForm"));
        String title = config.get("title", String.class).orElseThrow(() -> new IllegalArgumentException("title is required"));

        String content = config.get("content", String.class).orElse("");

        List<LobbySimpleFormButton> buttons = new ArrayList<>();

        config.get("buttons", ConfigurationSection.class).ifPresent((section) -> {
            for (String key : section.getKeys(false)) {
                String imageString = config.get("buttons." + key + ".icon", String.class).orElse(null);
                String text = config.get("buttons." + key + ".text", String.class).orElse("");
                List<String> actions =  config.getList("buttons." + key + ".actions", String.class).orElse(new ArrayList<>());

                String imageUrl = null;
                ImageType imageType = null;

                if (imageString != null) {
                    imageType = ImageType.valueOf(imageString.toUpperCase().split(", ")[0]);
                    imageUrl = imageString.split(", ")[1];
                }

                buttons.add(new LobbySimpleFormButton(imageUrl, imageType, text, actions));
            }
        });

        List<String> onErrorActions = config.getList("on-error", String.class).orElse(new ArrayList<>());
        List<String> onCloseActions = config.getList("on-close", String.class).orElse(new ArrayList<>());

        return new LobbySimpleForm(id, title, content, buttons, onCloseActions, onErrorActions);
    }

    @Override
    public Class<LobbySimpleForm> getTargetType() {
        return LobbySimpleForm.class;
    }
}
