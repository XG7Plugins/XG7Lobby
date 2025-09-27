package com.xg7plugins.xg7lobby.menus.custom.forms.typeAdapter;



import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.config.typeadapter.ConfigTypeAdapter;
import com.xg7plugins.modules.xg7geyserforms.forms.customform.IComponent;
import com.xg7plugins.xg7lobby.menus.custom.forms.custom.LobbyCustomForm;
import com.xg7plugins.xg7lobby.menus.custom.forms.custom.component.ComponentType;
import com.xg7plugins.xg7lobby.menus.custom.forms.custom.component.LobbyFormComponent;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class LobbyCustomFormAdapter implements ConfigTypeAdapter<LobbyCustomForm> {
    @Override
    public LobbyCustomForm fromConfig(ConfigSection config, String path, Object... optionalArgs) {

        String id = config.get("id", String.class);
        String title = config.get("title", String.class);

        if (id == null) throw  new NullPointerException("id is required");
        if (title == null) throw  new NullPointerException("title is required");

        List<String> onErrorActions = config.getList("on-error", String.class, true).orElse(new ArrayList<>());
        List<String> onCloseActions = config.getList("on-close", String.class, true).orElse(new ArrayList<>());
        List<String> onSubmitActions = config.getList("on-submit", String.class).orElse(new ArrayList<>());

        List<LobbyFormComponent> components = new ArrayList<>();

        config.child("components").getKeys(false).forEach((key) -> {

            ComponentType componentType = config.get("components." + key + ".type", ComponentType.class);

            if (componentType == null) throw new IllegalArgumentException("Invalid component type for component " + key);

            switch (componentType) {
                case SLIDER:

                    String sliderTitle = config.get("components." + key + ".title", "");
                    int minVal = config.get("components." + key + ".min-value", 0);
                    int maxVal = config.get("components." + key + ".max-value", 0);
                    int startVal = config.get("components." + key + ".start-value", 0);
                    int incrementVal = config.get("components." + key + ".increment-value", 0);

                    IComponent.Slider slider = new IComponent.Slider(sliderTitle, minVal, maxVal, incrementVal, startVal);

                    components.add(new LobbyFormComponent(key, componentType, slider));

                    return;

                case INPUT:

                    String inputTitle = config.get("components." + key + ".title", "");
                    String inputPlaceholder = config.get("components." + key + ".placeholder", "");
                    String inputDefText = config.get("components." + key + ".default-text", "");

                    IComponent.Input input = new IComponent.Input(inputTitle, inputPlaceholder, inputDefText);

                    components.add(new LobbyFormComponent(key, componentType, input));

                    return;

                case TOGGLE:

                    String toggleText = config.get("components." + key + ".text", "");
                    boolean defaultEnabled = config.get("components." + key + ".default-enabled", false);

                    IComponent.Toggle toggle = new IComponent.Toggle(toggleText, defaultEnabled);

                    components.add(new LobbyFormComponent(key, componentType, toggle));

                    return;

                case OPTIONS:

                    String optionsTitle = config.get("components." + key + ".title", "");
                    List<String> options = config.getList("components." + key + ".options", String.class).orElse(new ArrayList<>());

                    IComponent.DropDown dropDown = new IComponent.DropDown(optionsTitle, options, 0);

                    components.add(new LobbyFormComponent(key, componentType, dropDown));
            }
        });


        return new LobbyCustomForm(id, title, onCloseActions, onErrorActions, onSubmitActions, components);
    }

    @Override
    public Class<LobbyCustomForm> getTargetType() {
        return LobbyCustomForm.class;
    }
}
