package dirTree;

import dirTree.util.DirSystem;

import java.io.*;

public class TestDirTree {
    public static String filePath = "/Users/mac/Desktop/xx";
    public static String filePath2 = "/Users/mac/Desktop/yy";
    public static File file = new File("/Users/mac/Desktop/xxFile.ser");
    public static void testPickle(){
        DirTree newTree = new DirTree(filePath);
        DirTree oldTree = null;
        DirSystem.createFile(file.getAbsolutePath());

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(file)
            );
            objectOutputStream.writeObject(newTree);
            //DirSystem.safeDeleteDir( DirSystem.join(filePath, "bin") );
            newTree = new DirTree(filePath);
            ObjectInputStream inputStream = new ObjectInputStream(
                    new FileInputStream(file)
            );
            oldTree = (DirTree)inputStream.readObject();
            System.out.println(newTree.equals(oldTree));

        } catch ( IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }
    public static void main(String[] args){
        DirTree treex = new DirTree(filePath);
        DirSystem.cleanMoveDir(DirSystem.join(filePath, "fileSystem"), filePath2);
        DirTree treey = new DirTree(filePath);
        DirTree.symentricComp(treex, treey);
    }
}
