package com.test;

import com.analyze.Task;
import com.gant.Config;
import com.gant.planner.Tic;
import com.gant.planner.Transfer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TicTest {

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
        assertTrue(d.tic.empty.isFree());
        assertFalse(d.tic.withTask.isFree());
        Config.isIO = false;
        Config.duplex = false;
        assertTrue(d.tic.empty.isFree());
        assertFalse(d.tic.withTask.isFree());
        assertFalse(d.tic.withTaskAndSend12.isFree());
        Config.isIO = true;
        Config.duplex = true;
        assertFalse(d.tic.withTask.isFree());
        assertFalse(d.tic.withTaskAndSend12.isFree());
    }

    @Test
    public void testIsFreeForTheWork() throws Exception {
        Data d = new Data();
        Config.isIO = true;
        Config.duplex = false;
        assertTrue(d.tic.empty.isFree(d.task.w1));
        assertTrue(d.tic.empty.isFree(d.transfer.receive21));
        assertFalse(d.tic.withTask.isFree(d.task.w1));
        assertTrue(d.tic.withTask.isFree(d.transfer.receive21));
        assertTrue(d.tic.withSend12.isFree(d.task.w1));
        assertFalse(d.tic.withSend12.isFree(d.transfer.receive21));
        assertFalse(d.tic.withTaskAndSend12.isFree(d.task.w1));
        assertFalse(d.tic.withTaskAndSend12.isFree(d.transfer.receive21));
        Config.isIO = false;
        Config.duplex = false;
        assertTrue(d.tic.empty.isFree(d.task.w1));
        assertTrue(d.tic.empty.isFree(d.transfer.receive21));
        assertFalse(d.tic.withTask.isFree(d.task.w1));
        assertFalse(d.tic.withTask.isFree(d.transfer.receive21));
        assertFalse(d.tic.withSend12.isFree(d.task.w1));
        assertFalse(d.tic.withSend12.isFree(d.transfer.receive21));
        assertFalse(d.tic.withTaskAndSend12.isFree(d.task.w1));
        assertFalse(d.tic.withTaskAndSend12.isFree(d.transfer.receive21));
        Config.isIO = false;
        Config.duplex = true;
        assertTrue(d.tic.empty.isFree(d.task.w1));
        assertTrue(d.tic.empty.isFree(d.transfer.receive21));
        assertFalse(d.tic.withTask.isFree(d.task.w1));
        assertFalse(d.tic.withTask.isFree(d.transfer.receive21));
        assertFalse(d.tic.withSend12.isFree(d.task.w1));
        assertTrue(d.tic.withSend12.isFree(d.transfer.receive21));
        assertFalse(d.tic.withTaskAndSend12.isFree(d.task.w1));
        assertFalse(d.tic.withTaskAndSend12.isFree(d.transfer.receive21));
    }

    @Test
    public void testSetWork() throws Exception {
        Data d = new Data();
        Tic tic = new Tic(d.workflow.for1node);
        Config.isIO = true;
        Config.duplex = true;
        assertTrue(tic.setWork(d.task.w1));
        assertFalse(tic.setWork(d.task.w1));
        assertTrue(tic.setWork(d.transfer.send12));
        assertFalse(tic.setWork(d.transfer.send12));
        assertTrue(tic.setWork(d.transfer.receive21));

        Config.isIO = false;
        Config.duplex = false;
        tic = new Tic(d.workflow.for1node);
        assertTrue(tic.setWork(d.task.w1));
        assertFalse(tic.setWork(d.task.w1));
        tic = new Tic(d.workflow.for1node);
        assertTrue(tic.setWork(d.transfer.send12));
        assertFalse(tic.setWork(d.task.w1));
        assertFalse(tic.setWork(d.transfer.receive21));

        Config.isIO = false;
        Config.duplex = true;
        tic = new Tic(d.workflow.for1node);
        assertTrue(tic.setWork(d.task.w1));
        assertFalse(tic.setWork(d.transfer.send12));
        tic = new Tic(d.workflow.for1node);
        assertTrue(tic.setWork(d.transfer.send12));
        assertFalse(tic.setWork(d.task.w1));
        assertFalse(tic.setWork(d.transfer.receive12));
        assertTrue(tic.setWork(d.transfer.receive21));
    }
}