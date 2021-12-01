package at.ac.ait.lablink.core.client;

import at.ac.ait.lablink.core.client.impl.LlClient;

abstract public class LlClientShutdownHook extends Thread {

    private LlClient client;

    public LlClientShutdownHook(LlClient client) {
        this.client = client;
    }          
}
