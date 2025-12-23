package com.xg7plugins.xg7lobby.menus.custom.forms.modal;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.modules.xg7geyserforms.forms.Form;
import com.xg7plugins.modules.xg7geyserforms.forms.ModalForm;
import com.xg7plugins.xg7lobby.acitons.ActionsProcessor;
import com.xg7plugins.xg7lobby.menus.custom.forms.FormType;
import com.xg7plugins.xg7lobby.menus.custom.forms.LobbyForm;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@Getter
public class LobbyModalForm extends ModalForm implements LobbyForm {

    private final List<String> errorActions;
    private final List<String> firstButtonActions;
    private final List<String> secondButtonActions;

    public LobbyModalForm(String id, String title, String content, String button1, String button2, List<String> errorActions, List<String> firstButtonActions, List<String> secondButtonActions) {
        super(XG7Plugins.getInstance(), "lobby-custom-form:" + id, title, content, button1, button2, Collections.emptyList());
        this.firstButtonActions = firstButtonActions;
        this.secondButtonActions = secondButtonActions;
        this.errorActions = errorActions;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onClose(org.geysermc.cumulus.form.ModalForm form, Player player) {}

    @Override
    public void onError(org.geysermc.cumulus.form.ModalForm form, org.geysermc.cumulus.response.result.InvalidFormResponseResult<org.geysermc.cumulus.response.ModalFormResponse> result, Player player) {
        ActionsProcessor.process(errorActions, player);
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.ModalForm form, org.geysermc.cumulus.response.ModalFormResponse result, Player player) {
        ActionsProcessor.process(result.clickedFirst() ? firstButtonActions : secondButtonActions, player);
    }

    @Override
    public String title() {
        return super.getTitle();
    }

    @Override
    public FormType getType() {
        return FormType.MODAL;
    }

    @Override
    public Form<?, ?> getForm() {
        return this;
    }
}
