package com.test;

import com.gant.Config;
import com.gant.model.Node;
import com.gant.planner.NodeWorkflow;
import com.test.Data;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class NodeWorkflowTest {

    @Before
    public void setUp() throws Exception {
        Config.createCopy();
    }

    @After
    public void tearDown() throws Exception {
        Config.restoreFromCopy();
    }

    @Test
    public void testIsFree() throws Exception {
        Data d = new Data();

        Config.isIO = true;
        Config.duplex = true;

        assertTrue(d.workflow.for1node.isFree(d.task.w1, 0));
        assertTrue(d.workflow.for1node.isFree(d.task.w1, 7));
        assertTrue(d.workflow.for1node.isFree(d.task.w1, 7));
        assertTrue(d.workflow.for1node.isFree(d.transfer.send12, 7));

        assertFalse(d.workflow.taskAtStart.isFree(d.task.w1, 0));
        assertTrue(d.workflow.taskAtStart.isFree(d.task.w1, 7));
        assertTrue(d.workflow.taskAtStart.isFree(d.transfer.send12, 0));

        assertTrue(d.workflow.send12At7thTic.isFree(d.task.w1, 7));
        assertFalse(d.workflow.send12At7thTic.isFree(d.transfer.send12, 7));
        assertTrue(d.workflow.send12At7thTic.isFree(d.transfer.send21, 7));

        assertFalse(d.workflow.taskAt7thSend12At9thTic.isFree(d.task.w2, 6));
        assertTrue(d.workflow.taskAt7thSend12At9thTic.isFree(d.task.w2, 5));
        assertTrue(d.workflow.taskAt7thSend12At9thTic.isFree(d.transfer.send21w3, 6));
        assertFalse(d.workflow.taskAt7thSend12At9thTic.isFree(d.transfer.send12w3, 8));
        assertTrue(d.workflow.taskAt7thSend12At9thTic.isFree(d.transfer.send21w3, 8));
    }

    @Test
    public void testAssignWork() throws Exception {
        Data d = new Data();

        Config.isIO = true;
        Config.duplex = true;

        assertTrue(d.workflow.for1node.assignWork(d.task.w1, 0));
        assertFalse(d.workflow.taskAtStart.assignWork(d.task.w1, 0));
        assertTrue(d.workflow.send12At7thTic.assignWork(d.task.w1, 7));
        assertTrue(d.workflow.send12At7thTic.assignWork(d.transfer.send21, 7));
        assertFalse(d.workflow.taskAt7thSend12At9thTic.assignWork(d.task.w2, 6));
        assertTrue(d.workflow.taskAt7thSend12At9thTic.assignWork(d.task.w2, 8));
        assertTrue(d.workflow.taskAt7thSend12At9thTic.assignWork(d.transfer.send12w3, 6));
    }
}