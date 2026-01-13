package com.xg7plugins.xg7lobby.data.player;

import lombok.Data;

@Data
public class LobbySettings {

    private boolean hidingPlayers = false;
    private boolean hidingChat = false;

    //Coming soon
    //    private DayMode alwaysNightMode;

    private boolean buildEnabled = false;
    private boolean flying = false;

//    public enum DayMode {
//        ALWAYS_DAY,
//        ALWAYS_NIGHT,
//        NORMAL
//    }

}
