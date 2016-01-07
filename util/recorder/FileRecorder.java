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

    public void addFile(File file, String key){
        files.put(key, file);
    }
}
