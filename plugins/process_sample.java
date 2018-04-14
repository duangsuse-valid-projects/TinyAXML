import org.duangsuse.tinyaxml.AxmlFile;

// process!!!

/**
 * A simple plugin, make blank axml output
 * 
 * @author duangsuse
 * @since 1.0
 */
public class process_sample {
    public process_sample() {}

    public AxmlFile process(AxmlFile input, String[] args) {
        return new AxmlFile(new byte[0]);
    }
}
