package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: fabioneves
 * Date: 23/04/13
 * Time: 13:42
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    /**
     * TODO by baris
     * @param jobName
     * @return
     */
    public static String getNodeJobLastRan(String jobName) {
        TopLevelItem item = Jenkins.getInstance().getItem(jobName);
        if(item == null) {
             return null; //any node will be okay since the main job does not exist
        }

        Collection<? extends Job> jobs = item.getAllJobs();
        for(Job job : jobs) {
            String nodeName = ((FreeStyleProject) job).getLastStableBuild().getBuiltOn().getNodeName() ;
            return nodeName.equals("")  ? "master" : nodeName;
        }
        return jobName;
    }
}
