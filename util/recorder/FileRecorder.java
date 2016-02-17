package dirTree.util.recorder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileRecorder{
    protected Map<String, File> files;

    public FileRecorder(){
        files = new HashMap<>();
    }

    public Map<String, File> getFiles(){
        return files;
    }

    public void addFile(File file){
        files.put(file.getName(), file);
    }

    //the key is of general purpose
    //eg: In fileMoveBacker, the key means the original directory
    //While in fileRecorder, the key has no use
    //We simply add it here as a convenient way to store information
    public void addFile(File file, String key){
        files.put(key, file);
    }
}
