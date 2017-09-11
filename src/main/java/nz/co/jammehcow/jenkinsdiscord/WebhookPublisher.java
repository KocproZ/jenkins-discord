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
    private final boolean sendOnStateChange;
    private final boolean enableUrlLinking;
    private final boolean enableArtifactList;
    private static final String NAME = "Discord Notifier";
    private static final String VERSION = "1.0.0";

    @DataBoundConstructor
    public WebhookPublisher(String webhookURL, boolean sendOnStateChange, boolean enableUrlLinking, boolean enableArtifactList) {
        this.webhookURL = webhookURL;
        this.sendOnStateChange = sendOnStateChange;
        this.enableUrlLinking = enableUrlLinking;
        this.enableArtifactList = enableArtifactList;
    }

    public String getWebhookURL() { return this.webhookURL; }
    public boolean getSendOnStateChange() { return this.sendOnStateChange; }
    public boolean getEnableUrlLinking() { return this.enableUrlLinking; }
    public boolean getEnableArtifactList() { return this.enableArtifactList; }

    @Override
    public boolean needsToRunAfterFinalized() { return true; }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        JenkinsLocationConfiguration globalConfig = new JenkinsLocationConfiguration();

        if (this.webhookURL.isEmpty()) {
            listener.getLogger().println("The Discord webhook is not set!");
            return true;
        } else if (globalConfig.getUrl() == null || globalConfig.getUrl().isEmpty()) {
            listener.getLogger().println("Your Jenkins URL is not set (or is set to localhost)!");
            return true;
        }

        if (this.sendOnStateChange) {
            if (!build.getResult().equals(build.getPreviousBuild().getResult())) {
                // Stops the webhook payload being created if the status is the same as the previous
                return true;
            }
        }

        boolean buildStatus = build.getResult().isBetterOrEqualTo(Result.SUCCESS);

        DiscordWebhook wh = new DiscordWebhook(this.webhookURL);
        wh.setTitle(build.getProject().getDisplayName() + " #" + build.getId());

        String descriptionPrefix = "**Build:**  #" + build.getId() + "\n**Status:**  " + (build.getResult().toString().toLowerCase());

        wh.setDescription(new EmbedDescription(build, globalConfig, descriptionPrefix).toString());
        wh.setStatus(buildStatus);
        wh.setURL(globalConfig.getUrl() + build.getUrl());
        wh.setFooter("Jenkins v" + build.getHudsonVersion() + ", " + getDescriptor().getDisplayName() + " v" + getDescriptor().getVersion());

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
}
