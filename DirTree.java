package dirTree;

import dirTree.util.FileDescriber;
import dirTree.util.Pair;
import dirTree.util.exception.DirInvalidException;
import dirTree.util.DirSystem;
import dirTree.util.exception.FileInvalidException;
import dirTree.util.exception.InfoDirException;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

class DirNode implements Serializable, Comparable<DirNode>{
    public ArrayList<DirNode> childNodes; //child dir nodes
    public DirNode parNode;
    final public String filePath; //this path
    public ArrayList<FileDescriber> describers;

    public DirNode(String filePath) {
        childNodes = new ArrayList<>();
        describers = new ArrayList<>();
        this.filePath = filePath;
        this.parNode = null;
    }

    public DirNode(String filePath, DirNode parNode){
        this(filePath);
        this.parNode = parNode;
    }

    @Override
    public int compareTo(DirNode dirNode){
        return this.filePath.compareTo(dirNode.filePath);
    }

    public void removeChild(DirNode node){
        childNodes.remove(node);
    }

    public void unlinkPar(){
        if ( parNode != null )
            parNode.removeChild(this);
    }

    protected void buildTree(){
        try {
            for ( File file : DirSystem.listFiles(filePath) )
                if ( file.isDirectory() ){
                    DirNode newNode =
                            new DirNode(file.getAbsolutePath(), this);
                    childNodes.add( newNode );
                    newNode.buildTree();
                }
                else if ( file.isFile() ){
                    this.describers.add(
                            new FileDescriber(file.getAbsolutePath(), true));
                }
            Collections.sort(this.describers);
            Collections.sort(this.childNodes);

        } catch (InfoDirException ex){
            System.err.println(ex.getMessage());
        }
    }

    public boolean shallowCompare(DirNode dirNode){
        if ( ! filePath.equals(dirNode.filePath) )
            return false;
        if ( this.describers.size() != dirNode.describers.size() )
            return false;
        for ( int i = 0; i < describers.size(); ++i)
            if ( ! describers.get(i).equals(dirNode.describers.get(i) ) )
                return false;
        return true;
    }

    public boolean deepCompare(DirNode dirNode){
        if ( ! shallowCompare(dirNode) )
            return false;
        if ( this.childNodes.size() != dirNode.childNodes.size() )
            return false;
        for ( int i = 0; i < childNodes.size(); ++i)
            if ( ! childNodes.get(i).deepCompare(dirNode.childNodes.get(i)) )
                return false;
        return true;
    }

    protected void symentricCompFileDescribers(DirNode dirNode,
                                               List<FileDescriber> thisDesList,
                                               List<FileDescriber> thatDesList){
        int i = 0;
        int j = 0;
        while ( i < describers.size() &&
                j < dirNode.describers.size() ){
            if ( describers.get(i).
                    compareTo(dirNode.describers.get(j)) < 0 ){
                thisDesList.add(describers.get(i));
                ++i;
            }
            else if ( describers.get(i).
                    compareTo(dirNode.describers.get(j)) > 0 ){
                thatDesList.add(dirNode.describers.get(j));
                ++j;
            }
            else {
                if ( ! describers.get(i).equals(describers.get(j)) ){
                    thisDesList.add( describers.get(i) );
                    thatDesList.add( dirNode.describers.get(j) );
                }
                ++i;
                ++j;
            }
        }
        if ( i < describers.size() )
            for ( ; i < describers.size(); ++i)
                thatDesList.add( describers.get(i) );
        if ( j < dirNode.describers.size() )
            for ( ; j < dirNode.describers.size(); ++j)
                thatDesList.add( dirNode.describers.get(j) );
    }

    protected void symentricCompChildNodes(DirNode dirNode,
                                               List<DirNode> thisDirList,
                                               List<DirNode> thatDirList,
                                           List<FileDescriber> thisDesList,
                                           List<FileDescriber> thatDesList){
        int i = 0;
        int j = 0;
        while ( i < childNodes.size() &&
                j < dirNode.childNodes.size() ){
            if ( childNodes.get(i).
                    compareTo( dirNode.childNodes.get(j) ) < 0 ){
                thisDirList.add( childNodes.get(i) );
                ++i;
            }
            else if ( childNodes.get(i).
                    compareTo( dirNode.childNodes.get(j) ) > 0 ){
                thatDirList.add( dirNode.childNodes.get(j) );
                ++j;
            }
            else {
                childNodes.get(i).symentricComp(dirNode.childNodes.get(j),
                        thisDesList, thatDesList,
                        thisDirList, thatDirList);
                ++i;
                ++j;
            }
        }
        if ( i < childNodes.size() )
            for ( ; i < childNodes.size(); ++i)
                thisDirList.add(childNodes.get(i));
        if ( j < dirNode.childNodes.size() )
            for ( ; j < dirNode.childNodes.size(); ++j)
                thatDirList.add( dirNode.childNodes.get(j) );
    }

