package systemtests;

import com.datalex.jenkins.plugins.nodestalker.wrapper.NodeStalkerBuildWrapper;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.model.Label;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;


/**
 * Created with IntelliJ IDEA.
 * User: fabioneves
 * Date: 30/04/13
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public class TestUI {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    public void updateProjectConfig(HtmlPage page, String jobnameToFollow, boolean shareWorkspace) {
        page.getElementByName("_.job").setAttribute("value", jobnameToFollow);
        ((HtmlCheckBoxInput) page.getElementByName("_.shareWorkspace")).setChecked(shareWorkspace);
    }

    public void assertPluginUI(HtmlPage page, String jobNameToFollow, boolean expectedShareWorkspace)
            throws IOException, SAXException {
        boolean hasSettingInputClass = page.getElementByName("_.job").getAttribute("class").contains("setting-input");
        String followJobValue = page.getElementByName("_.job").getAttribute("value");
        boolean shareWorkspace = ((HtmlCheckBoxInput) page.getElementByName("_.shareWorkspace")).isChecked();
        assertNotNull(followJobValue);
        assertEquals(jobNameToFollow, followJobValue);
        assertTrue(hasSettingInputClass);
        assertEquals(expectedShareWorkspace, shareWorkspace);
    }

    public void assertPluginData(FreeStyleProject p, String jobNameToFollow, boolean expectedShareWorkspace) {
       NodeStalkerBuildWrapper buildWrapper = p.getBuildWrappersList().get(NodeStalkerBuildWrapper.class);
       assertEquals(jobNameToFollow, buildWrapper.getJob());
       assertEquals(expectedShareWorkspace, buildWrapper.isShareWorkspace());
    }

    public void assertNodeWhereItHasRan(HtmlPage page, String expectedNode) {
        List<HtmlDivision> nodes = page.getByXPath("//*[@id=\"main-panel\"]/div[1]/div[2]");
        assertEquals(1, nodes.size());
        HtmlDivision div = nodes.get(0);
        String[] parts = div.getTextContent().split(" ");
        String node = parts[parts.length -1];
        assertEquals(expectedNode, node);
    }


    @Test
    public void testPluginJobConfigurationSaving() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("JobA");
        NodeStalkerBuildWrapper buildWrapper = new NodeStalkerBuildWrapper("", false);
        p.getBuildWrappersList().add(buildWrapper);
        JenkinsRule.WebClient webClient = j.createWebClient();
        HtmlPage page = webClient.getPage(p, "configure");
        assertPluginUI(page, "", false);

        //submitting config as it is
        j.submit(page.getFormByName("config"));
        assertPluginData(p, "", false);

        page = webClient.getPage(p, "configure");
        updateProjectConfig(page, "JobA", true);
        j.submit(page.getFormByName("config"));

        page = webClient.getPage(p, "configure");
        assertPluginUI(page, "JobA", true);
        assertPluginData(p, "JobA", true);
    }

    @Test
    public void testFollowJobThatNeverRan() throws Exception {
        j.createSlave("Node1", "label1", null);
        FreeStyleProject vip = j.createFreeStyleProject("VIP");
        vip.setAssignedLabel(Label.get("label1"));
        FreeStyleProject stalker = j.createFreeStyleProject("STALKER");
        NodeStalkerBuildWrapper buildWrapper = new NodeStalkerBuildWrapper("", false);
        stalker.getBuildWrappersList().add(buildWrapper);

        JenkinsRule.WebClient webClient = j.createWebClient();
        HtmlPage page = webClient.getPage(stalker, "configure");
        updateProjectConfig(page, "VIP", true);
        j.submit(page.getFormByName("config"));
        page = webClient.getPage(stalker);

        //click build button
        page = webClient.getPage(stalker, "build?delay=0sec");

        page = webClient.getPage(stalker, "lastBuild");
        List<HtmlImage> elements = page.getByXPath("//*[@id=\"main-panel\"]/h1/img");
        assertTrue(elements.size() == 1);
        HtmlImage redBall = elements.get(0);
        assertEquals("Failed", redBall.getAttribute("alt"));

        page = webClient.getPage(stalker, "lastBuild/console");
        page.asText().contains("The job VIP has no traceable runs");
    }

    @Test
    public void testFollowJobThatRanOnNode1() throws Exception {
        j.createSlave("Node1", "label1", null);
        FreeStyleProject vip = j.createFreeStyleProject("VIP");
        vip.setAssignedLabel(Label.get("label1"));
        vip.scheduleBuild2(0);
        FreeStyleProject stalker = j.createFreeStyleProject("STALKER");
        NodeStalkerBuildWrapper buildWrapper = new NodeStalkerBuildWrapper("", false);
        stalker.getBuildWrappersList().add(buildWrapper);

        JenkinsRule.WebClient webClient = j.createWebClient();
        HtmlPage page = webClient.getPage(stalker, "configure");
        updateProjectConfig(page, "VIP", true);
        j.submit(page.getFormByName("config"));
        page = webClient.getPage(stalker);

        //click build button
        page = webClient.getPage(stalker, "build?delay=0sec");

        page = webClient.getPage(stalker, "lastBuild");
        List<HtmlImage> elements = page.getByXPath("//*[@id=\"main-panel\"]/h1/img");
        assertTrue(elements.size() == 1);
        HtmlImage blueBall = elements.get(0);
        assertEquals("Success", blueBall.getAttribute("alt"));
        assertNodeWhereItHasRan(page, "Node1");
        AbstractProject abstractStalker = AbstractProject.findNearest("STALKER");
        assertEquals(vip.getSomeWorkspace().getRemote(), abstractStalker.getCustomWorkspace());
    }

    @Test
    public void testFollowUnexistantJob() throws Exception {
        FreeStyleProject stalker = j.createFreeStyleProject("STALKER");
        NodeStalkerBuildWrapper buildWrapper = new NodeStalkerBuildWrapper("", false);
        stalker.getBuildWrappersList().add(buildWrapper);

        JenkinsRule.WebClient webClient = j.createWebClient();
        HtmlPage page = webClient.getPage(stalker, "configure");
        updateProjectConfig(page, "VIP", true);
        j.submit(page.getFormByName("config"));
        page = webClient.getPage(stalker);

        //click build button
        page = webClient.getPage(stalker, "build?delay=0sec");

        page = webClient.getPage(stalker, "lastBuild");
        List<HtmlImage> elements = page.getByXPath("//*[@id=\"main-panel\"]/h1/img");
        assertTrue(elements.size() == 1);
        HtmlImage redBall = elements.get(0);
        assertEquals("Failed", redBall.getAttribute("alt"));

        page = webClient.getPage(stalker, "lastBuild/console");
        page.asText().contains("The job VIP does not exist! Please check your configuration!");
    }


    @Test
    public void testThatWorkspaceIsRestored() throws Exception {
        j.createSlave("Node1", "label1", null);
        FreeStyleProject vip = j.createFreeStyleProject("VIP");
        vip.setCustomWorkspace("/tmp/vipworkspace");
        vip.setAssignedLabel(Label.get("label1"));
        vip.scheduleBuild2(0);
        FreeStyleProject stalker = j.createFreeStyleProject("STALKER");
        stalker.setCustomWorkspace("/tmp/stalker");
        NodeStalkerBuildWrapper buildWrapper = new NodeStalkerBuildWrapper("", false);
        stalker.getBuildWrappersList().add(buildWrapper);

        JenkinsRule.WebClient webClient = j.createWebClient();
        HtmlPage page = webClient.getPage(stalker, "configure");
        updateProjectConfig(page, "VIP", true);
        j.submit(page.getFormByName("config"));
        page = webClient.getPage(stalker);

        //click build button
        page = webClient.getPage(stalker, "build?delay=0sec");

        page = webClient.getPage(stalker, "lastBuild");
        List<HtmlImage> elements = page.getByXPath("//*[@id=\"main-panel\"]/h1/img");
        assertTrue(elements.size() == 1);
        HtmlImage blueBall = elements.get(0);
        assertEquals("Success", blueBall.getAttribute("alt"));
        assertNodeWhereItHasRan(page, "Node1");
        AbstractProject abstractStalker = AbstractProject.findNearest("STALKER");

        assertEquals("/tmp/stalker", abstractStalker.getCustomWorkspace());
    }

}
