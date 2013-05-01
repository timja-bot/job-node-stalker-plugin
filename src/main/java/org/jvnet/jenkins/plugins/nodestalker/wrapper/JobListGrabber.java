package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import jenkins.model.Jenkins;
import java.util.Collection;

public final class JobListGrabber {

    public static Collection<String> getJobs() {
        return Jenkins.getInstance().getJobNames();
    }
}
