package com.xg7plugins.xg7lobby.help.form;

import com.xg7plugins.boot.Plugin;
import com.xg7plugins.modules.xg7geyserforms.forms.SimpleForm;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.cumulus.util.FormImage;

import java.util.ArrayList;
import java.util.List;

public class CollaboratorsForm extends SimpleForm {
    public CollaboratorsForm() {
        super("lobby-collaborators-help", "lang:[help.form.collaborators-title]", XG7Lobby.getInstance());
    }

    @Override
    public String content(Player player) {
        return "lang:[help.form.collaborators-content]";
    }

    @Override
    public List<ButtonComponent> buttons(Player player) {
        List<ButtonComponent> buttons = new ArrayList<>();

        buttons.add(ButtonComponent.of("§aDaviXG7 &bCreator of all plugin", FormImage.Type.URL, "https://crafatar.com/avatars/45766b7f-9789-40e1-bd0b-46fa0d032bde"));
        buttons.add(ButtonComponent.of("§aSadnessSad &bBeta tester and video helper", FormImage.Type.URL, "https://crafatar.com/avatars/f12b8505-8b77-4046-9d86-8b5303690096"));
        buttons.add(ButtonComponent.of("§aBultzzXG7 &bBeta tester and video helper", FormImage.Type.URL, "https://crafatar.com/avatars/696581df-4256-4028-b55e-9452b4de40b6"));
        buttons.add(ButtonComponent.of("§aMintNonExistent (Gorrfy) &bBeta tester", FormImage.Type.URL, "https://crafatar.com/avatars/f66d01bf-0e1c-4800-9a50-060411bff0bd"));
        buttons.add(ButtonComponent.of("§aDanielXG7 &bBeta tester", FormImage.Type.URL, "https://crafatar.com/avatars/35e9eeda-84ce-497d-af08-7cf5d68a21c7"));
        buttons.add(ButtonComponent.of(Text.fromLang(player, XG7Lobby.getInstance(), "help.form.click-to-back").join().getText()));

        return buttons;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {
        switch (result.clickedButtonId()) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                XG7Lobby.getInstance().getHelpMessenger().getForm().getForm("lobby-collaborators-help").send(player);
                return;
            case 5:
                XG7Lobby.getInstance().getHelpMessenger().sendForm(player);
        }
    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {

    }

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {

    }
}
