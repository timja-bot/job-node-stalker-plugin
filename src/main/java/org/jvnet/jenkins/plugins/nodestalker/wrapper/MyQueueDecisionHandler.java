package org.jvnet.jenkins.plugins.nodestalker.wrapper;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Queue;

import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: fabioneves
* Date: 23/04/13
* Time: 12:35
* To change this template use File | Settings | File Templates.
*/
@Extension
public final class MyQueueDecisionHandler extends Queue.QueueDecisionHandler {

    public boolean shouldSchedule(Queue.Task p, List<Action> actions) {
        actions.add(new MyNodeAssignmentAction());
        return true;
    }

}