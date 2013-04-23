package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.model.Label;
import hudson.model.labels.LabelAssignmentAction;
import hudson.model.queue.SubTask;
import hudson.tasks.BuildWrapper;

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
        String node = Util.getJobLastRunNode(buildWrapper.getJob());
        return Label.get(node);
    }

    private NodeStalkerBuildWrapper getNodeStalkerBuildWrapper(SubTask task) {
        Collection<? extends Job> jobs  =  ((FreeStyleProject) task).getAllJobs();
        if(jobs == null || jobs.size() != 1) {
            logger.info(String.format("Using the system assigned label %s",
                    ((FreeStyleProject) task).getAssignedLabelString()));
            return null;
        }

        Job job = (Job) jobs.toArray()[0];
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

