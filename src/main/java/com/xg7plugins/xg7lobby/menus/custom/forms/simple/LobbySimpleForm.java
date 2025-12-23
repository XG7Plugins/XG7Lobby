package com.xg7plugins.xg7lobby.menus.custom.forms.simple;

import com.xg7plugins.modules.xg7geyserforms.forms.Form;
import com.xg7plugins.modules.xg7geyserforms.forms.SimpleForm;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.menus.custom.forms.FormType;
import com.xg7plugins.xg7lobby.menus.custom.forms.LobbyForm;
import com.xg7plugins.xg7lobby.menus.custom.forms.simple.button.LobbySimpleFormButton;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class LobbySimpleForm extends SimpleForm implements LobbyForm {

    private final List<String> closeActions;
    private final List<String> errorActions;

    private final String content;
    private final HashMap<Integer, LobbySimpleFormButton> buttons;

    public LobbySimpleForm(String id, String title, String content, List<LobbySimpleFormButton> buttons, List<String> closeActions, List<String> errorActions) {
        super("lobby-custom-form:" + id, title, XG7Lobby.getInstance(), Collections.emptyList());
        this.content = content;
        this.buttons = new HashMap<>();
        this.closeActions = closeActions;
        this.errorActions = errorActions;

        IntStream.range(0, buttons.size()).forEach(i -> this.buttons.put(i, buttons.get(i)));
    }

    @Override
    public String content(Player player) {
        return content;
    }

    @Override
    public List<ButtonComponent> buttons(Player player) {
        return buttons.values().stream().map(LobbySimpleFormButton::toButtonComponent).collect(Collectors.toList());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {
        LobbySimpleFormButton clickedButton = buttons.get(result.clickedButtonId());
        if (clickedButton == null) return;
        if (clickedButton.getActions() == null) return;
        ActionsProcessor.process(clickedButton.getActions(), player, Pair.of("button-id", result.clickedButtonId() + ""));
    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {
        ActionsProcessor.process(errorActions, player);
    }

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {
        ActionsProcessor.process(closeActions, player);
    }

    @Override
    public String title() {
        return super.getTitle();
    }

    @Override
    public FormType getType() {
        return FormType.SIMPLE;
    }

    @Override
    public Form<?, ?> getForm() {
        return this;
    }
}
