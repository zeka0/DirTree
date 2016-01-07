package dirTree.util;

import dirTree.util.exception.*;
import dirTree.util.recorder.FileMoveBacker;

import java.io.*;
import java.util.*;

/**
 *When a function returns a boolean, it means the problem isn't severe
 */
public class DirSystem {
    public static boolean renameTo(String fromPath, String toPath){
        File oriFile = new File(fromPath);
        return oriFile.renameTo( new File(toPath) );
    }

    public static String getFileName(String filePath){
        File file = new File(filePath);
        return file.getName();
    }

    public static boolean existsFile(String filePath){
        File file = new File(filePath);
        return file.exists();
    }

    public static boolean isWithin(String parPath, String childPath){
        String[] parPathSplit = parPath.split(File.separator);
        String[] childPathSplit = childPath.split(File.separator);
        if ( parPathSplit.length >= childPathSplit.length )
            return false;
        for ( int i = 0; i < childPathSplit.length - 1; ++i)
            if ( ! parPathSplit[i].equals(childPathSplit[i]) )
                return false;
        return true;
    }

    public static File[] listFiles(String dirPath)
        throws InfoDirException{
        File dir = new File(dirPath);
        if ( ! dir.isDirectory() )
            throw new InfoDirException("Dirpath not directory");
        File[] files = dir.listFiles();

        if ( files == null )
            throw new InfoDirException("File listing is null");
        return files;
    }

    public static String relativePath(String parPath, String childPath)
        throws InfoDirException{
        String[] parPathSplit = parPath.split(File.separator);
        String[] childPathSplit = childPath.split(File.separator);
        try {
            for ( int i = 0; i < parPathSplit.length; ++i){
                if ( ! parPathSplit[i].equals( childPathSplit[i] ) )
                    throw new InfoDirException("Path invalid");
            }

        } catch (ArrayIndexOutOfBoundsException ex){
            throw new InfoDirException("Path invalid");
        }

        String parDir = parPathSplit[parPathSplit.length - 1];

        int i = 0;
        for ( i = 0; i < childPathSplit.length; ++i ){
            if ( childPathSplit[i].equals(parDir) )
                break;
        }
        List<String> strs = new ArrayList<>();
        for ( i = i + 1; i < childPathSplit.length; ++i)
            strs.add( childPathSplit[i] );
        return join(strs);
    }

    public static boolean deleteFile(String filePath){
        File file = new File(filePath);
        return file.delete();
    }

    public static boolean deleteFiles(String dirPath) {
        //deletes all the files under this dir
        if ( ! checkFile(dirPath, true) )
            return false;

        File dir = new File(dirPath);

        try {
            for ( File file : dir.listFiles() )
                if ( file.isFile() )
                    if ( ! file.delete() )
                        System.err.println( "Error in deleting file: " + file.getAbsolutePath() );
        } catch (NullPointerException ex){
            return false;
        }
        return true;
    }

    private static void depthFirstStackCreate(File root, Stack<File> dirStack){
        dirStack.push(root);
        Stack<File> parDirStack = new Stack<>();
        parDirStack.push(root);
        File parDir = null;
        while ( ! parDirStack.isEmpty() ){
            parDir = parDirStack.pop();
            try {
                for (File subFile : parDir.listFiles())
                    if (subFile.isDirectory()) {
                        dirStack.push(subFile);
                        parDirStack.push(subFile);
                    }
            } catch (NullPointerException ex){
                System.err.println("Directory can't list files: " + parDir.getAbsolutePath());
            }
        }
    }

    private static void breastFirstQueueCreate(File root, Queue<File> dirQueue){
        dirQueue.add(root);
        Queue<File> parQueue = new ArrayDeque<>();
        parQueue.add(root);
        File parDir = null;
        while ( ! parQueue.isEmpty() ){
            parDir = parQueue.poll();
            try {
                for (File subFile : parDir.listFiles())
                    if (subFile.isDirectory()) {
                        dirQueue.add(subFile);
                        parQueue.add(subFile);
                    }
            } catch (NullPointerException ex){
                System.err.println("Directory can't list files: " + parDir.getAbsolutePath());
            }
        }
    }

