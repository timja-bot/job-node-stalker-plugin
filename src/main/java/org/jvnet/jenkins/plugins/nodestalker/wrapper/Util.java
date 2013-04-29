package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: fabioneves
 * Date: 29/04/13
 * Time: 10:52
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    public static FreeStyleProject getProject(String jobName) {
        if(jobName == null) {
            throw new IllegalArgumentException();
        }

        TopLevelItem item = Jenkins.getInstance().getItem(jobName);
        if(item == null) {   //any node will be okay since the main job does not exist
            return null;
        }
        Collection<? extends Job> projects = item.getAllJobs();
        return (FreeStyleProject)projects.toArray()[0];
    }
}
