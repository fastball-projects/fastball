package dev.fastball.maven;

import org.apache.commons.exec.LogOutputStream;
import org.apache.maven.plugin.logging.Log;

/**
 * @author gr@fastball.dev
 * @since 2023/1/5
 */
public class MavenLogOutputStream extends LogOutputStream {

    private final Log log;

    private final LogLevel level;

    public MavenLogOutputStream(Log log, LogLevel level) {
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
