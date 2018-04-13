// TinyAXML CLI(plugin runner)
package org.duangsuse.tinyaxml; // tinyaxml <3 duangsuse

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * TinyAXML CLI plugin runner
 * <p>
 * Also has some useful helper methods used by {@code org.duangsuse.tinyaxml} package (like {@code Word2Int})
 * <p>
 * Default plugin dir: $HOME/.config/tinyaxml, auto-created by this tool(ignore error)
 * <p>
 * Usage:
 * <p>{-h|--help|help}: display help
 * <p>{} run argument_missing plugin, no I/O file detected
 * <p>{x} run plugin named x, no I/O file detected
 * <p>{x, y} check if y is a file, yes, I/O file = y; run plugin x, no, run plugin x {y}, no default I/O file
 * <p>{x, y, z} check if y is a file, yes, IN=y; OUT=z, no, if z is a file, IN/OUT=z, invoke plugin x(y), else x(y, z)
 * <p>more: (infer)
 * 
 * @since 1.0
 * @author duangsuse
 */
public class Main {
    /** default plugin path variable */
    public static final String pluginPathEnv = "TINYAXML_PLUG";
    /** default plugin path, contains plugin .class files */
    public static final String defaultPluginPath = envOr("HOME", "/root/") + "/.config/tinyaxml/";
    /** default help message */
    public static final String helpMessage = "TinyAXML - A small utility for processing Android binary AXML files\n" +
                                            "Usage: tinyaxml [pluginID/pluginPath/help] [input] [output(optional)]";
    /** plugin process method id */
    public static final String pluginProcessMethodId = "process";
    /** plugin main method id */
    public static final String pluginMainMethodId = "main";
    /** missing plugin argument plugin id */
    public static final String missingPluginId = "plugin_missing";

    // stdin/out/err used by this utility
    public static PrintStream stdout = System.out;
    public static PrintStream stderr = System.err;
    public static InputStream stdin = System.in;

    /** Axml input file */
    public static File in;
    /** Axml output file */
    public static File out;
    /** Input byte buffer */
    public static byte[] inBuf;
    /** Plugin path */
    public static String pluginClassPath = envOr(pluginPathEnv, defaultPluginPath);
    /** Be more verbose? */
    public static boolean verbose = Boolean.valueOf(envOr("VERBOSE", String.valueOf(false)));

    /**
     * gets environment var 'name' or default string
     * 
     * @param name environment variable name, not nullable
     * @param defaultVal default value, not nullable
     * @return environment or default value
     * @since 1.0
     */
    public static String envOr(String name, String defaultVal) {
        String tmp = System.getenv(name);
        putsv("Variable " + name + " getted: " + tmp); // VERBOSE info
        return tmp == null ? defaultVal : tmp;
    }

    /** display stdout message @see PrintStream#println() */
    public static void puts(String msg) {
        stdout.println(msg);
    }

    /** display stderr message @see PrintStream#println() */
    public static void warn(String msg) {
        stderr.println(msg);
    }

    /** puts verbose information @see Main#puts(String) */
    public static void putsv(String msg) {
        if(verbose)
            puts(msg);
    }

    /**
     * get formated array
     * 
     * @param ary array given
     * @return new each {@code element.toString()} ed array
     * @since 1.0
     */
    public static String[] ppAry(Object[] ary) {
        String[] tmp = new String[ary.length];
        if (ary.length == 0)
            return tmp; // just return
        else {
            int i = 0;
            for (Object j:ary) // forEach <3 duangsuse
                tmp[i++] = j.toString();
            return tmp;
        }
    }

    /**
     * Main method, usage refer to class document
     * 
     * @param args command line arguments
     * @since 1.0
     */
    public static void main(String[] args) {
        // Register unhandled exception handler
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            // That's it!
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                puts(":-( Unhandled Exception running TinyAXML... at thread " + t.getName());
                puts("Caused by: " + e.getCause() + " Message: " + e.getMessage());
                e.printStackTrace(stderr);
                puts("Maybe you can send this stack trace to https://github.com/duangsuse/TinyAXML/issues (");
            }
        });

        putsv("CMDLine Arguments: " + ppAry(args));

        if (args.length == 0)
            invokePlugin(missingPluginId, null); // call plugin_missing if no arg is given
        
    }

    /**
     * Finds and invokes a plugin using given args, and sync with I/O file if needed
     * 
     * @param id plugin ID, not nullable
     * @param args args passed to the plugin, null for blank argument
     */
    public static void invokePlugin(String id, String[] args) {}
}