    public static boolean fastDeleteDir(String dirPath){
        //this version can delete much deeper directory than its counterpart
        if ( ! checkFile(dirPath, true) )
            return false;

        Stack<File> dirStack = new Stack<>();
        File dir = new File(dirPath);

        depthFirstStackCreate(dir, dirStack);

        File subDir = null;
        while ( ! dirStack.isEmpty() ){
            subDir = dirStack.pop();

            deleteFiles( subDir.getAbsolutePath() );

            if ( ! subDir.delete() )
                System.err.println(
                        "Error in deleting dir: " + subDir.getAbsolutePath() );
        }
        return true;
    }

    public static boolean deleteDir(String dirPath){
        //delete using depth-first search
        if ( ! checkFile(dirPath, true) )
            return false;

        ArrayList<File> files = new ArrayList<>();
        ArrayList<File> dirs = new ArrayList<>();

        File dir = new File(dirPath);

        try {
            for (File file : dir.listFiles()) {
                if (file.isFile())
                    files.add(file);
                else if (file.isDirectory())
                    dirs.add(file);
            }
        } catch (NullPointerException ex){
            return false;
        }

        for ( File dirHere : dirs )
            if ( ! deleteDir(dirHere.getAbsolutePath()) )
                System.err.println("Error in deleting directory: " + dirHere.getAbsolutePath());
        for ( File fileHere : files )
            if ( ! deleteFile(fileHere.getAbsolutePath()) )
                System.err.println("Error in deleting file: " + fileHere.getAbsolutePath());
        if ( dir.delete() )
            return true;
        else return false;
    }

    public static String join(String... names)
    {
        StringBuilder builder=new StringBuilder();
        for( int i = 0; i < names.length; ++i )
        {
            if ( names[i].startsWith(File.separator) )
                names[i] = names[i].substring( File.separator.length() );
            builder.append(File.separator + names[i]);
        }

        return builder.toString();
    }

    public static String join(List<String> names){
        String[] arrnames = new String[names.size()];
        int i = 0;
        for ( String name : names )
            arrnames[ i++ ] = name;
        return join(arrnames);
    }

    public static boolean checkFile(String filePath, boolean isDir,
                                    boolean isReadable, boolean isWritable){
        if ( ! checkFile(filePath, isDir) )
            return false;
        File file = new File(filePath);
        if ( isReadable )
            if ( ! file.canRead() )
                return false;
        if ( isWritable )
            if ( ! file.canWrite() )
                return false;
        return true;
    }

    public static boolean checkFile(String filePath, boolean isDir){
        //checks the validity of the file
        File file = new File(filePath);
        if ( ! file.exists() )
            return false;
        if ( isDir )
            return file.isDirectory();
        else
            return file.isFile();
    }

    public static boolean createDir(String dirPath){
        File dir = new File(dirPath);

        if ( dir.exists() )
            return true;
        return dir.mkdirs();
    }

    public static boolean createFile(String filePath){
        File file = new File(filePath);

        if ( file.exists() )
            return true;
        boolean isCreated = false;
        try {
            file.getParentFile().mkdirs();
            isCreated = file.createNewFile();
        } catch (IOException ex){
            isCreated = false;
        }

        return isCreated;
    }

