package nz.co.jammehcow.jenkinsdiscord;

import com.google.common.primitives.UnsignedInteger;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

import java.awt.*;

/**
 * Author: jammehcow.
 * Date: 22/04/17.
 */

class DiscordWebhook {
    private String webhookUrl;
    private JSONObject obj;
    private JSONObject embed;

    private enum Color {
        GREEN(1681177), RED(11278871);

        private UnsignedInteger code;
        Color(int code) { this.code = UnsignedInteger.asUnsigned(code); }
    }

    public DiscordWebhook(String url) {
        this.webhookUrl = url;
        this.obj = new JSONObject();
        this.obj.append("content", null);
        this.embed = new JSONObject();
        this.embed.append("footer", new JSONObject().append("text", "Jenkins Discord Webhook plugin made by jammehcow"));
        this.embed.append("type", "rich");
        this.embed.append("thumbnail", new JSONObject().append("url", "https://wiki.jenkins-ci.org/download/attachments/2916393/headshot.png"));
    }

    public DiscordWebhook setTitle(String title) {
        this.embed.append("title", title);
        return this;
    }

    public DiscordWebhook setURL(String buildUrl) {
        this.embed.append("url", buildUrl);
        return this;
    }

    public DiscordWebhook setStatus(boolean isSuccess) {
        this.embed.append("color", (isSuccess) ? Color.GREEN : Color.RED);
        return this;
    }

    public DiscordWebhook setDescription(String content) {
        this.embed.append("description", content);
        return this;
    }

    public void send() {
        Unirest.setDefaultHeader("Content-Type", "application/json");
        Unirest.post(this.webhookUrl).body(this.obj);
    }
}
