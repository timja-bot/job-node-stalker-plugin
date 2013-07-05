package com.datalex.jenkins.plugins.nodestalker.wrapper;

import hudson.model.TopLevelItem;
import hudson.model.AbstractProject;
import hudson.model.Job;

import java.util.Collection;

import jenkins.model.Jenkins;

/**
 *
 * This class throws an illegal exception and sets node to be followed to null if the user enters an invalid job name.
 *
 * @author Fabio Neves <fabio.neves@datalex.com>, Baris Batiege <baris.batiege@datalex.com>
 * @version 1.0
 */


public final class Util {

    public static AbstractProject getProject(String jobName) {
        if(jobName == null) {
            throw new IllegalArgumentException();
        }

        TopLevelItem item = Jenkins.getInstance().getItem(jobName);
        if(item == null) {   //any node will be okay since the main job does not exist
            return null;
        }
        Collection<? extends Job> projects = item.getAllJobs();
        return (AbstractProject) projects.toArray()[0];
    }
}
