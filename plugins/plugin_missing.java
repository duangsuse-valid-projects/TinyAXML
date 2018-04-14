import org.duangsuse.tinyaxml.Main;

// simple plugin_missing plugin

/**
 * A example plugin_missing plugin
 * 
 * @author duangsuse
 * @since 1.0
 */

 public class plugin_missing {
     public plugin_missing() {}

     public static void main(String[] args) { // CLI will pass all cmdline arguments to you
        if (args.length == 0) {
            Main.warn("Argument needed");
            Main.warn(Main.helpMessage);
            System.exit(1);
        }
        Main.warn("Could not find plugin: " + args[0]);
     }
 }
