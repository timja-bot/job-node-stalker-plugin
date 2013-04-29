package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.model.Build;
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
     *
     * @param jobName
     * @return
     */
    public static String getNodeJobLastRan(String jobName) {
        FreeStyleProject job = getJob(jobName);
        if(job == null) {
            return null;
        }
        Build build =  ((FreeStyleProject) job).getLastBuild();
        if(build !=  null) { //this will check if the job was ever built
            String nodeName = build.getBuiltOn().getNodeName() ;
            return nodeName.equals("")  ? "master" : nodeName;
        }
        return null;
    }

    public static FreeStyleProject getJob(String jobName) {
        if(jobName == null) {
            throw new IllegalArgumentException();
        }

        TopLevelItem item = Jenkins.getInstance().getItem(jobName);
        if(item == null) {   //any node will be okay since the main job does not exist
            return null;
        }
        Collection<? extends Job> jobs = item.getAllJobs();
        if(jobs.size() == 0) throw new IllegalStateException("This will never happen!");
        return (FreeStyleProject)jobs.toArray()[0];
    }


}
