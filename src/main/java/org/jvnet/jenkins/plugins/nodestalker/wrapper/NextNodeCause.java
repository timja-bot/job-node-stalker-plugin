package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.model.Cause;
import hudson.model.Run;

/**
 * Created with IntelliJ IDEA.
 * User: fabioneves
 * Date: 22/04/13
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public class NextNodeCause extends Cause.UpstreamCause {

    private String label;

    public NextNodeCause(String label, Run<?, ?> up) {
        super(up);
        this.label = label;
    }

    @Override
    public String getShortDescription() {
        return"A build with label/node [{0}] was requested";
    }
}

