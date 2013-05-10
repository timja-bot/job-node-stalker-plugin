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
     * Throws an illegal exception and sets node to be followed to null if the user enters an invalid job name.
     * @param jobName Name of job to be followed
     * @return Puts project list into an array
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
