package nz.co.jammehcow.jenkinsdiscord;

import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

/**
 * Author: jammehcow.
 * Date: 22/04/17.
 */

public class BuildListener extends RunListener<AbstractBuild> {
    /*@Override
    public void onCompleted(AbstractBuild abstractBuild, TaskListener listener) {
        new BuildmetricsNotifier().onCompleted(abstractBuild, listener);
    }*/
}
