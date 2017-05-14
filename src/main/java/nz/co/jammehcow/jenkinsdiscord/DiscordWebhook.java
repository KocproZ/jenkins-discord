package nz.co.jammehcow.jenkinsdiscord;

import com.google.common.primitives.UnsignedInteger;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import nz.co.jammehcow.jenkinsdiscord.exception.WebhookException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        this.obj.put("username", "Jenkins");
        this.obj.put("avatar_url", "https://wiki.jenkins-ci.org/download/attachments/2916393/headshot.png");
        this.embed = new JSONObject();
    }

    public DiscordWebhook setTitle(String title) {
        this.embed.put("title", title);
        return this;
    }

    public DiscordWebhook setURL(String buildUrl) {
        this.embed.put("url", buildUrl);
        return this;
    }

    public DiscordWebhook setStatus(boolean isSuccess) {
        this.embed.put("color", (isSuccess) ? Color.GREEN.code : Color.RED.code);
        return this;
    }

    public DiscordWebhook setDescription(String content) {
        this.embed.put("description", content);
        return this;
    }

    public DiscordWebhook setFooter(String text) {
        this.embed.put("footer", new JSONObject().put("text", text));
        return this;
    }

    public void send() throws WebhookException {
        this.obj.put("embeds", new JSONArray().put(this.embed));

        try {
            HttpResponse<JsonNode> response = Unirest.post(this.webhookUrl).header("Content-Type", "application/json").body(this.obj).asJson();

            try {
                if (response.getBody() == null || response.getBody().getObject().get("embeds") == null) throw new JSONException("Expected.");
                throw new WebhookException(response.getBody().getObject().toString(2));
            } catch (JSONException ignored) {}
        } catch (UnirestException e) { e.printStackTrace(); }
    }
}
