package com.datalex.jenkins.plugins.nodestalker.wrapper;

import jenkins.model.Jenkins;

import java.util.Collection;

/**
 * Author: Fabio Neves, Baris Batiege
 * Date: 4/26/13
 * Time: 2:59 PM
 */

public final class JobListGrabber {

    public static Collection<String> getJobs() {
        return Jenkins.getInstance().getJobNames();
    }
}
