package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
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
import java.util.Map;
import java.util.StringTokenizer;
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
    public static final String PLUGIN_DISPLAY_NAME = "[NODE STALKER] Run this job on the same node where another job has last ran";
    public static final String JOB_DOES_NOT_EXIST_PATTERN = "[NODE STALKER] The job %s does not exist! Please check your configuration!";
    private static final String JOB_HAS_NO_BUILD_PATTERN = "[NODE STALKER] The job %s has no traceable runs!";

    private String job;
    private boolean shareWorkspace;

    @DataBoundConstructor
    public NodeStalkerBuildWrapper(String job, boolean shareWorkspace) {
        this.job = job;
        this.shareWorkspace = shareWorkspace;
    }

    //required in order to restore the configuration values into the interface
    public String getJob() {
        return job == null ? "" : job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public boolean isShareWorkspace() {
        return shareWorkspace;
    }

    public void setShareWorkspace(boolean shareWorkspace) {
        this.shareWorkspace = shareWorkspace;
    }

    /**
     * @see hudson.tasks.BuildStep#getRequiredMonitorService()
     */
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {

        FreeStyleProject project = Util.getProject(job);
        final boolean shouldFail = project == null || project.getLastBuild() == null;

        if(shouldFail) {
            String pattern = project == null ? JOB_DOES_NOT_EXIST_PATTERN : JOB_HAS_NO_BUILD_PATTERN;
            String message = String.format(pattern, job);
            logger.warning(message);
            listener.getLogger().println(message);
        }

        return new Environment() {

            @Override
            public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
                if(shouldFail) {
                    return false;  // we return false because we want the job to fail!
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

        /**
         * Form validation method.
         *
         * Copied from hudson.tasks.BuildTrigger.doCheck(Item project, String value)
         */
        public FormValidation doCheckJob(@AncestorInPath Item job, @QueryParameter String value ) {
            if(!job.hasPermission(Item.CONFIGURE)){     // Require CONFIGURE permission on this project
                return FormValidation.ok();
            }
            StringTokenizer tokens = new StringTokenizer(hudson.Util.fixNull(value),",");
            boolean hasProjects = false;
            while(tokens.hasMoreTokens()) {
                String projectName = tokens.nextToken().trim();
                if (StringUtils.isNotBlank(projectName)) {
                    Item item = Jenkins.getInstance().getItem(projectName, job, Item.class); // only works after version 1.410
                    if(item == null) {
                        return FormValidation.error(Messages.BuildTrigger_NoSuchProject(projectName, AbstractProject.findNearest(projectName).getName()));
                    }
                    if(!(item instanceof AbstractProject)) {
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


