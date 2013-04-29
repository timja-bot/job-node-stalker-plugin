package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: fabioneves
 * Date: 29/04/13
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class MyNodeAssignementActionFunctionalTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();


    @Test
    public void testUpdateWorkspaceExistingFollowedProject() throws IOException, ExecutionException, InterruptedException {
        FreeStyleProject followed = j.createFreeStyleProject("JobA");
        FreeStyleBuild build = followed.scheduleBuild2(0).get();

        FreeStyleProject mockedProject = mock(FreeStyleProject.class);
        new MyNodeAssignmentAction().updateWorkspace(followed, mockedProject);
        verify(mockedProject, times(1)).setCustomWorkspace(build.getWorkspace().getRemote());
    }

//    @Test
//    public void testGetAssignedLabelNodeFound() throws IOException, ExecutionException, InterruptedException {
//        FreeStyleProject followed = j.createFreeStyleProject("JobA");
//        FreeStyleBuild build = followed.scheduleBuild2(0).get();
//
//
//    }


}
