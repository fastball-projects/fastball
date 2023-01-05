package dev.fastball.runtime.spring.devtools;

import org.apache.commons.exec.LogOutputStream;
import org.slf4j.Logger;

/**
 * @author gr@fastball.dev
 * @since 2023/1/5
 */
public class Slf4jLogOutputStream extends LogOutputStream {

    private final Logger log;

    private final LogLevel level;

    public Slf4jLogOutputStream(Logger log, LogLevel level) {
        this.log = log;
        this.level = level;
    }

    @Override
    protected void processLine(String line, int i) {
        switch (level) {
            case DEBUG:
                log.debug(line);
                break;
            case INFO:
                log.info(line);
                break;
            case WARN:
                log.warn(line);
                break;
            case ERROR:
                log.error(line);
                break;
        }
    }

    enum LogLevel {
        DEBUG, INFO, WARN, ERROR;
    }
}
