// TinyAXML CLI(plugin runner)
package org.duangsuse.tinyaxml; // tinyaxml <3 duangsuse

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.nio.file.FileAlreadyExistsException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.duangsuse.tinyaxml.error.*;

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
    /** All CMD arguments */
    public static String[] args;
    /** Default plugin path variable */
    public static final String pluginPathEnv = "TINYAXML_PLUG";
    /** Default plugin path, contains plugin .class files */
    public static final String defaultPluginPath = envOr("HOME", "/root/") + "/.config/tinyaxml/";
    /** Default help message */
    public static final String helpMessage = "TinyAXML - A small utility for processing Android binary AXML files\n" +
                                            "Usage: tinyaxml [pluginID/pluginPath/help] [input] [output(optional)]";
    /** Plugin process method id */
    public static final String pluginProcessMethodId = "process";
    /** Plugin main method id */
    public static final String pluginMainMethodId = "main";
    /** Missing plugin argument plugin id */
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
    /** Loaded plugin class */
    public static Class<?> pluginClass;
    /** Be more verbose? */
    public static boolean verbose = Boolean.valueOf(envOr("VERBOSE", String.valueOf(false)));
    /** Continue even magic is wrong? */
    public static boolean tryCompat = false;
    /** Should write file? */
    public static boolean shouldWrite;

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
     * array reverse index helper
     * 
     * Example:
     * {@code revAry([0, 1, 2, 3], 0) -> 3}
     * 
     * @param ary input array
     * @return reverse indexed object
     * @since 1.0
     */
    public static Object revAry(Object[] ary, int index) {
        return ary[ary.length - index];
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
     * given path exists & is a file?
     * 
     * @param path file path
     * @return is a valid file
     * @since 1.0
     */
    public static boolean isFile(String path) {
        File tmp = new File(path);
        if (tmp.exists()) // must exist
            return tmp.isFile(); // is a file
        return false; // not file
    }

    /**
     * check file, readable&writeable?
     * 
     * @param f given file object
     * @return can read&write?
     * @since 1.0
     */
    public static boolean checkFile(File f) {
        return f.canRead() && f.canWrite();
    }

    /**
     * crop an array object
     * <p> result including start/end index
     *
     * Example:
     *  {@code cropAry([0,1,2,3,4], 1, 2) -> [1,2]}
     * 
     * @param ary input array object
     * @param start start index
     * @param end end index
     * @return new croped array object
     * 
     * @throws IllegalArgumentException if start/end size mismatch
     * @since 1.0
     */
    public static Object[] cropAry(Object[] ary, int start, int end) {
        int ary_size = ary.length;
        if (start == end)
            throw new IllegalArgumentException("Start and end should not be equal");
        if (start < 0 || start > end || start >= ary_size || end >= ary_size) // may not be negative, start should be smaller than end
            throw new IllegalArgumentException(String.format("Ary size mismatch/negative %1$s/%2$s", start, end));
        int length = (end - start) + 1;
        Object[] tmp = new Object[length]; // range including start index/end index
        System.arraycopy(ary, start, tmp, 0, length); // copy!
        return tmp;
    }
    /** byte[] version of cropAry @see Main#cropAry(Object[], int, int) */
    public static byte[] cropAry(byte[] ary, int start, int end) {
        int ary_size = ary.length;
        if (start == end)
            throw new IllegalArgumentException("Start and end should not be equal(calling byte[] cropAry)");
        if (start < 0 || start > end || start >= ary_size || end >= ary_size) // may not be negative, start should be smaller than end
            throw new IllegalArgumentException(String.format("Ary size mismatch/negative %1$s/%2$s(calling byte[] cropAry)", start, end));
        int length = (end - start) + 1;
        byte[] tmp = new byte[length]; // range including start index/end index
        System.arraycopy(ary, start, tmp, 0, length); // copy!
        return tmp;
    }

    /**
     * Main method, usage refer to class document
     * 
     * @param args command line arguments
     * @since 1.0
     */
    public static void main(String[] args) {
        Main.args = args;
        // Register unhandled exception handler
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            // That's it!
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                puts(":-( Unhandled Exception running TinyAXML... at thread " + t.getName());
                if (e.getClass().equals(FileNotFoundException.class) || e.getClass().equals(FileAlreadyExistsException.class) || e.getClass().equals(IOException.class))
                    puts("(Maybe File I/O Error, try delete existing file/create non-existing file/run as root/admin may help)");
                puts("Caused by: " + e.getCause() + " Message: " + e.getMessage());
                e.printStackTrace(stderr);
                puts("Maybe you can send this stack trace to https://github.com/duangsuse/TinyAXML/issues (");
            }
        });

        putsv("CMDLine Arguments: " + ppAry(args));

        if (args.length == 0)
            invokePlugin(missingPluginId, null); // call plugin_missing if no arg is given
        else if (args.length == 1) {
            String arg1 = args[0];
            if (arg1.equals("help") || arg1.equals("--help") || arg1.equals("-help") || arg1.equals("-h"))
                puts(helpMessage);
            else // Run plugin named arg1
                invokePlugin(arg1, null);
        } else if (args.length == 2) { // maybe: plugin_name arg1 or plugin_name I/O file
            String maybe_file_path = args[1];
            if (isFile(maybe_file_path)) {
                File iofile = new File(maybe_file_path);
                if (!checkFile(iofile))
                    warn("Bad file permission, may cause failure(infered output)");
                in = iofile;
                out = iofile;
                invokePlugin(args[0], null);
            } else {
                invokePlugin(args[0], new String[] {args[1]}); // not I/O file, it's an argument
            }
        } else { // args.size now >= 3 (may containing plugin_id, I/O file, plugin arguments)
            String maybe_input_path = (String)revAry(args, 1); // Bad type conversation(
            String maybe_output_path = (String)revAry(args, 0); // duangsuse bad
            if (isFile(maybe_input_path)) { // now I know they are *real* I/O path
                in = new File(maybe_input_path);
                out = new File(maybe_output_path);
                if (!checkFile(in) || !checkFile(out))
                    warn("Bad file permisson or output file, bad("); // XD
                boolean b = false;
                try { b = out.createNewFile(); }
                catch (IOException ignored) { warn("IO error creating output file"); }
                if (!out.mkdirs() || !b) {
                    warn("Failed to create output file!, falling back to use temp file (Interrupt ^C now if you want to exit)");
                    try {
                        Thread.sleep(3000);
                        try {
                            out = File.createTempFile("tinyaxml", "xml");
                            warn(String.format("Writting to %1$s...", out.getAbsolutePath()));
                        } catch (IOException e) {
                            warn("Failed to create temp file!!!");
                            e.printStackTrace(stderr);
                            System.exit(2);
                        }
                    } catch (InterruptedException ignored) {
                        warn("Exited.");
                        System.exit(2); // file...
                    }
                }
                invokePlugin(args[0], (String[])cropAry(args, 1, args.length - 2)); // arrCrop...
            } else { // now I must check if maybe_output_file is input file
                if (isFile(maybe_output_path)) { // I/O auto-infered
                    File iofile = new File(maybe_output_path);
                    in = iofile; // <3
                    out = iofile;
                    if (!checkFile(iofile))
                        warn("Bad file permission, may cause failure(I/O same file infered)");
                    invokePlugin(args[0], (String[])cropAry(args, 1, args.length - 1));
                } else {
                    invokePlugin(args[0], (String[])cropAry(args, 1, args.length)); // now pass all arguments to plugin
                }
            }
        }
    }

    /**
     * Is item in array?
     * 
     * @param ary input array
     * @param item target item
     * @return position of item in array or -1 if item not present
     * @since 1.0
     */
    public static int isIn(Object item, Object[] ary) {
        int i = 0;
        for (Object o:ary) {
            if (o.equals(item))
                break;
            i++;
        }
        if (i >= ary.length)
            return -1;
        return i;
    }

    /**
     * Finds and invokes a plugin using given args, and sync with I/O file if needed
     * 
     * @param id plugin ID or path, not nullable
     * @param args args passed to the plugin, null for blank argument
     */
    public static void invokePlugin(String id, String[] args) {
        putsv("Invoking plugin " + id + "...");
        if (args == null) // Desugar
            args = new String[0];
        try {
            findAndLoadPlugin(id);
        } catch (ExtensionBootstrapError e) {
            warn(e.errnum.getDescription());
            System.exit(3); // Extension error
        }

        shouldWrite = false;
        Method process_method = null;
        try {
            process_method = pluginClass.getDeclaredMethod(pluginProcessMethodId, new Class<?>[] {AxmlFile.class});
        } catch (NoSuchMethodException ignored) {
            try {
                process_method = pluginClass.getDeclaredMethod(pluginMainMethodId, new Class<?>[] {String[].class});
                try {
                    process_method.invoke(pluginClass.getDeclaredConstructor(new Class<?>[] {}).newInstance(), new Object[] {cropAry(Main.args, 1, args.length)});
                } catch (IllegalAccessException e) {
                    warn("Access should be public in plugin(invoking main)");
                    System.exit(5);
                } catch (InvocationTargetException e) {
                    warn("ITE caught, maybe it's thrown by plugin class(invoking main)");
                    e.getTargetException().printStackTrace(stderr);
                } catch (InstantiationException e) {
                    warn("Failed to construct class!!!(main)");
                    System.exit(5);
                }
            } catch (NoSuchMethodException e) {
                warn("Bad plugin. neither process nor main method found");
                System.exit(4);
            }
            warn("Failed preparing plugin: cannot get process method");
            System.exit(4); // Bad(
        }
        if (process_method.getReturnType().equals(AxmlFile.class))
            shouldWrite = true; // or I won't write generated file to out
        // invoke now!
        AxmlFile axml = null;
        putsv("Invoking process at " + System.currentTimeMillis());
        try {
            axml = (AxmlFile)process_method.invoke(pluginClass.getDeclaredConstructor(new Class<?>[] {}).newInstance(), new Object[] {args});
        } catch (IllegalAccessException e) {
            warn("Access should be public in plugin");
            System.exit(4);
        } catch (InvocationTargetException e) {
            warn("ITE caught, maybe it's thrown by plugin class");
            e.getTargetException().printStackTrace(stderr);
        } catch (InstantiationException e) {
            warn("Failed to construct class!!!");
            System.exit(4);
        } catch (NoSuchMethodException e) {
            warn("Could not get constructor method");
            System.exit(4);
        }
        putsv("Back to control at " + System.currentTimeMillis());
        // now write
        if (shouldWrite) {
            try {
                FileOutputStream fout = new FileOutputStream(out);
                fout.write(axml.getBytes());
                fout.flush(); // flush buffer
                fout.close();
            } catch (FileNotFoundException ignored) {
                warn("output file moved(");
                System.exit(6);
            } catch (IOException ignored) {
                warn("failed to output");
                System.exit(6);
            }
        }
    }

    /**
     * Finds and loads the plugin class
     * 
     * @param id plugin ID or path
     * @throws ExtensionBootstrapError error loading plugin class
     * @see Main#pluginClass
     * @see Main#defaultPluginPath
     * @see Main#pluginPathEnv
     */
    public static void findAndLoadPlugin(String id) throws ExtensionBootstrapError {
        File class_file;
        if (id.endsWith(".class")) { // should be a class file
            class_file = new File(id);
        } else { // find in plugin classpath
            class_file = new File(pluginClassPath + id + ".class");
        }
        if (!class_file.exists()) { // no recursive loops
            if (!id.equals(missingPluginId))
                invokePlugin(missingPluginId, new String[] {id}); // yes to plugin_missing plugin
            else {
                warn("plugin_missing plugin not installed");
                throw new ExtensionBootstrapError(Errno.EPLUGINNODEF);
            }
        }

        // now normal
        try {
            // convert to URL
            URL url = class_file.toURI().toURL();
            putsv("loading url class: " + url.toString());
            URL[] urls = new URL[] {url};
            // now use URLClassLoader to load this class
            URLClassLoader cl = new URLClassLoader(urls);

            pluginClass = cl.loadClass(id); // must be same with file name
            try {
                cl.close(); // close class loader
            } catch (IOException ignored) {
                throw new ExtensionBootstrapError(Errno.EIOEXCEPT);
            }
        } catch (MalformedURLException ignored) {}
          catch (ClassNotFoundException ignored) {} // eat(
    }
}
