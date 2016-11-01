package com.amplexor.ia.worker;

import com.amplexor.ia.worker.WorkerManager;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by minkenbergs on 6-10-2016.
 */
public class WorkerManagerTest {
    @Test
    public void getWorkerManager() throws Exception {
        assertNotNull(WorkerManager.getWorkerManager());
    }
}