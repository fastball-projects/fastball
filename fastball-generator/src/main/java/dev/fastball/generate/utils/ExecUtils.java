package dev.fastball.generate.utils;

import dev.fastball.generate.exception.GenerateException;
import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author gr@fastball.dev
 * @since 2023/1/5
 */
public class ExecUtils {
    private ExecUtils() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(ExecUtils.class);

    /**
     * 执行本地命令
     *
     * @param command 需执行的本地命令
     * @throws IOException 执行异常
     */
    public static void exec(String command) throws IOException {
        exec(command, null);
    }

    /**
     * 执行本地命令
     *
     * @param command 需执行的本地命令
     * @param execDir 执行命令的工作目录
     * @throws IOException 执行异常
     */
    public static void exec(String command, File execDir) throws IOException {
        execute(command, execDir, null, null);
    }

    /**
     * 执行本地命令
     *
     * @param command 需执行的本地命令
     * @param execDir 执行命令的工作目录
     * @param out     标准输出流
     * @param err     错误输出流
     * @throws IOException 执行异常
     */
    public static void exec(String command, File execDir, OutputStream out, OutputStream err) throws IOException {
        execute(command, execDir, new PumpStreamHandler(out, err), null);
    }

    /**
     * 异步的执行本地命令
     *
     * @param command 需执行的本地命令
     * @param execDir 执行命令的工作目录
     * @param out     标准输出流
     * @param err     错误输出流
     * @throws IOException 执行异常
     */
    public static void execAsync(String command, File execDir, OutputStream out, OutputStream err) throws IOException {
        execAsync(command, execDir, new PumpStreamHandler(out, err), new DefaultExecuteResultHandler());
    }

    /**
     * 异步的执行本地命令
     *
     * @param command              需执行的本地命令
     * @param execDir              执行命令的工作目录
     * @param streamHandler        输出流处理器
     * @param executeResultHandler 执行结果处理器
     * @throws IOException 执行异常
     */
    public static void execAsync(String command, File execDir, PumpStreamHandler streamHandler, ExecuteResultHandler executeResultHandler) throws IOException {
        execute(command, execDir, streamHandler, executeResultHandler);
    }

    private static void execute(String command, File execDir, PumpStreamHandler streamHandler, ExecuteResultHandler executeResultHandler) throws IOException {
        CommandLine commandLine = CommandLine.parse(command);
        DefaultExecutor exec = new DefaultExecutor();
        if (execDir != null) {
            exec.setWorkingDirectory(execDir);
        }
        if (streamHandler != null) {
            exec.setStreamHandler(streamHandler);
        }
        if (executeResultHandler != null) {
            exec.execute(commandLine, executeResultHandler);
        } else {
            exec.execute(commandLine);
        }
    }


    public static void checkNodeAndPNPM() {
        try {
            execute("npm -v", null, null, null);
        } catch (IOException e) {
            throw new GenerateException("'npm' not found, please confirm nodejs has been installed, see https://nodejs.org/ ");
        }
        try {
            execute("pnpm -v", null, null, null);
        } catch (IOException ignore) {
            try {
                LOG.info("Try install pnpm...");
                execute("npm i -g pnpm", null, null, null);
            } catch (IOException e) {
                throw new GenerateException("install 'pnpm' error, see https://pnpm.io/installation ", e);
            }
        }
    }
}
