import org.duangsuse.tinyaxml.AxmlFile;
import org.duangsuse.tinyaxml.Main;

// print axml to_string

/**
 * Print axml.to_s
 * 
 * @author duangsuse
 * @since 1.0
 */
public class to_s {
    public to_s() {}

    public void process(AxmlFile a) {
        Main.puts(a.toString());
    }
}
