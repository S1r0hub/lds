/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lds.indexing;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Fouad Komeiha
 */
public class LdIndexer {
    private static Map<String, LdIndex> indexes;
    
    static private LdIndexer indexManager;
    
    private LdIndexer(){
        indexes = new HashMap<>();
    }
    
    public static LdIndexer getManager() {
        if(indexManager == null){
            indexManager = new LdIndexer();            
        }        
        return indexManager;
    }
    
    public LdIndex loadIndex(String indexFilePath) throws Exception{
        LdIndex index = null;
        if(!indexes.containsKey(indexFilePath)){
            index = new LdIndex(indexFilePath);
            indexes.put(indexFilePath , index);
        }
        
        index = indexes.get(indexFilePath);
        return index;
    }
    
    public void closeIndex(LdIndex index){
        if(indexes.containsKey(index.getDBPath())){
            index.close();
            indexes.remove(index.getDBPath());
        }

    }
    
    public void closeAllIndexes(){
        this.indexes = null;
    }
    
}
