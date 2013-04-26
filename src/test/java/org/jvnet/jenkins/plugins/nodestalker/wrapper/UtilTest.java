package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.model.Project;
import hudson.model.TaskListener;
import hudson.model.labels.LabelAtom;
import hudson.slaves.DumbSlave;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: barisbatiege
 * Date: 4/24/13
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class UtilTest  {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test(expected = IllegalArgumentException.class)
    public void testWithNullNode() throws Exception {
        String node = Util.getNodeJobLastRan(null);
    }

    @Test()
    public void testNodeWithEmptyString() throws Exception {
        assertNull(Util.getNodeJobLastRan(""));
    }

    @Test()
    public void testWithNullWorkspace() throws Exception {

        final String paramName = "node";
        final String nodeName = "someNode" + System.currentTimeMillis();

        //create a node -> node1
        DumbSlave slave = createOnlineSlave(new LabelAtom(nodeName));
        //create a project  - JobA
        //FreeStyleProject project = Jenkins.getInstance().createProject();
        Project<?, ?> projectA = createFreeStyleProject("projectA");
        //schedule a run on that node
        setupBuild();
        //test


        String node = Util.getNodeJobLastRan("*");
        assertNotNull(node);
        assertEquals("*", node);
    }

    private void setupBuild() throws IOException, InterruptedException {
        when(build.getEnvironment(any(TaskListener.class))).thenReturn(new EnvVars());
        when(listener.getLogger()).thenReturn(listenerLogger);
    }


}
