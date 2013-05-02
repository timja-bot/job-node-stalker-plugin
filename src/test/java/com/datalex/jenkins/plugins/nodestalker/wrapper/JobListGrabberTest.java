package com.datalex.jenkins.plugins.nodestalker.wrapper;

import hudson.model.FreeStyleProject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Author: Fabio Neves, Baris Batiege
 * Date: 4/26/13
 * Time: 2:59 PM
 */
public class JobListGrabberTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void testGetJobs() throws Exception {
        Collection<String> jobNamesList = JobListGrabber.getJobs();
        assertNotNull(jobNamesList);
        assertTrue(jobNamesList.isEmpty());
    }

    @Test
    public void testGetJobsWithOneJob() throws Exception {
        FreeStyleProject parent = j.createFreeStyleProject("JobA");
        Collection<String> jobNamesList = JobListGrabber.getJobs();
        assertNotNull(jobNamesList);
        assertEquals(1, jobNamesList.size());
        String elem = (String)jobNamesList.toArray()[0];
        assertEquals("JobA", elem);
    }

    @Test
    public void testGetJobsWithSeveralJobs() throws Exception {
        j.createFreeStyleProject("JobA");
        j.createFreeStyleProject("JobB");
        j.createFreeStyleProject("JobC");
        j.createFreeStyleProject("JobD");
        Collection<String> jobNamesList = JobListGrabber.getJobs();
        assertNotNull(jobNamesList);
        assertEquals(4, jobNamesList.size());
        String jobA = (String)jobNamesList.toArray()[0];
        String jobB = (String)jobNamesList.toArray()[1];
        String jobC = (String)jobNamesList.toArray()[2];
        String jobD = (String)jobNamesList.toArray()[3];
        assertEquals("JobA", jobA);
        assertEquals("JobB", jobB);
        assertEquals("JobC", jobC);
        assertEquals("JobD", jobD);
    }
}