    public static void copyFile(String fileFromPath, String fileToPath)
        throws InfoFileException, SevereFileException{
        if ( ! checkFile(fileFromPath, false) )
            throw new InfoFileException("Copy file that doesn't exist");

        File fromFile = new File(fileFromPath);
        File toFile = new File(fileToPath);

        if ( ! createFile(toFile.getAbsolutePath()) )
            throw new InfoFileException("Can't create copy-to file");

        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;

        try {
            inputStream = new BufferedInputStream(
                    new FileInputStream(fromFile)
            );
            outputStream = new BufferedOutputStream(
                    new FileOutputStream(toFile)
            );

            byte[] bytes = new byte[1024 * 10];
            int readSize = -1;

            while ((readSize = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, readSize);
            }
            outputStream.flush(); //flush buffer

        } catch (IOException ex){
            throw new SevereFileException( ex.getMessage() );
        }
        finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ex){
                System.err.println( "Error in closing streams: " + ex.getMessage() );
            }
        }
    }

    public static void copyDir(String fromDirPath, String toDirPath, boolean isRecursive)
        throws InfoDirException, SevereDirException{

        if ( isWithin(fromDirPath, toDirPath) )
            throw new InfoDirException("Can't copy directories that overlap");

        if ( ! checkFile(fromDirPath, true) )
            throw new InfoDirException("Copy file that doesn't exist");

        File fromDir = new File(fromDirPath);

        if ( ! createDir(toDirPath) )
            throw new InfoDirException("Can't create to-dir");

        try {
            for (File fileHere : fromDir.listFiles()) {
                if (fileHere.isFile()) {
                    try {
                        copyFile(fileHere.getAbsolutePath(), join(toDirPath, fileHere.getName()));
                    } catch (InfoFileException ex) {
                        System.err.println("Error in copying: " +
                                fileHere.getAbsolutePath());
                    } catch (SevereFileException ex){
                        throw new SevereDirException( ex.getMessage() );
                    }
                } else if (fileHere.isDirectory()) {
                    if (isRecursive) {
                        String newToPath = join(toDirPath, fileHere.getName());
                        createDir(newToPath);
                        try {
                            copyDir(fileHere.getAbsolutePath(), newToPath, true);
                        } catch (InfoDirException ex) {
                            System.err.println("Error in copying: " +
                                    fileHere.getAbsolutePath());
                        }
                    }
                }
            }
        } catch (NullPointerException ex){
            throw new InfoDirException("File listing error");
        }

    }

    /**
     * Default to recurisvly copy
     */
    public static boolean cleanCopyDir(String fromDirPath, String toDirPath)
        throws InfoDirException{
        if ( isWithin(fromDirPath, toDirPath) )
            throw new InfoDirException("Can't copy directories that overlap");

        if ( ! checkFile(fromDirPath, true) )
            throw new InfoDirException("Copy file that doesn't exist");

        Queue<File> dirQueue= new ArrayDeque<>();

        if ( ! createDir(toDirPath) )
            throw new InfoDirException("Can't create to-dir");

        File root = new File( fromDirPath );
        breastFirstQueueCreate(root, dirQueue);
        File subDir = null;
        FileMoveBacker backer = new FileMoveBacker();

        try {
            while ( ! dirQueue.isEmpty() ){
                subDir = dirQueue.poll();
                for (File file : subDir.listFiles()) {
                    if (file.isFile()) {
                        backer.addFile(file, subDir.getAbsolutePath());
                        try {
                            copyFile(file.getAbsolutePath(),
                                    join(toDirPath, relativePath(fromDirPath, file.getAbsolutePath())));
                        } catch (InfoFileException ex) {
                        System.err.println( ex.getMessage() );
                        }
                    }
                }
            }
        } catch (NullPointerException | SevereFileException ex) {
            System.err.println( ex.getMessage() );
            System.err.println( "Aborting copying process" );
            backer.moveBack();
            return false;
        }
        return true;
    }

    public static void moveFile(String fromFilePath, String toFilePath)
        throws InfoFileException, SevereFileException{
        if ( renameTo(fromFilePath, toFilePath) )
            return;
        copyFile(fromFilePath, toFilePath);
        deleteFile(fromFilePath);
    }

    public static void moveDir(String fromDirPath, String toDirPath)
        throws InfoDirException, SevereDirException{
        if ( renameTo(fromDirPath, toDirPath) )
            return;

        copyDir(fromDirPath, toDirPath, true);
        deleteDir(fromDirPath);
    }

    public static boolean cleanMoveDir(String fromDirPath, String toDirPath){
        try {
            if ( cleanCopyDir(fromDirPath, toDirPath) )
                deleteDir(fromDirPath);
            else return false;
        } catch (InfoDirException ex){
            System.err.println( ex.getMessage() );
            System.err.println( "Aborting moving process" );
            return false;
        }
        return true;
    }

    public static void main(String[] args){
        try {
            String desktop = "/Users/mac/Desktop";
            System.out.println(relativePath("/Users/mac", "/Users/mac/Desktop"));
            moveDir("/Users/mac/Desktop/xx", "/Users/mac/Desktop/fds/cd");
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

}