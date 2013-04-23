package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.model.FreeStyleProject;
import hudson.model.Job;
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
    public static String getJobLastRunNode(String jobName) {
        Collection<? extends Job> jobs = Jenkins.getInstance().getItem(jobName).getAllJobs();
        for(Job job : jobs) {
            String nodeName = ((FreeStyleProject) job).getLastStableBuild().getBuiltOn().getNodeName() ;
            return nodeName.equals("")  ? "master" : nodeName;
        }
        return jobName;
    }
}
