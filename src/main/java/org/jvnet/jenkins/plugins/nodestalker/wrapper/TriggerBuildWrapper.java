package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.BuildWrapper;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: fabioneves
 * Date: 16/04/13
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
public class TriggerBuildWrapper extends BuildWrapper {

    /**
     * @see hudson.tasks.BuildStep#getRequiredMonitorService()
     */
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        //TODO check if the plugin is enabled on this job, if not return the default environment
        //IF it is trigger a conditioned environment

        // trigger one build after the other
        return new TriggerNextBuildEnvironment();
    }


    /**
     * Environment triggering one build after the other - if the build result of
     * the previous build is as expected.
     */
    private class TriggerNextBuildEnvironment extends Environment {

        @Override
        public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
            triggerBuilds(build, listener);
            return true;
        }

        private void triggerBuilds(AbstractBuild build, BuildListener listener) {
        }

        /**
         * Decides whether the next build should be triggered.
         *
         * @param buildResult the current build result
         * @param runIfResult the definition when to trigger the next build
         * @return <code>true</code> if the next build should be triggered
         */
        private boolean shouldScheduleNextJob(Result buildResult, String runIfResult) {
            // If runIfResult is null, set it to "allCases".
            if (runIfResult == null) {
                runIfResult = "allCases";
            }
            // If runIfResult is "allCases", we're running regardless.
            if (runIfResult.equals("allCases")) {
                return true;
            } else {
                // Otherwise, we're going to need to compare against the build
                // result.

                if (runIfResult.equals("success")) {
                    return ((buildResult == null) || (buildResult.isBetterOrEqualTo(Result.SUCCESS)));
                } else if (runIfResult.equals("unstable")) {
                    return ((buildResult == null) || (buildResult.isBetterOrEqualTo(Result.UNSTABLE)));
                }
            }

            // If we get down here, something weird's going on. Return false.
            return false;
        }

    }

}