    /**
     * Warning: can only compare two trees that rooted in the same root
     * thisDesListr contains the describers that is in thisNode but not in thatNode
     * thisDirList contains the dirNodes that is in thisNode but not in thatNode
     * */
    public void symentricComp(DirNode dirNode,
                              List<FileDescriber> thisDesList, List<FileDescriber> thatDesList,
                              List<DirNode> thisDirList, List<DirNode> thatDirList){

        symentricCompChildNodes(dirNode,
                thisDirList, thatDirList,
                thisDesList, thatDesList);
        symentricCompFileDescribers(dirNode,
                thisDesList, thatDesList);
    }
}

public class DirTree implements Serializable{
    public DirNode rootNode;
    private Date lastModifiedDate;

    public DirTree(String rootPath) {
        rootNode = new DirNode(rootPath);
        buildTree();
        lastModifiedDate = new Date();
    }

    protected void buildTree(){
        rootNode.buildTree();
    }

    public void updateDate(){
        lastModifiedDate = new Date();
    }

    /**
     * preserve the fromNode's original name
     * */
    public void moveNodeTo(DirNode fromNode, DirNode toNode) {
        if ( fromNode == toNode )
            return;

        boolean isSucc = true;
        String newPath = DirSystem.join( toNode.filePath,
                DirSystem.getFileName(fromNode.filePath) );

        isSucc = DirSystem.cleanMoveDir(fromNode.filePath, newPath);
        if ( isSucc ){
            fromNode.unlinkPar();
            toNode.childNodes.add( new DirNode(newPath, toNode) );
            updateDate();
        }
    }

    /**
     * preserve the fromNode's original name
     * */
    public void copyNodeTo(DirNode fromNode, DirNode toNode){

        boolean isSucc = true;
        String newPath = DirSystem.join( toNode.filePath,
                DirSystem.getFileName(fromNode.filePath) );
        try {
            isSucc = DirSystem.cleanCopyDir(fromNode.filePath, newPath);
        } catch (InfoDirException ex){
            System.err.println( ex.getMessage() );
            isSucc = false;
        }
        finally {
            if ( isSucc ){
                toNode.childNodes.add( new DirNode(newPath, toNode) );
                updateDate();
            }
        }
    }

    public void deleteNode(DirNode node){
        if ( node == rootNode ){
            System.err.println("Can't delete root node");
            return;
        }

        if ( DirSystem.deleteDir(node.filePath) )
            updateDate();
        node.unlinkPar();
    }

    public String getName(){
        return rootNode.filePath + lastModifiedDate;
    }

    public boolean equals(DirTree dirTree){
        return rootNode.deepCompare(dirTree.rootNode);
    }

    /**
     * Roughly and quickly compares two dirTrees
     * Can't tell bytes-difference of two trees
     * Can only compare two trees that rooted in the same root
     * */
    public static Pair<
            Pair< List<DirNode>, List<FileDescriber> >,
                Pair< List<DirNode>, List<FileDescriber> > >
        symentricComp(DirTree leftTree, DirTree rightTree){
        List<DirNode> thisDirList = new ArrayList<>();
        List<DirNode> thatDirList = new ArrayList<>();
        List<FileDescriber> thisDesList = new ArrayList<>();
        List<FileDescriber> thatDesList = new ArrayList<>();

        if ( ! leftTree.rootNode.filePath.equals(rightTree.rootNode.filePath) ){
            thisDirList.add(leftTree.rootNode);
            thatDirList.add(rightTree.rootNode);
        }

        else {
            leftTree.rootNode.symentricCompChildNodes(rightTree.rootNode,
                    thisDirList, thatDirList,
                    thisDesList, thatDesList);
        }

        return new Pair<>(
                new Pair<>(thisDirList, thisDesList),
                new Pair<>(thatDirList, thatDesList)
        );
    }
}