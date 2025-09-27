package com.xg7plugins.xg7lobby.menus.custom.forms;

import com.xg7plugins.config.file.ConfigFile;
import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.managers.Manager;
import com.xg7plugins.modules.xg7geyserforms.XG7GeyserForms;
import com.xg7plugins.xg7lobby.XG7Lobby;
import com.xg7plugins.xg7lobby.menus.custom.forms.custom.LobbyCustomForm;
import com.xg7plugins.xg7lobby.menus.custom.forms.modal.LobbyModalForm;
import com.xg7plugins.xg7lobby.menus.custom.forms.simple.LobbySimpleForm;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class CustomFormsManager implements Manager {

    private final HashMap<String, LobbyForm> forms = new HashMap<>();

    public void loadForms() {
        XG7Lobby lobby = XG7Lobby.getInstance();

        lobby.getDebug().loading("Loading custom forms...");

        File folderFile = new File(lobby.getDataFolder(), "menus/forms");

        boolean existsBefore = folderFile.exists();

        if (!existsBefore) folderFile.mkdirs();

        List<File> formsFiles = getDefaults(existsBefore, Collections.singletonList("games_form"), folderFile);

        for (File file : formsFiles) {
            ConfigSection config = ConfigFile.of("menus/forms/" + file.getName().replace(".yml", ""), lobby).root();

            String id = config.get("id", String.class);
            FormType type = config.get("type", FormType.class);

            if (id == null) throw new IllegalArgumentException("Form id cannot be null!");
            if (type == null) throw new IllegalArgumentException("Form type cannot be null!");

            LobbyForm lobbyForm = null;

            switch (type) {
                case CUSTOM:
                    lobbyForm = config.get("", LobbyCustomForm.class);
                    break;
                case MODAL:
                    lobbyForm = config.get("", LobbyModalForm.class);
                    break;
                case SIMPLE:
                    lobbyForm = config.get("", LobbySimpleForm.class);
                    break;
            }

            if (lobbyForm == null) throw new IllegalArgumentException("Form malconfigured!");

            forms.put(id, lobbyForm);
            XG7GeyserForms.getInstance().registerForm(lobbyForm.getForm());
        }

        lobby.getDebug().loading("Loaded " + forms.size() + " custom forms.");
        lobby.getDebug().info("Loaded " + forms.keySet() + " custom forms.");
    }


    private List<File> getDefaults(boolean existsBefore, List<String> forms, File folder) {

        List<File> formsFiles = new ArrayList<>(Arrays.asList(Objects.requireNonNull(folder.listFiles())));

        for (String form : forms) {
            File file = new File(folder, "menus/forms/" + form + ".yml");
            if (!file.exists() && !existsBefore) {
                XG7Lobby.getInstance().saveResource("menus/forms/" + form + ".yml", false);
                formsFiles.add(file);
            }
        }
        return formsFiles;
    }

    public boolean is(String id, FormType type) {
        if (!forms.containsKey(id)) return false;
        return forms.get(id).getType() == type;
    }

    public List<String> getIds() {
        return new ArrayList<>(forms.keySet());
    }

    public boolean contains(String id) {
        return forms.containsKey(id);
    }

    public Collection<LobbyForm> getForms() {
        return forms.values();
    }

    public LobbyForm getForm(String id) {
        return forms.get(id);
    }

    public <T extends LobbyForm> T getForm(String id, Class<T> clazz) {
        if (!forms.containsKey(id)) return null;
        return clazz.cast(forms.get(id));
    }

    public LobbyForm getFormByXG7GeyserFormsId(String id) {
        for (LobbyForm form : forms.values()) {
            if (form.getForm().getId().equalsIgnoreCase(id)) return form;
        }
        return null;
    }

    public void sendForm(String id, Player player) {
        if (!forms.containsKey(id)) return;

        XG7GeyserForms.getInstance().sendForm(player, "lobby-custom-form:" + id);
    }

    public void reloadForms() {
        forms.clear();
        loadForms();
    }


}
