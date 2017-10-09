package nz.co.jammehcow.jenkinsdiscord;

import hudson.Extension;
import hudson.model.TaskListener;
import nz.co.jammehcow.jenkinsdiscord.exception.WebhookException;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class DiscordPipelineStep extends AbstractStepImpl {
    private final String webhookURL;

    private String title;
    private String link;
    private String description;
    private String footer;
    private boolean successful;

    @DataBoundConstructor
    public DiscordPipelineStep(String webhookURL) {
        this.webhookURL = webhookURL;
    }

    public String getWebhookURL() {
        return webhookURL;
    }

    public String getTitle() {
        return title;
    }

    @DataBoundSetter
    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    @DataBoundSetter
    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    @DataBoundSetter
    public void setDescription(String description) {
        this.description = description;
    }

    public String getFooter() {
        return footer;
    }

    @DataBoundSetter
    public void setFooter(String footer) {
        this.footer = footer;
    }

    public boolean isSuccessful() {
        return successful;
    }

    @DataBoundSetter
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public static class DiscordPipelineStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {
        @Inject
        transient DiscordPipelineStep step;

        @StepContextParameter
        private transient TaskListener listener;

        @Override
        protected Void run() throws Exception {
            listener.getLogger().println("Sending notification to Discord.");

            DiscordWebhook wh = new DiscordWebhook(step.getWebhookURL());
            wh.setTitle(step.getTitle());
            wh.setURL(step.getLink());
            wh.setDescription(step.getDescription());
            wh.setFooter(step.getFooter());
            wh.setStatus(step.isSuccessful());

            try { wh.send(); }
            catch (WebhookException e) { e.printStackTrace(); }

            return null;
        }

        private static final long serialVersionUID = 1L;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {
        public DescriptorImpl() { super(DiscordPipelineStepExecution.class); }

        @Override
        public String getFunctionName() {
            return "discordSend";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Send an embed message to Webhook URL";
        }
    }
}
