package com.xg7plugins.xg7lobby.environment;

import com.xg7plugins.boot.EnvironmentConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class XG7LobbyEnvironment extends EnvironmentConfig {

    private boolean isChatLocked = false;

}
