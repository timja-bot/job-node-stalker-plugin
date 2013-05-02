package com.datalex.jenkins.plugins.nodestalker.wrapper;

import hudson.tasks.BuildStepMonitor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Author: Fabio Neves, Baris Batiege
 * Date: 01/05/13
 * Time: 14:52
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


