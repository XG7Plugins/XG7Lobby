package com.xg7plugins.xg7lobby.menus.custom.forms.simple.button;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.geysermc.cumulus.component.ButtonComponent;

import java.util.List;

@RequiredArgsConstructor
@Data
public class LobbySimpleFormButton {

    private final String imageUrl;
    private final ImageType imageType;

    private final String text;
    private final List<String> actions;

    public ButtonComponent toButtonComponent() {
        if (imageUrl == null) return ButtonComponent.of(text);
        return ButtonComponent.of(text, imageType.toCumulusType(), imageUrl);
    }

}
