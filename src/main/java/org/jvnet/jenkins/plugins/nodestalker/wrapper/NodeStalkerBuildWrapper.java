package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.*;
import hudson.tasks.Messages;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import hudson.Util;
/**
 * Created with IntelliJ IDEA.
 * User: fabioneves
 * Date: 16/04/13
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
public class NodeStalkerBuildWrapper extends BuildWrapper {

    private static final Logger logger = Logger.getLogger(NodeStalkerBuildWrapper.class.getName());
    public static final String PLUGIN_DISPLAY_NAME = "Node Stalker Plugin";

    private String job;

    @DataBoundConstructor
    public NodeStalkerBuildWrapper(String job) {
        this.job = job;
    }

    //this is required in order to restore the configuration value into the interface
    public String getJob() {
        return job;
    }

    //this is required in order to be able to update a configuration of a job with node stalker plugin
    public void setJob(String job) {
        this.job = job;
    }

    /**
     * @see hudson.tasks.BuildStep#getRequiredMonitorService()
     */
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        if(!Jenkins.getInstance().getJobNames().contains(job)) {
            listener.getLogger().println(String.format("The job %s does not exist! Please check your configuration!", job));
        }

        return new Environment() {

            @Override
            public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
                if(!Jenkins.getInstance().getJobNames().contains(job)) {
                    return false;
                }
                return super.tearDown(build, listener);    //To change body of overridden methods use File | Settings | File Templates.
            }
        };
    }

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();


    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        public DescriptorImpl() {
            super(NodeStalkerBuildWrapper.class);
        }

        public String getDisplayName() {
            return PLUGIN_DISPLAY_NAME;
        }

        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }
        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws hudson.model.Descriptor.FormException {
            return super.configure(req, json);
        }

        /**
         * Form validation method.
         *
         * Copied from hudson.tasks.BuildTrigger.doCheck(Item project, String value)
         */
        public FormValidation doCheckJob(@AncestorInPath Item job, @QueryParameter String value ) {
            // Require CONFIGURE permission on this project
            if(!job.hasPermission(Item.CONFIGURE)){
                return FormValidation.ok();
            }
            StringTokenizer tokens = new StringTokenizer(Util.fixNull(value),",");
            boolean hasProjects = false;
            while(tokens.hasMoreTokens()) {
                String projectName = tokens.nextToken().trim();
                if (StringUtils.isNotBlank(projectName)) {
                    Item item = Jenkins.getInstance().getItem(projectName, job, Item.class); // only works after version 1.410
                    if(item==null){
                        return FormValidation.error(Messages.BuildTrigger_NoSuchProject(projectName, AbstractProject.findNearest(projectName).getName()));
                    }
                    if(!(item instanceof AbstractProject)){
                        return FormValidation.error(Messages.BuildTrigger_NotBuildable(projectName));
                    }
                    hasProjects = true;
                }
            }
            if (!hasProjects) {
                return FormValidation.error("No project specified");
            }

            return FormValidation.ok();
        }

    }
}


