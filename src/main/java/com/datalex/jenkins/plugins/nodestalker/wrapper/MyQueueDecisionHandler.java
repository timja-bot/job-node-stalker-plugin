package com.datalex.jenkins.plugins.nodestalker.wrapper;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Queue;

import java.util.List;

/**
 *
 *
 *
 * @author Fabio Neves <fabio.neves@datalex.com>, Baris Batiege <baris.batiege@datalex.com>
 * @version 1.0
 */
@Extension
public final class MyQueueDecisionHandler extends Queue.QueueDecisionHandler {

    public boolean shouldSchedule(Queue.Task p, List<Action> actions) {
        actions.add(new MyNodeAssignmentAction());
        return true;
    }

}