package com.datalex.jenkins.plugins.nodestalker.wrapper;

import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;

import java.util.Collection;

/**
 * Helper class with methods to access a project based on its name.
 *
 * @author Fabio Neves <fabio.neves@datalex.com>, Baris Batiege <baris.batiege@datalex.com>
 * @version 1.0
 */


public final class Util {

    /**
     * Retrieves the project associated with the provided job name.
     *
     * @param jobName Name of job to be followed
     * @return Project associated with the job name, returns null if project related to job name can not be found
     */
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
