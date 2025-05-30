package com.xg7plugins.xg7lobby;

import com.xg7plugins.boot.EnvironmentConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class XG7LobbyConfig extends EnvironmentConfig {

    boolean isChatLocked;

}
