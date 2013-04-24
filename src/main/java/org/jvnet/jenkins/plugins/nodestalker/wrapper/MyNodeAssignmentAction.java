package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.model.*;
import hudson.model.labels.LabelAssignmentAction;
import hudson.model.queue.SubTask;
import hudson.tasks.BuildWrapper;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: fabioneves
 * Date: 23/04/13
 * Time: 12:36
 * To change this template use File | Settings | File Templates.
 */
public class MyNodeAssignmentAction implements LabelAssignmentAction {

    private static final Logger logger = Logger.getLogger(MyNodeAssignmentAction.class.getName());
    public static final String DISPLAY_NAME = "NodeAssignmentAction";

    public Label getAssignedLabel(SubTask task) {
        //Checking if the plugin is enabled on the job configuration
        NodeStalkerBuildWrapper buildWrapper = getNodeStalkerBuildWrapper(task);
        if(buildWrapper == null) { //if no buildwrapper is returned we need to keep jenkins default behaviour
            return task.getAssignedLabel();
        }
        //otherwise we are going to calculate where the parent job last run occurred
        String jobName =  buildWrapper.getJob();
        String node = Util.getNodeJobLastRan(jobName);

        if(!StringUtils.isEmpty(jobName) && buildWrapper.isShareWorkspace()) {
            updateWorkspace((FreeStyleProject) task, jobName);
        }

        return Label.get(node);
    }

    private void updateWorkspace(FreeStyleProject project, String jobName) {
        try {

            AbstractProject followedProject = AbstractProject.findNearest(jobName);
            if(followedProject == null) {
                logger.warning(String.format("Could not get the job for %s. Custom workspace will not be set", jobName));
                return;
            }
            String workspace = followedProject.getSomeBuildWithWorkspace().getWorkspace().getRemote();
            logger.info(String.format("Workspace %s", workspace)); //TODO remove this later...
            project.setCustomWorkspace(workspace);

        } catch (Exception e) {
            logger.severe("We could not set the parent workspace");
        }
    }

    private Job getJob(SubTask task) {
        Collection<? extends Job> jobs  =  ((FreeStyleProject) task).getAllJobs();
        if(jobs == null || jobs.size() != 1) {
            logger.info(String.format("Using the system assigned label %s",
                    ((FreeStyleProject) task).getAssignedLabelString()));
            return null;
        }
        return (Job) jobs.toArray()[0];
    }

    private NodeStalkerBuildWrapper getNodeStalkerBuildWrapper(SubTask task) {
        Job job = getJob(task);
        if(job == null) {
            return null;
        }
        Collection<BuildWrapper> buildWrappers = ((FreeStyleProject) job).getBuildWrappers().values();
        for(BuildWrapper buildWrapper : buildWrappers) {
            if(buildWrapper.getClass().equals(NodeStalkerBuildWrapper.class)) {
                return (NodeStalkerBuildWrapper) buildWrapper;
            }
        }
        return null;
    }

    public String getIconFileName() {
        return null; //no need for an icon
    }

    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    public String getUrlName() {
        return null; //no need for an url name
    }
}

