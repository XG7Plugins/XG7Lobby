package com.xg7plugins.xg7lobby.menus.custom.forms.custom;

import com.xg7plugins.modules.xg7geyserforms.forms.Form;
import com.xg7plugins.modules.xg7geyserforms.forms.customform.CustomForm;
import com.xg7plugins.modules.xg7geyserforms.forms.customform.IComponent;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.menus.custom.forms.FormType;
import com.xg7plugins.xg7lobby.menus.custom.forms.LobbyForm;
import com.xg7plugins.xg7lobby.menus.custom.forms.custom.component.LobbyFormComponent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class LobbyCustomForm extends CustomForm implements LobbyForm {


    private final List<String> closeActions;
    private final List<String> errorActions;
    private final List<String> onSubmitActions;

    private final List<LobbyFormComponent> components;

    public LobbyCustomForm(String id, String title, List<String> closeActions, List<String> errorActions, List<String> onSubmitActions, List<LobbyFormComponent> components) {
        super("lobby-custom-form:" + id, title, XG7Lobby.getInstance(), Collections.emptyList());
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
                    continue;
                case SLIDER:
                    placeholders.add(Pair.of(lobbyFormComponent.getId(), result.asSlider(i) + ""));
                    continue;
                case OPTIONS:
                    placeholders.add(Pair.of(lobbyFormComponent.getId(), result.asDropdown(i) + ""));
                    continue;
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

    @Override
    public String title() {
        return super.getTitle();
    }

    @Override
    public FormType getType() {
        return FormType.CUSTOM;
    }

    @Override
    public Form<?, ?> getForm() {
        return this;
    }
}
