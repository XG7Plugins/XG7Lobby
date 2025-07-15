package com.xg7plugins.xg7lobby.menus.custom.forms.custom.component;

import com.xg7plugins.modules.xg7geyserforms.forms.customform.IComponent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LobbyFormComponent {

    private final String id;
    private final ComponentType type;
    private final List<String> actions;
    private final IComponent component;

}
