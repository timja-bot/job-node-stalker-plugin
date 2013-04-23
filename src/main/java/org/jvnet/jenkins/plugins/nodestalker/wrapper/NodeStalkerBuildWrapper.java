package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.*;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

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
        return new Environment() {
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

    }
}


