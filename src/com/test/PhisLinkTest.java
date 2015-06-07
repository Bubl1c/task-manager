package com.test;

import com.gant.Config;
import com.gant.planner.PhisLink;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PhisLinkTest {

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
        assertTrue(d.pLink.phisLink.isFree());
        assertFalse(d.pLink.phisLinkSend12Receive21.isFree());
        assertFalse(d.pLink.phisLinkSend12Receive12.isFree());
        Config.duplex = true;
        assertTrue(d.pLink.phisLinkSend21.isFree());
        Config.duplex = false;
        assertFalse(d.pLink.phisLinkSend21.isFree());
    }

    @Test
    public void testIsFreeForTranfer() throws Exception {
        Data d = new Data();
        assertTrue(d.pLink.phisLink.isFree(d.transfer.send12));
        assertFalse(d.pLink.phisLinkSend12Receive21.isFree(d.transfer.send12));
        Config.duplex = true;
        assertTrue(d.pLink.phisLinkSend21.isFree(d.transfer.send12));
        assertFalse(d.pLink.phisLinkSend21.isFree(d.transfer.send21));
        Config.duplex = false;
        assertFalse(d.pLink.phisLinkSend21.isFree(d.transfer.send12));
    }

    @Test
    public void testSetTransfer() throws Exception {
        Data d = new Data();
        PhisLink phisLink = new PhisLink();
        assertTrue(phisLink.setTransfer(d.transfer.send12));
        Config.duplex = true;
        assertFalse(phisLink.setTransfer(d.transfer.send12));
        assertTrue(phisLink.setTransfer(d.transfer.send21));
        Config.duplex = false;
        phisLink = new PhisLink();
        assertTrue(phisLink.setTransfer(d.transfer.send12));
        assertFalse(phisLink.setTransfer(d.transfer.send21));
    }
}