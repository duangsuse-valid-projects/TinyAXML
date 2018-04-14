// TinyAXML CLI(plugin runner)
package org.duangsuse.tinyaxml; // tinyaxml <3 duangsuse

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java.util.Scanner;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.FileAlreadyExistsException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import sun.misc.Signal;
import sun.misc.SignalHandler;

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
    /** The pluginClass classloader */
    public static ClassLoader cl;
    /** Loaded plugin class */
    public static Class<?> pluginClass;
    /** Be more verbose? */
    public static boolean verbose = Boolean.valueOf(envOr("VERBOSE", String.valueOf(false)));
    /** Continue even magic is wrong? */
    public static boolean tryCompat = false;
    /** Should write file? */
    public static boolean shouldWrite;
    /** Binary word little-endian or big-endian */
    public static boolean isLittleEndian = true;
    /** Output file infered?? */
    public static boolean isOutputFileInfered = true;
    /** Keep going even if out is not writeable? */
    public static boolean isKeep = false;

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
     * @throws IndexOutOfBoundsException if iob indexing array
     * @since 1.0
     */
    public static Object revAry(Object[] ary, int index) throws IndexOutOfBoundsException {
        return ary[ary.length - index - 1];
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
     * Print string array
     * 
     * @param ary string array
     * @since 1.0
     */
    public static void pAry(String[] ary) {
        stdout.print(" [Array]:");
        for (String s:ary)
            stdout.print(" " + s);
        puts("");
    }

    /**
     * Main method, usage refer to class document
     * 
     * No automatic tests required
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

        putsv("CMDLine Arguments: ");
        if (verbose)
            pAry(ppAry(args));

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
                isOutputFileInfered = false; // plugin API implementation
                boolean b = true;
                try { b = out.createNewFile(); }
                catch (IOException ignored) { warn("IO error creating output file"); }
                if (!b)
                    puts("Output file exists, that's OK");
                if (!b) if (!out.canWrite()) { // Validate only if file is not created by CLI
                    warn("Failed to validate output file!, falling back to use temp file (Interrupt ^C now if you want to exit or keep going)");
                    Signal.handle(new Signal("INT"), new SignalHandler() {
                        @Override
                        public void handle(Signal ignored) {
                            String decision = gets("Keep goning with orginal file? [Y/n]");
                            Main.isKeep = (decision.length() == 0 || decision.equals("y"));
                        }
                    });
                    try {
                        Thread.sleep(3000);
                        if(!isKeep) try {
                            out = File.createTempFile("tinyaxml_", ".xml");
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
                if (args.length != 3)
                    invokePlugin(args[0], ppAry(cropAry(args, 1, args.length - 3))); // arrCrop...
                else
                    invokePlugin(args[0], new String[0]);
            } else { // now I must check if maybe_output_file is input file
                if (isFile(maybe_output_path)) { // I/O auto-infered
                    File iofile = new File(maybe_output_path);
                    in = iofile; // <3
                    out = iofile;
                    if (!checkFile(iofile))
                        warn("Bad file permission, may cause failure(I/O same file infered)");
                    if (args.length != 3)
                        invokePlugin(args[0], ppAry(cropAry(args, 1, args.length - 2)));
                    else
                        invokePlugin(args[0], new String[] {args[1]});
                } else {
                    invokePlugin(args[0], ppAry(cropAry(args, 1, args.length - 1))); // now pass all arguments to plugin
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
     * Convert a binary word(32bit) to Java int
     * 
     * Example:
     *  {@code word2Int([0, 0, 0, 0]) -> 0}
     * 
     * @param ary array of 4 bytes
     * @return converted integer
     * @throws IllegalArgumentException if ary size is bigger/smaller than 4
     */
    public static int word2Int(byte[] ary) throws IllegalArgumentException {
        if (ary.length != 4)
            throw new IllegalArgumentException("Word size must be 4");
        ByteBuffer bb = ByteBuffer.wrap(ary);
        if (isLittleEndian)
            bb.order(ByteOrder.LITTLE_ENDIAN);
       return  bb.getInt();
    }

    /**
     * Convert a integer to binary 4-byte ary
     * 
     * @param i input integer to convert
     * @return array of 4-byte converted binary word
     */
    public static byte[] int2Word(int i) {
        ByteBuffer b = ByteBuffer.allocate(4); // Allocate a 4-byte ary
        if (isLittleEndian)
            b.order(ByteOrder.LITTLE_ENDIAN);
        b.putInt(i);
        return b.array();
    }

    /**
     * Raise a error message and then stops the program
     * 
     * @param msg error message
     * @since 1.0
     */
    public static void panic(String msg) {
        warn(msg);
        System.exit(1);
    }

    /**
     * Ask user for input from stdin
     * 
     * @param prompt information
     * @return user input
     * @since 1.0
     */
    public static String gets(String prompt) {
        stdout.print(prompt); // display ps1
        Scanner scan = new Scanner(stdin);
        String tmp = scan.nextLine();
        scan.close();
        return tmp;
    }

    /**
     * Finds and invokes a plugin using given args, and sync with I/O file if needed
     * <p>
     * No automatic tests required
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

        // void process or void main or AxmlFile process
        shouldWrite = false;
        Method process_method = null;
        if (pluginClass == null)
            panic("Failed to get plugin");
        try {
            process_method = pluginClass.getDeclaredMethod(pluginProcessMethodId, new Class<?>[] {AxmlFile.class, String[].class});
        } catch (NoSuchMethodException ignored) {
            try {
                process_method = pluginClass.getDeclaredMethod(pluginMainMethodId, new Class<?>[] {String[].class});
                try {
                    String[] carg = null;
                    if (Main.args.length != 0)
                        if (Main.args.length != 1)
                            if (Main.args.length != 2)
                                carg = ppAry(cropAry(Main.args, 1, Main.args.length - 1));
                            else carg = new String[] {Main.args[1]};
                        else
                            if (pluginClass.getName().equals(missingPluginId))
                                carg = new String[] {Main.args[0]}; // for plugin_missing plugin
                            else
                                carg = new String[0];
                    else
                        carg = new String[0];
                    process_method.invoke(pluginClass.getDeclaredConstructor(new Class<?>[] {}).newInstance(), new Object[] {carg}); // main(String[] args)
                    System.exit(0); // exit XD
                } catch (IllegalAccessException e) {
                    warn("Access should be public in plugin(invoking main)");
                    System.exit(5);
                } catch (InvocationTargetException e) {
                    warn("ITE caught, maybe it's thrown by plugin class(invoking main)");
                    e.getTargetException().printStackTrace(stderr);
                    panic("That's all");
                } catch (InstantiationException e) {
                    warn("Failed to construct class!!!(main)");
                    System.exit(5);
                }
            } catch (NoSuchMethodException e) {
                warn("Bad plugin. neither process nor main method could be found");
                System.exit(4);
            }
            // warn("Failed preparing plugin: cannot get process method");
            // System.exit(4); // Bad(
        }
        if (process_method.getReturnType().equals(AxmlFile.class))
            shouldWrite = true; // or I won't write generated file to out
        // invoke now!
        // First: read input, parse it
        AxmlFile axml = null;
        FileInputStream axml_fin = null;
        if (in == null)
            panic("This plugin requires AXML input");
        try {
            axml_fin = new FileInputStream(in);
            inBuf = new byte[axml_fin.available()];
            InputStream bufin = new BufferedInputStream(axml_fin);
            bufin.read(inBuf);
            bufin.close();
        } catch (FileNotFoundException ignored) {
            warn("Input file not found!");
            System.exit(2);
        } catch (IOException ignored) {
            warn("Cannot read input file");
            System.exit(2);
        } finally {
            if (axml_fin != null)
                try {
                      axml_fin.close();
                } catch (IOException ignored) {}
        }
        AxmlFile axml_in = new AxmlFile(inBuf);
        putsv("Invoking process at " + System.currentTimeMillis());
        try {
            axml = (AxmlFile)process_method.invoke(pluginClass.getDeclaredConstructor(new Class<?>[] {}).newInstance(), new Object[] {axml_in, args});
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
                warn("Output file moved(");
                System.exit(6);
            } catch (IOException ignored) {
                warn("Failed to output");
                System.exit(6);
            }
        } else puts("This plugin does not write to file.(");
    }

    /**
     * Load a class from class file, or panic
     * <p> loaded class to {@link Main#pluginClass}
     * <p> No automatic tests required
     * @param file class file path dir
     * @param name class name
     * @since 1.0
     */
    public static void loadClass(File file, String name) {
        // now normal
        try {
            // convert to URL
            URL url = file.toURI().toURL();
            putsv("Loading url class: " + url.toString());
            URL[] urls = new URL[] {url};
            // now use URLClassLoader to load this class
            cl = new URLClassLoader(urls);

            pluginClass = cl.loadClass(name); // must be same with file name
            if (pluginClass == null)
                System.exit(4);
            /*
            try {
                cl.close(); // close class loader
                // FIXED: I won't close class loader until process finished
            } catch (IOException ignored) {
                throw new ExtensionBootstrapError(Errno.EIOEXCEPT);
            }
            */
        } catch (MalformedURLException ignored) {}
        catch (ClassNotFoundException ignored) {
            panic("Failed to load plugin class! plugin classname must be same with plugin ID");
        } // eat(
    }

    /**
     * Finds and loads the plugin class
     * <p>
     * No automatic tests required
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
            if (!id.equals(missingPluginId)) {
                // invokePlugin(missingPluginId, new String[] {id}); // yes to plugin_missing plugin
                findAndLoadPlugin(missingPluginId); // FIXED (no return stmt)
                return;
            } else {
                warn("Plugin plugin_missing plugin not installed, try -h for help");
                throw new ExtensionBootstrapError(Errno.EPLUGINNODEF);
            }
        }

        loadClass(class_file.getParentFile(), id);
    }
}
