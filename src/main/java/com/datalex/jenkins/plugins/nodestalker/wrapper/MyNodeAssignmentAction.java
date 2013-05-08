package com.datalex.jenkins.plugins.nodestalker.wrapper;

import hudson.model.*;
import hudson.model.labels.LabelAssignmentAction;
import hudson.model.queue.SubTask;
import org.apache.commons.lang.StringUtils;

import java.util.logging.Logger;

/**
 *
 * This is the main class of the plugin. This class checks whether the plugin is enabled, then takes in the node and
 * workspace locations of the last build of the parent job (the job specified by the user). It then restricts the current job to run on
 * that node and (if Share Workspace is enabled) workspace.
 *
 * @author Fabio Neves <fabio.neves@datalex.com>, Baris Batiege <baris.batiege@datalex.com>
 * @version 1.0
 */
public class MyNodeAssignmentAction implements LabelAssignmentAction {

    protected static final Logger logger = Logger.getLogger(MyNodeAssignmentAction.class.getName());
    public static final String DISPLAY_NAME = "NodeAssignmentAction";


    /*
    *  This method checks if the plugin is enabled then finds the node of the parent job. The method also checks whether
    *  Share Workspace option of the plugin is enabled.
    *
    *  @param
    *
    *
    *  @return
    *  The node that the last build of the parent job was built on
    *
    * */

    public Label getAssignedLabel(SubTask task) {
        if(!BuildableItemWithBuildWrappers.class.isAssignableFrom(task.getClass())) {
            return task.getAssignedLabel();
        }

        //Checking if the plugin is enabled on the job configuration. If no buildwrapper is returned we keep default Jenkins behaviour.

        NodeStalkerBuildWrapper buildWrapper = getNodeStalkerBuildWrapper((BuildableItemWithBuildWrappers)task);
        if(buildWrapper == null) {
            return task.getAssignedLabel();
        }

        // Otherwise we are going to find where the last build of the parent job occurred

        String jobName =  buildWrapper.getJob();
        String node = getNodeJobLastRan(Util.getProject(jobName), task.getAssignedLabel());

        if(!StringUtils.isEmpty(jobName) && buildWrapper.isShareWorkspace()) {
            AbstractProject followedProject = AbstractProject.findNearest(jobName);
            if(!followedProject.getName().equals(jobName)) {
                followedProject = null;
            }
            if(followedProject == null) {
                logger.warning(String.format("Could not get the job for %s. Custom workspace will not be set", jobName));
            } else {
                updateWorkspace(followedProject, (AbstractProject) task);
            }
        }
        return Label.get(node);
    }
         // Overriding the Custom Workspace field of our job to follow the workspace of the parent job
    protected void updateWorkspace(AbstractProject followedProject, AbstractProject project) {
        try {
            AbstractBuild build = followedProject.getSomeBuildWithWorkspace();
            if(build != null && build.getWorkspace() != null) {
                String workspace = build.getWorkspace().getRemote();
                project.setCustomWorkspace(workspace);
            }
        } catch (Exception e) {
            logger.severe("We could not set the parent workspace");
        }
    }

    /*
    *
    *
    *
    * @return
    *
    * */
    protected NodeStalkerBuildWrapper getNodeStalkerBuildWrapper(BuildableItemWithBuildWrappers project) {
        if(project.getBuildWrappersList() == null) {
            return null;
        }
        return  project.getBuildWrappersList().get(NodeStalkerBuildWrapper.class);
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


    /*
    *
    * This method sets node if it is an empty string, if node is currently null, it sets node to master.
    *
    * @param
    *
    *
    * @return
    * Null
    *
    * */
    protected String getNodeJobLastRan(AbstractProject project, Label defaultLabel) {
        if(project == null) {
            return defaultLabel != null ? defaultLabel.getDisplayName() : "master";
        }

        Node node = project.getLastBuiltOn();
        if(node != null) {
            return StringUtils.isEmpty(node.getNodeName()) ? "master" : node.getNodeName();
        }
        return null;
    }


}

