package com.xg7plugins.xg7lobby.menus.custom.forms.typeAdapter;

import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.config.typeadapter.ConfigTypeAdapter;
import com.xg7plugins.xg7lobby.menus.custom.forms.simple.LobbySimpleForm;
import com.xg7plugins.xg7lobby.menus.custom.forms.simple.button.ImageType;
import com.xg7plugins.xg7lobby.menus.custom.forms.simple.button.LobbySimpleFormButton;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class LobbySimpleFormAdapter implements ConfigTypeAdapter<LobbySimpleForm> {
    @Override
    public LobbySimpleForm fromConfig(ConfigSection config, String path, Object... optionalArgs) {

        String id = config.get("id", String.class);
        String title = config.get("title", String.class);

        if (id == null) throw  new NullPointerException("id is required");
        if (title == null) throw  new NullPointerException("title is required");

        String content = config.get("content", "");

        List<LobbySimpleFormButton> buttons = new ArrayList<>();

        config.child("buttons").getKeys(false).forEach(key -> {
            String imageString = config.get("buttons." + key + ".icon");
            String text = config.get("buttons." + key + ".text", "");
            List<String> actions = config.getList("buttons." + key + ".actions", String.class).orElse(new ArrayList<>());

            String imageUrl = null;
            ImageType imageType = null;

            if (imageString != null) {
                imageType = ImageType.valueOf(imageString.toUpperCase().split(", ")[0]);
                imageUrl = imageString.split(", ")[1];
            }

            buttons.add(new LobbySimpleFormButton(imageUrl, imageType, text, actions));
        });

        List<String> onErrorActions = config.getList("on-error", String.class, true).orElse(new ArrayList<>());
        List<String> onCloseActions = config.getList("on-close", String.class, true).orElse(new ArrayList<>());

        return new LobbySimpleForm(id, title, content, buttons, onCloseActions, onErrorActions);
    }

    @Override
    public Class<LobbySimpleForm> getTargetType() {
        return LobbySimpleForm.class;
    }
}
