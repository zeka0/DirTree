package dirTree;

import java.io.*;
import java.util.ArrayList;

public class TestPickling {
    public static class Pickle implements Serializable{
        public String name;
        public ArrayList<Pickle> subPickes;

        public Pickle(String name){
            this.name = name;
            subPickes = new ArrayList<>();
        }

        @Override
        public String toString(){
            StringBuilder builder = new StringBuilder();
            builder.append("I am " + name + "And I have\n");
            for ( Pickle pickle : subPickes )
                builder.append(pickle.name + "\n");
            return builder.toString();
        }
    }

    public static final String desPath = "/Users/mac/Desktop/outFile.ser";

    public static void writeObj(){
        Pickle[] pickles = new Pickle[2];
        pickles[0] = new Pickle("John Corner");
        pickles[1] = new Pickle("John S");
        pickles[0].subPickes.add(new Pickle("Son"));
        pickles[0].subPickes.add(pickles[1]);

        File outFile = new File( desPath );
        try {
            if ( ! outFile.exists() )
                outFile.createNewFile();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream( outFile )
            );
            for ( Pickle pickle : pickles )
                objectOutputStream.writeObject(pickle);

        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public static void readObj(){
        Pickle[] pickles = new Pickle[2];
        File inFile = new File( desPath );
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    new FileInputStream( inFile )
            );
            for ( int i = 0; i < pickles.length; ++i)
                pickles[i] = (Pickle) objectInputStream.readObject();
            for ( Pickle pickle : pickles)
                System.out.println( pickle );

        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args){
        writeObj();
        readObj();
    }
}
