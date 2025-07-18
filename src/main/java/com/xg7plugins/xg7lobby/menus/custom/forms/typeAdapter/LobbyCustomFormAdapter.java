package com.xg7plugins.xg7lobby.menus.custom.forms.typeAdapter;

import com.xg7plugins.data.config.Config;
import com.xg7plugins.data.config.ConfigTypeAdapter;
import com.xg7plugins.modules.xg7geyserforms.forms.customform.IComponent;
import com.xg7plugins.xg7lobby.menus.custom.forms.custom.LobbyCustomForm;
import com.xg7plugins.xg7lobby.menus.custom.forms.custom.component.ComponentType;
import com.xg7plugins.xg7lobby.menus.custom.forms.custom.component.LobbyFormComponent;
import com.xg7plugins.xg7lobby.menus.custom.forms.modal.LobbyModalForm;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LobbyCustomFormAdapter implements ConfigTypeAdapter<LobbyCustomForm> {
    @Override
    public LobbyCustomForm fromConfig(Config config, String path, Object... optionalArgs) {

        String id = config.get("id", String.class).orElseThrow(() -> new IllegalArgumentException("Missing id for LobbyModalForm"));
        String title = config.get("title", String.class).orElseThrow(() -> new IllegalArgumentException("title is required"));

        List<String> onErrorActions = config.getList("on-error", String.class, true).orElse(new ArrayList<>());
        List<String> onCloseActions = config.getList("on-close", String.class, true).orElse(new ArrayList<>());
        List<String> onSubmitActions = config.getList("on-submit", String.class).orElse(new ArrayList<>());

        List<LobbyFormComponent> components = new ArrayList<>();

        config.get("components", ConfigurationSection.class).ifPresent((section) -> {
            for (String key : section.getKeys(false)) {

                ComponentType componentType = config.get("components." + key + ".type", ComponentType.class).orElseThrow(() -> new IllegalArgumentException("Missing component type for LobbyCustomForm component!"));

                switch (componentType) {
                    case SLIDER:

                        String sliderTitle = config.get("components." + key + ".title", String.class).orElse("");
                        int minVal = config.get("components." + key + ".min-value", Integer.class).orElse(0);
                        int maxVal = config.get("components." + key + ".max-value", Integer.class).orElse(0);
                        int startVal = config.get("components." + key + ".start-value", Integer.class).orElse(0);
                        int incrementVal = config.get("components." + key + ".increment-value", Integer.class).orElse(0);

                        IComponent.Slider slider = new IComponent.Slider(sliderTitle, minVal, maxVal, incrementVal, startVal);

                        components.add(new LobbyFormComponent(key, componentType, slider));

                        continue;

                    case INPUT:

                        String inputTitle = config.get("components." + key + ".title", String.class).orElse("");
                        String inputPlaceholder = config.get("components." + key + ".placeholder", String.class).orElse("");
                        String inputDefText = config.get("components." + key + ".default-text", String.class).orElse("");

                        IComponent.Input input = new IComponent.Input(inputTitle, inputPlaceholder, inputDefText);

                        components.add(new LobbyFormComponent(key, componentType, input));

                        continue;

                    case TOGGLE:

                        String toggleText = config.get("components." + key + ".text", String.class).orElse("");
                        boolean defaultEnabled = config.get("components." + key + ".default-enabled", Boolean.class).orElse(false);

                        IComponent.Toggle toggle = new IComponent.Toggle(toggleText, defaultEnabled);

                        components.add(new LobbyFormComponent(key, componentType, toggle));

                        continue;

                    case OPTIONS:

                        String optionsTitle = config.get("components." + key + ".title", String.class).orElse("");
                        List<String> options = config.getList("components." + key + ".options", String.class).orElse(new ArrayList<>());

                        IComponent.DropDown dropDown = new IComponent.DropDown(optionsTitle, options, 0);

                        components.add(new LobbyFormComponent(key, componentType, dropDown));
                }

            }
        });


        return new LobbyCustomForm(id, title, onCloseActions, onErrorActions, onSubmitActions, components);
    }

    @Override
    public Class<LobbyCustomForm> getTargetType() {
        return LobbyCustomForm.class;
    }
}
