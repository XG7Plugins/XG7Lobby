package com.xg7plugins.xg7lobby.menus.custom.forms.custom;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7geyserforms.forms.customform.CustomForm;
import com.xg7plugins.modules.xg7geyserforms.forms.customform.IComponent;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.aciton.ActionsProcessor;
import com.xg7plugins.xg7lobby.menus.custom.forms.custom.component.LobbyFormComponent;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.InputComponent;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LobbyCustomForm extends CustomForm {

    private final List<String> closeActions;
    private final List<String> errorActions;
    private final List<String> onSubmitActions;

    private final List<LobbyFormComponent> components;

    public LobbyCustomForm(String id, String title, List<String> closeActions, List<String> errorActions, List<String> onSubmitActions, List<LobbyFormComponent> components) {
        super(id, title, XG7Lobby.getInstance());
        this.closeActions = closeActions;
        this.errorActions = errorActions;
        this.onSubmitActions = onSubmitActions;
        this.components = components;
    }

    @Override
    public List<IComponent> components(Player player) {
        return components.stream().map(LobbyFormComponent::getComponent).collect(Collectors.toList());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.CustomForm form, CustomFormResponse result, Player player) {

        List<Pair<String, String>> placeholders = new ArrayList<>();

        for (int i = 0; i < components.size(); i++) {
            LobbyFormComponent lobbyFormComponent = components.get(i);

            switch (lobbyFormComponent.getType()) {
                case INPUT:
                    placeholders.add(Pair.of(lobbyFormComponent.getId(), result.asInput(i)));
                case SLIDER:
                    placeholders.add(Pair.of(lobbyFormComponent.getId(), result.asSlider(i) + ""));
                case OPTIONS:
                    placeholders.add(Pair.of(lobbyFormComponent.getId(), result.asDropdown(i) + ""));
                case TOGGLE:
                    placeholders.add(Pair.of(lobbyFormComponent.getId(), result.asToggle(i) + ""));
            }
        }

        ActionsProcessor.process(onSubmitActions, player, placeholders);

    }

    @Override
    public void onError(org.geysermc.cumulus.form.CustomForm form, InvalidFormResponseResult<CustomFormResponse> result, Player player) {
        ActionsProcessor.process(errorActions, player);

    }

    @Override
    public void onClose(org.geysermc.cumulus.form.CustomForm form, Player player) {
        ActionsProcessor.process(closeActions, player);

    }
}
