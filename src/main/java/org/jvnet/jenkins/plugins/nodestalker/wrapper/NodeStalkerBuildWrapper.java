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

    private String job;

    @DataBoundConstructor
    public NodeStalkerBuildWrapper(String job) {
        this.job = job;
    }

    public String getJob() {
        return job;
    }

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
        //IF it is trigger a conditioned environment
        logger.info("The job run has started! Now we will do some Magic!!");
        String node = getJobLastRunNode(job);
        build.getProject().setAssignedLabel(Label.get(node));
        return new Environment() {
            @Override
            public boolean tearDown(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
                return super.tearDown(build, listener);    //To change body of overridden methods use File | Settings | File Templates.
            }
        };
    }


    /**
     * TODO by baris
     * @param jobName
     * @return
     */
    private String getJobLastRunNode(String jobName) {
        Collection<? extends Job> jobs = Jenkins.getInstance().getItem(jobName).getAllJobs();
        for(Job job : jobs) {
            String nodeName = ((FreeStyleProject) job).getLastStableBuild().getBuiltOn().getNodeName() ;
            return nodeName.equals("")  ? "master" : nodeName;
        }
        return jobName;
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


