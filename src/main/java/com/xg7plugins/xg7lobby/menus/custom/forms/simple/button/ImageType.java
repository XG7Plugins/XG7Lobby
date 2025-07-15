package com.xg7plugins.xg7lobby.menus.custom.forms.simple.button;

import org.geysermc.cumulus.util.FormImage;

public enum ImageType {

    URL,
    PATH;


    public FormImage.Type toCumulusType() {
        return FormImage.Type.valueOf(this.name());
    }

}
