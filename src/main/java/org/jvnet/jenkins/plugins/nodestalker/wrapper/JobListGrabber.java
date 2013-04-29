package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import jenkins.model.Jenkins;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: barisbatiege
 * Date: 4/18/13
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class JobListGrabber {

    public static Collection<String> getJobs() {
        return Jenkins.getInstance().getJobNames();
    }
}
