package nz.co.jammehcow.jenkinsdiscord;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * Author: jammehcow.
 * Date: 22/04/17.
 */

public class WebhookPublisher extends Notifier {
    private final String webhookURL;

    @DataBoundConstructor
    public WebhookPublisher(String webhookURL) { this.webhookURL = webhookURL; }

    public String getWebhookURL() { return this.webhookURL; }

    @Override
    public boolean needsToRunAfterFinalized() { return true; }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        System.out.println("Inside 1");

        if (this.webhookURL.isEmpty()) {
            listener.getLogger().println("Discord webhook is not set!");
            return true;
        }

        System.out.println("Inside 2");

        DiscordWebhook wh = new DiscordWebhook(this.webhookURL);
        wh.setTitle(build.getDisplayName());
        wh.setDescription(build.getBuildStatusSummary().message);
        wh.setStatus(build.getResult().isCompleteBuild());
        wh.setURL(build.getUrl());
        wh.send();

        return true;
    }


    public BuildStepMonitor getRequiredMonitorService() { return BuildStepMonitor.NONE; }


    @Override
    public DescriptorImpl getDescriptor() { return (DescriptorImpl) super.getDescriptor(); }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {


        public boolean isApplicable(Class<? extends AbstractProject> aClass) { return true; }

        public FormValidation doCheckWebhookURL(@QueryParameter String value) {
            // TODO: regex tester.
            if (!value.startsWith("https://discordapp.com/api/webhooks/"))
                return FormValidation.error("Please enter a valid Discord webhook URL.");
            return FormValidation.ok();
        }

        public String getDisplayName() { return "Discord Webhook"; }

    }
}
