package com.test;

import com.gant.planner.PhisLink;
import com.gant.planner.Transfer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class PhisLinkTest {
    private PhisLink phisLink;

    static Transfer sendTransfer1 = new Transfer(1,2,9,10,1, Transfer.Type.SEND);
    static Transfer sendTransfer2 = new Transfer(2,1,9,10,1, Transfer.Type.SEND);
    static Transfer sendTransfer3 = new Transfer(1,3,9,10,1, Transfer.Type.SEND);
    static Transfer reseiveTransfer1 = new Transfer(sendTransfer1, true);
    static Transfer reseiveTransfer2 = new Transfer(sendTransfer2, true);
    static Transfer reseiveTransfer3 = new Transfer(sendTransfer3, true);

    public PhisLinkTest(PhisLink phisLink) {
        this.phisLink = phisLink;
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] {
                { new PhisLink()},
                { new PhisLink(sendTransfer1, reseiveTransfer2)},
                { new PhisLink(sendTransfer1, reseiveTransfer1) },
                { new PhisLink(sendTransfer1, reseiveTransfer3) },
                { new PhisLink(sendTransfer1) }
        };
        return Arrays.asList(data);
    }

    @Test
    public void testIsFree() throws Exception {
        assertTrue(phisLink.isFree());
    }

    @Test
    public void testIsFreeForTranfer() throws Exception {
        assertTrue("send 1: ", phisLink.isFree(sendTransfer1));
        assertTrue("receive 1: ", phisLink.isFree(reseiveTransfer1));
    }

    @Test
    public void testSetTransfer() throws Exception {
        assertTrue("set send 1: ", phisLink.setTransfer(sendTransfer1));
    }
}