package nz.co.jammehcow.jenkinsdiscord;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import jenkins.model.JenkinsLocationConfiguration;
import nz.co.jammehcow.jenkinsdiscord.exception.WebhookException;
import nz.co.jammehcow.jenkinsdiscord.util.EmbedDescription;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * Author: jammehcow.
 * Date: 22/04/17.
 */

public class WebhookPublisher extends Notifier {
    private final String webhookURL;
    private final String thumbnailURL;
    private final boolean sendOnStateChange;
    private boolean enableUrlLinking;
    private final boolean enableArtifactList;
    private final boolean enableFooterInfo;
    private static final String NAME = "Discord Notifier";
    private static final String VERSION = "1.2.1";

    @DataBoundConstructor
    public WebhookPublisher(String webhookURL, String thumbnailURL, boolean sendOnStateChange, boolean enableUrlLinking, boolean enableArtifactList, boolean enableFooterInfo) {
        this.webhookURL = webhookURL;
        this.thumbnailURL = thumbnailURL;
        this.sendOnStateChange = sendOnStateChange;
        this.enableUrlLinking = enableUrlLinking;
        this.enableArtifactList = enableArtifactList;
        this.enableFooterInfo = enableFooterInfo;
    }

    public String getWebhookURL() { return this.webhookURL; }
    public boolean isSendOnStateChange() { return this.sendOnStateChange; }
    public boolean isEnableUrlLinking() { return this.enableUrlLinking; }
    public boolean isEnableArtifactList() { return this.enableArtifactList; }
    public boolean isEnableFooterInfo() { return this.enableFooterInfo; }

    @Override
    public boolean needsToRunAfterFinalized() { return true; }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        // The global configuration, used to fetch the instance url
        JenkinsLocationConfiguration globalConfig = new JenkinsLocationConfiguration();

        // Create a new webhook payload
        DiscordWebhook wh = new DiscordWebhook(this.webhookURL);

        if (this.webhookURL.isEmpty()) {
            // Stop the plugin from continuing when the webhook URL isn't set. Shouldn't happen due to form validation
            listener.getLogger().println("The Discord webhook is not set!");
            return true;
        }

        if (this.enableUrlLinking && (globalConfig.getUrl() == null || globalConfig.getUrl().isEmpty())) {
            // Disable linking when the instance URL isn't set
            listener.getLogger().println("Your Jenkins URL is not set (or is set to localhost)! Disabling linking.");
            this.enableUrlLinking = false;
        }

        if (this.sendOnStateChange) {
            if (build.getResult().equals(build.getPreviousBuild().getResult())) {
                // Stops the webhook payload being created if the status is the same as the previous
                return true;
            }
        }

        boolean buildStatus = build.getResult().isBetterOrEqualTo(Result.SUCCESS);
        wh.setTitle(build.getProject().getDisplayName() + " #" + build.getId());

        String descriptionPrefix;

        // Adds links to the description and title if enableUrlLinking is enabled
        if (this.enableUrlLinking) {
            String url = globalConfig.getUrl() + build.getUrl();
            descriptionPrefix = "**Build:** "
                + getMarkdownHyperlink(build.getId(), url)
                + "\n**Status:** "
                + getMarkdownHyperlink(build.getResult().toString().toLowerCase(), url);
            wh.setURL(url);
        } else {
            descriptionPrefix = "**Build:** "
                    + build.getId()
                    + "\n**Status:** "
                    + build.getResult().toString().toLowerCase();
        }

        wh.setThumbnail(thumbnailURL);
        wh.setDescription(new EmbedDescription(build, globalConfig, descriptionPrefix, this.enableArtifactList).toString());
        wh.setStatus(buildStatus);

        if (this.enableFooterInfo) wh.setFooter("Jenkins v" + build.getHudsonVersion() + ", " + getDescriptor().getDisplayName() + " v" + getDescriptor().getVersion());

        try { wh.send(); }
        catch (WebhookException e) { e.printStackTrace(); }

        return true;
    }


    public BuildStepMonitor getRequiredMonitorService() { return BuildStepMonitor.NONE; }


    @Override
    public DescriptorImpl getDescriptor() { return (DescriptorImpl) super.getDescriptor(); }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public boolean isApplicable(Class<? extends AbstractProject> aClass) { return true; }

        public FormValidation doCheckWebhookURL(@QueryParameter String value) {
            if (!value.matches("https://discordapp\\.com/api/webhooks/\\d{18}/(\\w|-|_)*(/?)"))
                return FormValidation.error("Please enter a valid Discord webhook URL.");
            return FormValidation.ok();
        }

        public String getDisplayName() { return NAME; }

        public String getVersion() { return VERSION; }
    }
    
    private static String getMarkdownHyperlink(String content, String url) {
        return "[" + content + "](" + url + ")";
    }
}
