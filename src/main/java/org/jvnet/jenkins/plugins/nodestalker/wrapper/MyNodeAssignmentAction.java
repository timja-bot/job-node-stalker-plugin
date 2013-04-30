package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.model.*;
import hudson.model.labels.LabelAssignmentAction;
import hudson.model.queue.SubTask;
import hudson.tasks.BuildWrapper;
import jenkins.model.Jenkins;
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

    protected static final Logger logger = Logger.getLogger(MyNodeAssignmentAction.class.getName());
    public static final String DISPLAY_NAME = "NodeAssignmentAction";

    public Label getAssignedLabel(SubTask task) {
        //Checking if the plugin is enabled on the job configuration
        NodeStalkerBuildWrapper buildWrapper = getNodeStalkerBuildWrapper((FreeStyleProject)task);
        if(buildWrapper == null) { //if no buildwrapper is returned we need to keep jenkins default behaviour
            return task.getAssignedLabel();
        }
        //otherwise we are going to calculate where the parent job last run occurred
        String jobName =  buildWrapper.getJob();
        String node = getNodeJobLastRan(Util.getProject(jobName));

        if(!StringUtils.isEmpty(jobName) && buildWrapper.isShareWorkspace()) {
            AbstractProject followedProject = AbstractProject.findNearest(jobName);
            if(!followedProject.getName().equals(jobName)) {
                followedProject = null;
            }
            if(followedProject == null) {
                logger.warning(String.format("Could not get the job for %s. Custom workspace will not be set", jobName));
            } else {
                updateWorkspace(followedProject, (FreeStyleProject) task);
            }
        }
        return Label.get(node);
    }

    protected void updateWorkspace(AbstractProject followedProject, FreeStyleProject project) {
        try {
            AbstractBuild build = followedProject.getSomeBuildWithWorkspace();
            if(build != null) {
                String workspace = build.getWorkspace().getRemote();
                project.setCustomWorkspace(workspace);
            }
        } catch (Exception e) {
            logger.severe("We could not set the parent workspace");
        }
    }

    protected NodeStalkerBuildWrapper getNodeStalkerBuildWrapper(FreeStyleProject project) {
        Collection<BuildWrapper> buildWrappers = project.getBuildWrappers().values();
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


    /**
     *
     * @param project
     * @return
     */
    protected String getNodeJobLastRan(FreeStyleProject project) {
        if(project == null) {
            return null;
        }
        Build build = project.getLastBuild();
        if(build !=  null) { //this will check if the job was ever built
            String nodeName = build.getBuiltOn().getNodeName() ;
            return nodeName.equals("")  ? "master" : nodeName;
        }
        return null;
    }


}

