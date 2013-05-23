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


    /**
     *  This method checks if the plugin is enabled then finds the node of the parent job.
     *  If the plugin is not enabled, the default label set by Jenkins is used.
     *  The method also checks whether Share Workspace option of the plugin is enabled.
     *
     *  @param task The current build of our job
     *
     *  @return The node that the last build of the parent job was built on.
     *
     */
    public Label getAssignedLabel(SubTask task) {
        if(!BuildableItemWithBuildWrappers.class.isAssignableFrom(task.getClass())) {
            return task.getAssignedLabel();
        }

        NodeStalkerBuildWrapper buildWrapper = getNodeStalkerBuildWrapper((BuildableItemWithBuildWrappers)task);


        if(buildWrapper == null) {
            return task.getAssignedLabel();
        }

        String jobName =  buildWrapper.getJob();
        String node = getNodeJobLastRan(Util.getProject(jobName), task.getAssignedLabel());


        if(!StringUtils.isEmpty(jobName) && buildWrapper.isShareWorkspace()) {
            AbstractProject followedProject = AbstractProject.findNearest(jobName);

            AbstractProject currentProject = (AbstractProject) task;
            String workspaceValue = currentProject.getCustomWorkspace();
            buildWrapper.setOldCustomWorkspace(workspaceValue);
            if(!followedProject.getName().equals(jobName)) {
                followedProject = null;
            }
            if(followedProject == null) {
                logger.warning(String.format("Could not get the job for %s. Custom workspace will not be set", jobName));
            } else {
                updateWorkspace(followedProject, currentProject);
            }
        }
        return Label.get(node);
    }


    /**
     * Overriding the Custom Workspace field of our job to follow the workspace of the parent job
     *
     *  @param followedProject the project specified in the UI
     *  @param project The project that is currently building
     */
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

    /**
     * This method checks current jobs configuration to see if NodeStalkerPlugin is enabled. It does this by checking
     * if NodeStalkerPlugin exists in the jobs buildWrappersList, as NodeStalkerPlugin is a plugin of the buildWrappers
     * category.
     *
     * @param project the job that is currently running
     * @return <ul>
     *          <li>Returns null if the projects BuildWrapperList does not exist</li>
     *          <li>Returns NodeStalkerBuildWrapper if the projects BuildWrapperList is found and contains NodeStalkerPlugin </li>
     *         </ul>
     *
     */
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


    /**
     * This method sets node if it is an empty string, if node is currently null, it sets node to master.
     *
     * @param project job that we want to follow
     * @param defaultLabel label that Jenkins would assign without Node Stalker plugin
     * @return  <ul>
     *            <li>Normally returns the node that the followed job most recently ran on</li>
     *            <li>returns DisplayName of defaultLabel if job can't be found, but defaultLabel exists</li>
     *            <li>returns 'master' if job can't be found and defaultLabel is null </li>
     *            <li>If 'node' is empty, returns master</li>
     *            <li>If 'node' is null, returns null</li>
     *          </ul>
     */
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

