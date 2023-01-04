package dev.fastball.generate.utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author gr@fastball.dev
 * @since 2023/1/5
 */
public class NodeJsUtils {

    public static void exec(String command, File execDir) throws IOException {
        ByteArrayOutputStream susStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        CommandLine commandLine = CommandLine.parse(command);
        DefaultExecutor exec = new DefaultExecutor();
        PumpStreamHandler streamHandler = new PumpStreamHandler(susStream, errStream);
        exec.setStreamHandler(streamHandler);
        exec.setWorkingDirectory(execDir);
        int code = exec.execute(commandLine);
        String suc = susStream.toString("GBK");
        String err = errStream.toString("GBK");
        System.out.println(suc);
        System.err.println(err);
    }
}
