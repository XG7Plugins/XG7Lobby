package com.xg7plugins.xg7lobby.help.form;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.XG7PluginsAPI;

import com.xg7plugins.config.file.ConfigSection;
import com.xg7plugins.modules.xg7geyserforms.forms.SimpleForm;
import com.xg7plugins.modules.xg7menus.item.impl.BookItem;
import com.xg7plugins.utils.text.Text;
import com.xg7plugins.xg7lobby.XG7Lobby;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.response.result.InvalidFormResponseResult;
import org.geysermc.cumulus.util.FormImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XG7LobbyHelpForm extends SimpleForm {
    public XG7LobbyHelpForm() {
        super("lobby-help-form", "lang:[help.form.title]", XG7Lobby.getInstance());
    }

    @Override
    public String content(Player player) {

        ConfigSection lang = XG7PluginsAPI.langManager().getLangByPlayer(XG7Lobby.getInstance(), player).join().getSecond().getLangConfiguration();

        List<String> content = lang.getList("help.form.content", String.class).orElse(Collections.emptyList());


        return String.join("\n", content);
    }

    @Override
    public List<ButtonComponent> buttons(Player player) {

        List<ButtonComponent> buttons = new ArrayList<>();

        buttons.add(ButtonComponent.of("lang:[help.form.about]", FormImage.Type.URL, "https://www.google.com/imgres?q=1x1%20chat%20icon&imgurl=https%3A%2F%2Fcdn-icons-png.freepik.com%2F512%2F5624%2F5624123.png&imgrefurl=https%3A%2F%2Fwww.freepik.com%2Ficon%2Fchat_5624123&docid=-0U84-zDgCqqTM&tbnid=oCuetAkO3SfrtM&vet=12ahUKEwiV-pKn9MKOAxUHq5UCHb-yOygQM3oECCAQAA..i&w=512&h=512&hcb=2&itg=1&ved=2ahUKEwiV-pKn9MKOAxUHq5UCHb-yOygQM3oECCAQAA"));
        buttons.add(ButtonComponent.of("lang:[help.form.commands]", FormImage.Type.URL, "https://www.google.com/imgres?q=1x1%20image%20commands%20slash&imgurl=https%3A%2F%2Fa.slack-edge.com%2F80588%2Fimg%2Fservices%2Fslash-commands_512.png&imgrefurl=https%3A%2F%2Fslack.com%2Fmarketplace%2FA0F82E8CA-slash-commands&docid=64bTqJh2CXeoLM&tbnid=ri6g_nRdNWRO6M&vet=12ahUKEwjP8bbN8sKOAxUxALkGHcF1I5gQM3oECB8QAA..i&w=512&h=512&hcb=2&ved=2ahUKEwjP8bbN8sKOAxUxALkGHcF1I5gQM3oECB8QAA"));
        buttons.add(ButtonComponent.of("lang:[help.form.menus-guide]", FormImage.Type.URL, "https://www.google.com/imgres?q=1x1%20image%20GUI%20icon&imgurl=https%3A%2F%2Fwww.svgrepo.com%2Fshow%2F340386%2Fgui.svg&imgrefurl=https%3A%2F%2Fwww.svgrepo.com%2Fsvg%2F340386%2Fgui&docid=SvVXMpY_h90NJM&tbnid=eY6E9ce7Aeg6KM&vet=12ahUKEwiv2fni8sKOAxW-GbkGHQUNMH8QM3oECCcQAA..i&w=800&h=800&hcb=2&ved=2ahUKEwiv2fni8sKOAxW-GbkGHQUNMH8QM3oECCcQAA"));
        buttons.add(ButtonComponent.of("lang:[help.form.custom-commands-guide]", FormImage.Type.URL, "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAgVBMVEX///8AAAD6+vpVVVWZmZnR0dFTU1PV1dVXV1dMTEz29vb5+fny8vLQ0NCnp6eVlZWGhoaMjIx3d3cwMDBFRUU/Pz9oaGgKCgpHR0fIyMjo6Og3NzePj4/c3Nw7OztdXV19fX0hISEqKiqjo6O1tbVwcHC6uroTExNkZGQbGxskJCTElXD6AAAMj0lEQVR4nO1d60LiOhAGZJWbKAhyUVQUdPX9H/DQUiBtJnNJJimy5/u1urXkIzOZSyaZRoNCa7kYtreT7/XPz/v7dPyxuemRf5MMy+H4+mH29dR52ww83/A5XjctPN3fer5PFYPRkzmqeVv81S/vpja7At+vw1aMUfPRe7YGtf4QveHm0UmvwKjOiVyAQ5qt+C94oPhl2PJfqIw715A23DcM5hyGO471zOPWPaJ77jtabh30/NIUcYUNaMx+zYRJcZ5aVFsoQQHFfpdJcRKTjo1WhxoQm2KPp4vdxB4AMYMiio0n+mXNaT8iGwDkDMoo0rOYWER7jBkUUexRuphaRNs8gpLlBl9R58m98LE6xQZmF6c1+Kb6FFtuQZ3X4nyzKbK9G6cuptbBA9gU2e5W6wX8+6lNMJF3g3ilZfDHAy03U/uxu+ZSkQcC7izO2G8EHLiubeh3K/mrJg8EXIr8kLhfNf2AmchNVapIg0lxLVgqyroImInCFiuyOAFYs5m62BZ8iCmoE/sjD0H3cwAR92df2b/kUZzvH15wAnXDaABm4uRN6Qf9Wbj0aP+aJ6j74axZprt/8G4mGMHmKJCPjdzZ9qWYLwwLcOkHsKcImokTZsp+wCGiB5ZpDsW37MHM2k04UV5uNOagmah+a2o4RfR+FDu7x5b5v1gu2C7qR3Uwx0swKxNGPOglqE+nAc45n9d6JAlKfCUapYAXoEiuqF+NU6riyW8Mdm72LoiTidZ1+c2AoFIUd47b6viDVzgLBN3vwcwK9K6rr/5rP0QI6kOj8XH6CfA1KYBZBS0xHfxYrxbr4nWj8df4UZxWgtMmaqvpwN49AWYRFdRxozUzfxamJRx5oa0SwZ2c2hSFujgsbMURorjdlfhSzDACFGWCumx8Vn7T5c+iM7M3UwyEAUGVUHwqLTR7sA22cwtP1yLyBNVFcecl31u/ZDlweG52ocgQosg3GrvQ4tX+LctooMnnoSbDEEHNolVo84XhwOHZdeUIavBufQLTaGTr5hoa4Qu13CA6mOEthE/v0/6Vp9HIteULHCJhNKj9Ef6egY1dPGgLeY81i1VB3TvIjjGiukhuAIUwzHxRm6KPLhaOx8z6yz2QqJ8Q0TCGe2ebRZGYxUP2CpbSJrK3y9jC82Z4LEKwU7lSXTym59bOYTp0kbNHyd71qeIU8AK6KBLUznGxRLYHwQwcaxPWNwY240FAUO3lxknx6jR2rHoNiPppHYRHx0ElovcV1Ixix/jZrvAzYOXVmNvoHZ+tU6sQiEURXG5KKfIhOtSK0WDXCVx7MLRSFt5Go1xoeosPtRTqsQk2m8BGg5igv9EooUcM1XDgeDpY4FomqNWsWgFfXSyBKiQ9Gg3BDGYQ6aKzGA+gyFpRTZBp4yLqFxKU6SI8gzBFni4aqKYxbOQOnEhEC4rcWXSI6B52RkQqqEvgrRXson7xDGZgCipaL3oD/IGUIqMWruMxgxl4Kyo2gxBBsaBu/EbPAociRvCP4294Dtzx6XgEGbqI6iA8gxkAQUW+TXYVlQ8IXURLmt0EIaOBZHoZa00AcKPhI6J7VHUR/SqBjKIiOsgnS1dRN8UrVFZuojJ0f7inDh5g6iK1pLGL/fzgmEWhHcRMP2l542qi6xuW6eANEmngIprD06KzAaxzQhHNNMmVu2E5FpyTFQGwU1NCM/En/z1Mkefir5DPCwewjyEV0aaD4uCH6+Bb+4iKAJKL2CLjJghSZO9ax1tP7RkU6uAf438DNu7YBw4VCProoALF3ncUgoCI+umgAsWlcw8jAMJFBjYTehTtqEufoNCT+QM8FUBxAJ8d0SQodLZhlzlkueGejOVBXQcVKKqGw6pmQo/ip2tXWINgiJkowy5n4GOgFBBHE9EMnbB6eZXsm6KrZoMRLhFoB4tqRB3EEyNcDEZhHIN1EBVRBYI79DbMyz8gxNVBvcO5qxHrDhcbEVw1g6AavxzLz+1E7JFHcdUOkO8u0xisFh/P92/j8duovWGYkkiu2h6KIuoAdXXK2ZsJBoirN87eTDCAzmJUVy2GDkLAhvxbzAQBty7+fh0khh1VB1OJaAFQUH+Dq8YGNPQL0cEjLIqXYCbKqAw/qpnwqXUMR5mA0FU7exHNYQphVDMhrHPUxHEWL08HD+g4CerpYK0ECyKXZibKuIocDyb2ZCB0gT36847opQCESNFVq1tEYfzSaIKNSzETTvx6V43EBZkJGP/rYAn/tA6eJ8F/WwfPOyfDwy9M/Arxv6tWwv9m4uxw+Tr4T5uJi9BBmR3EDo9fvA4CBM9hTqNmtu+7KSigUEw6ATM4qn9tFYoopoNARH/vmNmUiCqiI+fcpkPUzZfRkXwKKiCErprQTBiXAda19xTXTIxK/18Pxaiu2qj8RC2JRb14EHDVRtVn0uuiopkALJ59IWd6QY0a0VszmD+XgtYJKexgFSm9m7iuGiCiBcV0hXvxXTXH15GKYh0iukciQcVmUM9Vc3wlCfglc9VgxNfFhK4ajNdNe5QdG7h//lisYjSyTemq0fiebD+V24TFjuh98DDSuyA9uavGxXyj1L4nvavGxkylSXjUxG8YwYyjoAEZjJrNBAdhnUPOVgdNvIaI6nmZCRdm/ke89Vy1GDpowPMq8bibL0oiWkDQZMUgWLerJsKLhzLWFy554V3syJ2jq4biS0bxV5iJCr5FTpzeKppERPeQdOaqM6IPALtt2C9w1Rxg9jqOWycTbwYz8ILGqK5azBlsMhtWKs4gcPel7pVUNug2InHNRIN1kXgQSKsYv6SZuJ0hFNR6muAEKH3HRhjQq4j7acop484ietk+cHP9EcGbLwawLzIcqCYCN9cXUC2nRJezYODhMNCqNofyIWXUawoGHioOwNuT9cspYy43RPYNohijZtuT4h1DwKkNR1tQo5Q0++liu9Gf0E9RVr+6okYqSvfRxdwno68+JrOLZUHVcNVgiCnuU/i0b0snF81ZFOqgqN+TUFAPexQ9iuJD9ZPsRr6D9eHhqFX3MkE9hQ0t6l7gSsamhfQfiF11L1hRS7tMxHJzW/6UBXit+5OcoE9ZCHsWy9tofVxQK3xG9q8aewcuwckXri5W9wn76B2dz+WHJwDrjOLDrfW7CCdf8FaMBwChO9a6otwGqmhVwrlnOcbJF1YbEWint4XMYrnr7+H+YJpilON1nNQUvJWNGI116cFjAwiKYpzTZ4yrxF179X2nd/NlPtY6XcWKU4x0QJJmiKTPXBRn5kNmJxaMoraZYDPEqi2cRsN8qNTj0E0x2gFJiiFeTuLQxZKUllckF8V4ByTfcIJUhhd24MyVplX5P5hixAOS+D4GoyAIMhpmct9qSApRjKWDGVCLz6l4gnTRzCjaQgJQRLqYBFdhLwIJNqCo37ynCxg8q795gfDqXaSrFrtZu6WLxj4i+H5XpAEQDOW3cxqdV/nzi/KsqN9IY8BawOqM3VQqMXeFepKqw6rRMFJRf+G3AxTXAEGVAnNH2zBZWWW5M9fsNLCB6/Z8ziwqHRKAO4eI60ZNUTAar7vNAK2LWqcDwaVAXhhrGg1joXl2MiRXVL3DOsBC7VP52zuZfqNeAUvo4LqoeErXjoHZZqKMg5AZHg3e2BkTVM2DOpaY2jPYe2R9oXPr74n2QG6KuieRKgYbINhtzjlFa4UuGraC6u8ItcfMCeqeQyp/0TbBPK/W7TPelDtwE+MXBEHXcqN9XK5nujWADu5t3dT+DwC7Z40UPqOrM2QX9Q+RGxEUIKKH5XDKmcXW3IwNOX1WAYqcD5LhtOKBOnhAl6OLA3MXhlWBFdKvjo2DXbZFtGUaNGm9uturT0/RNYOVTrAvsrdisWdqihuYYK+aopiLVITIAaWl+AqaCVuPWEajgKAVcAKKS7eZKGNiP+Z+Jx8JKNrVvT14Cl7Yyw1vWysdxSqcGy8so5HhUcIwPUVk84xrNEQES75QCqCrBM+Bw7K8VUxGi8QXxVhmojIgzorK2nvd4Xu7UT78zgBZMcPRRVajynl7Vct1VHTt2px8B20r3t8+a7pPzL23a4A8WWHtyJTw/Xeod2WBFKSIFrNI6CIS3k+eb2NcrsEGo7wyB+7A9V1quN3Uyi7DituwF3XgoDTsrPuBnlVIB24DbezAYdt6+G2R3ig4MaByZAXmboErt799HC7P4WJbEysWxwenp9U3Hrqzy9fOAgPypOIjolRFeP+gflmPKlrDV3dn6ekdOvQs8/PaXulnzbQxuL0Htp/XY3Jmus+L82d3QO9m8zGevr///Ky/J9v2cMFYNP4DBOK2q7RIMdIAAAAASUVORK5CYII="));
        buttons.add(ButtonComponent.of("lang:[help.form.collaborators]", FormImage.Type.URL, "https://www.google.com/imgres?q=1x1%20image%20star%20icon&imgurl=https%3A%2F%2Ft3.ftcdn.net%2Fjpg%2F01%2F09%2F84%2F42%2F360_F_109844239_A7MdQSDf4y1H80cfvHZuSa0zKBkZ68S7.jpg&imgrefurl=https%3A%2F%2Fstock.adobe.com%2Fbr%2Fsearch%3Fk%3Dstar%2Bicon&docid=HeLjRe2I2RnLNM&tbnid=OwXLe9vJCtYgYM&vet=12ahUKEwiVjO6P88KOAxV_ELkGHa9cM14QM3oECCwQAA..i&w=360&h=360&hcb=2&ved=2ahUKEwiVjO6P88KOAxV_ELkGHa9cM14QM3oECCwQAA"));

        return buttons;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onFinish(org.geysermc.cumulus.form.SimpleForm form, SimpleFormResponse result, Player player) {
        switch (result.clickedButtonId()) {
            case 1:
                XG7Lobby.getInstance().getHelpMessenger().getForm().getForm("commands").send(player);
                return;
            case 0:
            case 2:
            case 3:
                ConfigSection lang = XG7PluginsAPI.langManager().getLangByPlayer(XG7Lobby.getInstance(), player).join().getSecond().getLangConfiguration();

                List<String> about = lang.getList("help." + (result.clickedButtonId() == 2 ? "selector-guide" : result.clickedButtonId() == 0 ? "about" : "custom-commands-guide"), String.class).orElse(new ArrayList<>());

                BookItem bookItem = BookItem.newBook();

                List<List<String>> pages = new ArrayList<>();
                List<String> currentPage = new ArrayList<>();

                for (String line : about) {

                    currentPage.add(Text.detectLangs(player, XG7Plugins.getInstance(),line).join()
                            .replace("discord", "discord.gg/jfrn8w92kF")
                            .replace("github", "github.com/DaviXG7")
                            .replace("website", "xg7plugins.com")
                            .replace("version", XG7Lobby.getInstance().getDescription().getVersion())
                            .getText());
                    if (currentPage.size() == 10) {
                        pages.add(new ArrayList<>(currentPage));
                        currentPage.clear();
                    }
                }
                if (!currentPage.isEmpty()) pages.add(currentPage);

                for (List<String> page : pages) bookItem.addPage(String.join("\n", page));


                bookItem.openBook(player);
                return;
            case 4:
                XG7Lobby.getInstance().getHelpMessenger().getForm().getForm("lobby-collaborators-help").send(player);
        }
    }

    @Override
    public void onError(org.geysermc.cumulus.form.SimpleForm form, InvalidFormResponseResult<SimpleFormResponse> result, Player player) {

    }

    @Override
    public void onClose(org.geysermc.cumulus.form.SimpleForm form, Player player) {

    }
}
