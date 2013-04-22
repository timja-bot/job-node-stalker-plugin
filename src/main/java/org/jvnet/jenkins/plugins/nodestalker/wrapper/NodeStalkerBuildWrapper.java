package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.model.Messages;
import hudson.tasks.*;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: fabioneves
 * Date: 16/04/13
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
public class NodeStalkerBuildWrapper extends BuildWrapper {

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
            //TODO
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

    /**
     * TODO by baris
     * @param labelValue
     * @return
     */
    private String getJobLastRunNode(String labelValue) {
        String jobName = labelValue.split("-")[0].trim();
        Collection<? extends Job> jobs = Jenkins.getInstance().getItem(jobName).getAllJobs();
        for(Job job : jobs) {
            String nodeName = ((FreeStyleProject) job).getLastStableBuild().getBuiltOn().getNodeName() ;
            return nodeName.equals("")  ? "master" : nodeName;
        }
        return labelValue;
    }

    /**
     * <p>This method will check if the label that has come from the UI is related to another job.
     *    If it is, we have to get the real Job Name
     * </p>
     *
     * @param labelValue The label value that comes from the UI
     * @return true if labelValue ends with ' - Node', else false
     */
    public boolean shouldLookForParentNode(String labelValue) {
        return labelValue.indexOf(" - Node") > -1;
    }


    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        public DescriptorImpl() {
            super(NodeStalkerBuildWrapper.class);
        }

        public String getDisplayName() {
            return "Node Stalker Plugin";
        }

        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }
        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws hudson.model.Descriptor.FormException {
            String job = (String)json.get("job");
            // TODO Auto-generated method stub
            return super.configure(req, json);
        }

    }
}


