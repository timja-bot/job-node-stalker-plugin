package com.datalex.jenkins.plugins.nodestalker.wrapper;

import hudson.model.Item;
import hudson.model.TopLevelItem;
import hudson.model.AbstractProject;
import hudson.model.Job;

import java.util.Collection;
import java.util.logging.Logger;

import jenkins.model.Jenkins;

/**
 *
 * This class throws an illegal exception and sets node to be followed to null if the user enters an invalid job name.
 *
 * @author Fabio Neves <fabio.neves@datalex.com>, Baris Batiege <baris.batiege@datalex.com>
 * @version 1.0
 */


public final class Util {
    protected static final Logger logger = Logger.getLogger(Util.class.getName());

    public static AbstractProject getProject(String jobName) {
        if(jobName == null) {
            throw new IllegalArgumentException();
        }

        Item item = Jenkins.getInstance().getItemByFullName(jobName);
        if(item == null) {   //any node will be okay since the main job does not exist
            logger.warning(String.format("Cannot find job %s", jobName));
            return null;
        }
        if (!(item instanceof TopLevelItem)) {
            logger.warning(String.format("Found job %s, but it is not instance of TopLevelItem. Custom workspace will not be set", jobName));
            return null;
        }

        Collection<? extends Job> projects = item.getAllJobs();
        return (AbstractProject) projects.toArray()[0];
    }
}
