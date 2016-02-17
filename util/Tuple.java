package dirTree.util;

import java.io.Serializable;

@Deprecated
public class Tuple implements Serializable{
    public final Class[] classes;
    private Object[] objects;

    public Tuple(Class...classes){
        this.classes = classes;
    }

    public void setObjects(Object...objects){
        if ( classes.length != objects.length )
            throw new IllegalArgumentException("Wrong length");
        objects = objects;
    }

    public Object[] getObjects(){
        return objects;
    }

    public Class[] getClasses(){
        return classes;
    }

    public Object get(int i){
        return objects[i];
    }

    public Class getClass(int i){
        return classes[i];
    }
}

