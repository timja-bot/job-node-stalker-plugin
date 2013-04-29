package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.model.*;
import hudson.tasks.BuildWrapper;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: fabioneves
 * Date: 29/04/13
 * Time: 10:35
 * To change this template use File | Settings | File Templates.
 */
public class MyNodeAssignmentActionTest {

    /**
     *
     * @param nodeName <p>The node where the mock will return that will run. If its empty means the master,
     *                 if null means no run has ocurred for that job</p>
     * @return
     */
    private FreeStyleProject createMock(String nodeName) {
        FreeStyleProject project = mock(FreeStyleProject.class);
        if(nodeName != null) {
            FreeStyleBuild build = mock(FreeStyleBuild.class);
            Node node = mock(Node.class);
            when(node.getNodeName()).thenReturn(nodeName);
            when(build.getBuiltOn()).thenReturn(node);
            when(project.getLastBuild()).thenReturn(build);
        }
        return project;
    }

    @Test
    public void testFollowedJobRanOnNode1() throws Exception {
        FreeStyleProject project = createMock("Node1");
        String nodeName = new MyNodeAssignmentAction().getNodeJobLastRan(project);
        assertEquals("Node1", nodeName);
    }

    @Test
    public void testRanOnMaster() throws Exception {
        FreeStyleProject project = createMock("");
        String node = new MyNodeAssignmentAction().getNodeJobLastRan(project);
        assertEquals("master", node);
    }

    @Test
    public void testInexistantProject() throws Exception {
        String node = new MyNodeAssignmentAction().getNodeJobLastRan(null);
        assertNull(node);
    }

    @Test
    public void testNoPreviousBuild() throws Exception {
        FreeStyleProject project = createMock(null);
        String node = new MyNodeAssignmentAction().getNodeJobLastRan(project);
        assertNull(node);
    }

    @Test
    public void testGetIconFileName() {
        assertNull(new MyNodeAssignmentAction().getIconFileName());
    }

    @Test
    public void testGetDisplayName() {
        assertEquals(MyNodeAssignmentAction.DISPLAY_NAME, new MyNodeAssignmentAction().getDisplayName());
    }

    @Test
    public void testGetUrlName() {
        assertNull(new MyNodeAssignmentAction().getUrlName());
    }

    @Test
    public void testCheckBuildWrapperIsPresent() {
        FreeStyleProject task = mock(FreeStyleProject.class);
        NodeStalkerBuildWrapper mockedBuildWrapper = new NodeStalkerBuildWrapper("JobA", false);
        Map<Descriptor<BuildWrapper>, BuildWrapper> buildWrappers= new HashMap<Descriptor<BuildWrapper>, BuildWrapper>();
        buildWrappers.put(NodeStalkerBuildWrapper.DESCRIPTOR, mockedBuildWrapper);
        when(task.getBuildWrappers()).thenReturn(buildWrappers);
        BuildWrapper result = new MyNodeAssignmentAction().getNodeStalkerBuildWrapper(task);
        assertNotNull(result);
        assertEquals(mockedBuildWrapper, result);
    }

    @Test
    public void testCheckNoBuildWrapperIsPresent() {
        FreeStyleProject task = mock(FreeStyleProject.class);
        Map<Descriptor<BuildWrapper>, BuildWrapper> buildWrappers= new HashMap<Descriptor<BuildWrapper>, BuildWrapper>();
        when(task.getBuildWrappers()).thenReturn(buildWrappers);
        BuildWrapper result = new MyNodeAssignmentAction().getNodeStalkerBuildWrapper(task);
        assertNull(result);
    }

    @Test
    public void testCheckBuildWrappersIsNull() {
        FreeStyleProject task = mock(FreeStyleProject.class);
        BuildWrapper result = new MyNodeAssignmentAction().getNodeStalkerBuildWrapper(task);
        assertNull(result);
    }

}
