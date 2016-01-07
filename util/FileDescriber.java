package dirTree.util;

import java.io.File;
import java.io.Serializable;

public class FileDescriber implements Comparable<FileDescriber>, Serializable{
    public String filePath;
    public boolean isRecorded;
    public long size;
    public boolean canRead;
    public boolean canWrite;
    public boolean canExecute;
    public long lastModified;

    public FileDescriber(String absolutePath, boolean shouldRecord){
        filePath = absolutePath;
        isRecorded = shouldRecord;
        if ( shouldRecord )
            record();
    }

    protected void record(){
        File file = new File(filePath);
        size = file.getTotalSpace();
        canRead = file.canRead();
        canWrite = file.canWrite();
        canExecute = file.canExecute();
        lastModified = file.lastModified();
    }

    public boolean equals(FileDescriber fileDescriber){
        //doesn't compare lastModified
        if ( this.isRecorded != fileDescriber.isRecorded )
            return false;
        if ( ! this.filePath.equals(fileDescriber.filePath) )
            return false;
        if ( ! this.isRecorded )
            return true;
        if ( this.size != fileDescriber.size )
            return false;
        if ( this.canRead != fileDescriber.canRead )
            return false;
        if ( this.canWrite != fileDescriber.canWrite )
            return false;
        if ( this.canExecute != fileDescriber.canExecute )
            return false;
        return true;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Path: " + filePath + "\n");
        if ( isRecorded ) {
            builder.append("Size: " + size + "\n");
            builder.append("CanRead: " + canRead + "\n");
            builder.append("CanWrite: " + canWrite+ "\n");
            builder.append("CanExecute: " + canExecute+ "\n");
            builder.append("LastModified: " + lastModified + "\n");
        }
        return builder.toString();
    }

    @Override
    public int compareTo(FileDescriber describer){
        return this.filePath.compareTo(describer.filePath);
    }
}
