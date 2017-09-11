package nz.co.jammehcow.jenkinsdiscord.util;

import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.scm.ChangeLogSet;
import jenkins.model.JenkinsLocationConfiguration;

import java.util.LinkedList;
import java.util.List;

/**
 * @author jammehcow
 */

public class EmbedDescription {
    private static final int maxEmbedStringLength = 2048; // The maximum length of an embed description.

    private LinkedList<String> changesList = new LinkedList<>();
    private LinkedList<String> artifactsList = new LinkedList<>();
    private String prefix;
    private String finalDescription;

    public EmbedDescription(AbstractBuild build, JenkinsLocationConfiguration globalConfig, String prefix) {
        String artifactsURL = globalConfig.getUrl() + build.getUrl() + "artifact/";
        this.prefix = prefix;
        this.changesList.add("\n**Changes:**\n");
        this.artifactsList.add("\n**Artifacts:**\n");
        Object[] changes = build.getChangeSet().getItems();

        if (changes.length == 0) {
            this.changesList.add("\n*No changes.*\n");
        } else {
            for (Object o : changes) {
                ChangeLogSet.Entry entry = (ChangeLogSet.Entry) o;
                String commitID = (entry.getParent().getKind().equalsIgnoreCase("svn")) ? entry.getCommitId() : entry.getCommitId().substring(0, 6);

                this.changesList.add("   - ``" + commitID + "`` *" + entry.getMsg() + " - " + entry.getAuthor().getFullName() + "*\n");
            }
        }

        List<Run.Artifact> artifacts = build.getArtifacts();
        if (artifacts.size() == 0) {
            this.artifactsList.add("\n*No artifacts to be found.*");
        } else {
            for (Run.Artifact artifact : artifacts) {
                this.artifactsList.add(" - " + artifactsURL + artifact.getHref() + "\n");
            }
        }

        while (this.getCurrentDescription().length() > maxEmbedStringLength) {
            if (this.changesList.size() > 5) {
                // Dwindle the changes list down to 5 changes.
                while (this.changesList.size() != 5) this.changesList.removeLast();
            } else if (this.artifactsList.size() > 1) {
                this.artifactsList.clear();
                this.artifactsList.add(artifactsURL);
            } else {
                // Worst case scenario: truncate the description.
                this.finalDescription = this.getCurrentDescription().substring(0, maxEmbedStringLength - 1);
                return;
            }
        }

        this.finalDescription = this.getCurrentDescription();
    }

    private String getCurrentDescription() {
        StringBuilder description = new StringBuilder();
        description.append(this.prefix);

        // Collate the changes and artifacts into the description.
        for (String changeEntry : this.changesList) description.append(changeEntry);
        for (String artifact : this.artifactsList) description.append(artifact);

        return description.toString();
    }

    @Override
    public String toString() {
        return this.finalDescription;
    }
}
