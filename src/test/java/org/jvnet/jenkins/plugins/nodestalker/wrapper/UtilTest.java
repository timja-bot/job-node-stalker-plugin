package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertNull;

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
    public void testWithNullJob() throws Exception {
        String node = Util.getNodeJobLastRan(null);
    }

    @Test()
    public void testWithEmptyString() throws Exception {
        assertNull(Util.getNodeJobLastRan(""));
    }


}
