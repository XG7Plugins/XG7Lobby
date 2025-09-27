package com.xg7plugins.xg7lobby.menus.custom.forms.typeAdapter;

import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.config.typeadapter.ConfigTypeAdapter;
import com.xg7plugins.xg7lobby.menus.custom.forms.modal.LobbyModalForm;

import java.util.ArrayList;
import java.util.List;

public class LobbyModalFormAdapter implements ConfigTypeAdapter<LobbyModalForm> {
    @Override
    public LobbyModalForm fromConfig(ConfigSection config, String path, Object... optionalArgs) {

        String id = config.get("id", String.class);
        String title = config.get("title", String.class);

        if (id == null) throw  new NullPointerException("id is required");
        if (title == null) throw  new NullPointerException("title is required");

        String content = config.get("content", "");

        String button1Text = config.get("first-button-text", "");
        String button2Text = config.get("second-button-text", "");

        List<String> onClickButton1 = config.getList("on-click-first-button", String.class, true).orElse(new ArrayList<>());
        List<String> onClickButton2 = config.getList("on-click-second-button", String.class, true).orElse(new ArrayList<>());

        List<String> onErrorActions = config.getList("on-error", String.class, true).orElse(new ArrayList<>());

        return new LobbyModalForm(id, title, content, button1Text, button2Text, onErrorActions, onClickButton1, onClickButton2);
    }

    @Override
    public Class<LobbyModalForm> getTargetType() {
        return LobbyModalForm.class;
    }
}


