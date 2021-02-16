package org.andot.share.linedot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Linedot启动器
 *
 * LineDot 同道
 * 取自 some line 是 同一行
 *  dot 音译 道
 * @author andot
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class LinedotApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinedotApplication.class, args);
    }

}
