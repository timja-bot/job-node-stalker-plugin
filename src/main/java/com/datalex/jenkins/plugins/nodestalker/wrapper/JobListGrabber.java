package com.datalex.jenkins.plugins.nodestalker.wrapper;

import jenkins.model.Jenkins;

import java.util.Collection;

/**
 *
 * Grabs a list of all enabled jobs on the current Jenkins instance.
 *
 * @author Fabio Neves <fabio.neves@datalex.com>, Baris Batiege <baris.batiege@datalex.com>
 * @version 1.0
 */

public final class JobListGrabber {
    /**
     *  The list contained in JobListGrabber is used in config.jelly to populate the drop-down list on the UI
     */
    public static Collection<String> getJobs() {
        return Jenkins.getInstance().getJobNames();
    }
}
