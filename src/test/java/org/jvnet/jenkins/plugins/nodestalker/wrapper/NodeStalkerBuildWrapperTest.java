package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.tasks.BuildStepMonitor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: fabioneves
 * Date: 01/05/13
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 */
public class NodeStalkerBuildWrapperTest {

    @Test
    public void testSetJob() {
        NodeStalkerBuildWrapper buildWrapper = new NodeStalkerBuildWrapper("",false);
        buildWrapper.setJob("JobTest");
        assertEquals("JobTest", buildWrapper.getJob());
    }

    @Test
    public void testSetShareWorkspace() {
        NodeStalkerBuildWrapper buildWrapper = new NodeStalkerBuildWrapper("",false);
        buildWrapper.setShareWorkspace(true);
        assertTrue(buildWrapper.isShareWorkspace());
    }

    @Test
    public void testBuildStepConstant() {
        NodeStalkerBuildWrapper buildWrapper = new NodeStalkerBuildWrapper("",false);
        assertEquals(BuildStepMonitor.BUILD, buildWrapper.getRequiredMonitorService());

    }

}


