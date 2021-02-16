package org.andot.share.linedot.config;

import org.andot.share.linedot.core.LineDotServer;
import org.andot.share.linedot.core.LineDotWebSocketServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author andot
 */
@Component
public class StartupApplication implements ApplicationRunner {

    @Resource
    private LineDotServer lineDotServer;
    @Resource
    private LineDotWebSocketServer lineDotWebSocketServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // lineDotServer.startup();
        lineDotWebSocketServer.startup();
    }
}
