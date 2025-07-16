package com.xg7plugins.xg7lobby.menus.custom.forms;

import com.xg7plugins.modules.xg7geyserforms.forms.Form;

public interface LobbyForm {

    String title();
    FormType getType();

    Form<?,?> getForm();

}
