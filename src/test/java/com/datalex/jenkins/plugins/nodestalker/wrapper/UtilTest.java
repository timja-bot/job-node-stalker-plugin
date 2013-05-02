package com.datalex.jenkins.plugins.nodestalker.wrapper;

import hudson.model.FreeStyleProject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;

/**
 * Author: Fabio Neves, Baris Batiege
 * Date: 29/04/13
 * Time: 10:53
 */
public class UtilTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test(expected = IllegalArgumentException.class)
    public void testGetProjectWithNull() throws Exception {
        Util.getProject(null);
    }

    @Test
    public void testGetProjectWithEmptyString() throws Exception {
        assertNull(Util.getProject(""));
    }

    @Test
    public void testJobFound() throws Exception {
        FreeStyleProject expected = j.createFreeStyleProject("JobA");
        FreeStyleProject project = Util.getProject("JobA");
        assertNotNull(project);
        assertNotNull(project);assertEquals(expected, project);
    }

    @Test
    public void testJobDoesNotExist() throws Exception {
        j.createFreeStyleProject("JobA");
        FreeStyleProject project = Util.getProject("JobB");
        assertNull(project);
    }
}
