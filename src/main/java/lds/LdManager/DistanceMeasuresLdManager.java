/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lds.LdManager;

import java.util.ArrayList;
import java.util.List;
import ldq.LdDataset;
import lds.LdManager.ontologies.Ontology;
import lds.indexing.LdIndex;
import lds.indexing.LdIndexerManager;
import lds.resource.R;
import org.openrdf.model.URI;
import slib.graph.model.impl.repo.URIFactoryMemory;
import slib.graph.model.repo.URIFactory;

/**
 *
 * @author Fouad Komeiha
 */
public class DistanceMeasuresLdManager extends LdManagerBase{
    private boolean useIndex;
    
    protected LdIndex edgesIndex;
    protected LdIndex commonObjectsIndex;
    protected LdIndex commonSubjectsIndex;
    protected LdIndex countShareCommonObjectsIndex;
    protected LdIndex objectsIndex;
    protected LdIndex subjectsIndex;
    
    
    protected LdIndexerManager manager;
    
    public DistanceMeasuresLdManager(LdDataset dataset , boolean useIndex) {
        super(dataset);
        this.useIndex = useIndex;

    }
    
    public void loadIndexes() throws Exception {
        
        manager = LdIndexerManager.getManager();
        
        String edgesIndexFile = System.getProperty("user.dir") + "/Indexes/Distance_Measures/edges_index_" + dataset.getName().toLowerCase().replace(" ", "_") + ".db";
        edgesIndex = manager.loadIndex(edgesIndexFile);
        
        String objectsIndexFile = System.getProperty("user.dir") + "/Indexes/Distance_Measures/objects_index_" + dataset.getName().toLowerCase().replace(" ", "_") + ".db";
        objectsIndex = manager.loadIndex(objectsIndexFile);
        
        String commonObjectsIndexFile = System.getProperty("user.dir") + "/Indexes/Distance_Measures/commonObjects_index_" + dataset.getName().toLowerCase().replace(" ", "_") + ".db";
        commonObjectsIndex = manager.loadIndex(commonObjectsIndexFile);
        
        String countShareCommonObjectsIndexFile = System.getProperty("user.dir") + "/Indexes/Distance_Measures/countShareCommonObjects_index_" + dataset.getName().toLowerCase().replace(" ", "_") + ".db";
        countShareCommonObjectsIndex = manager.loadIndex(countShareCommonObjectsIndexFile);
        
        String commonSubjectsIndexFile = System.getProperty("user.dir") + "/Indexes/Distance_Measures/commonSubjects_index_" + dataset.getName().toLowerCase().replace(" ", "_") + ".db";
        commonSubjectsIndex = manager.loadIndex(commonSubjectsIndexFile);
        
        String countSubjectIndexFile = System.getProperty("user.dir") + "/Indexes/Distance_Measures/subjects_index_" + dataset.getName().toLowerCase().replace(" ", "_") + ".db";
        subjectsIndex = manager.loadIndex(countSubjectIndexFile);
    }
    
     public void closeIndexes(){
        if (useIndex) {
            manager.closeIndex(edgesIndex);
            manager.closeIndex(objectsIndex);
            manager.closeIndex(commonObjectsIndex);
            manager.closeIndex(commonSubjectsIndex);
            manager.closeIndex(countShareCommonObjectsIndex);
            manager.closeIndex(subjectsIndex);
        }
     }
    
    @Override
    public List<String> getEdges(R a) { 
        if(useIndex){
            return edgesIndex.getListFromIndex(dataset , Utility.createKey(a) , baseClassPath + "getEdges" , a);
        }
        
        return super.getEdges(a);
    }
    
    @Override
    public List<String> getObjects(R a){
        if(useIndex){
            return objectsIndex.getListFromIndex(dataset , Utility.createKey(a) , baseClassPath + "getObjects" , a );            
        }
        
        return super.getObjects(a);
    }
    
    @Override
    public List<String> getSubjects(R a) {
        if(useIndex){
            return subjectsIndex.getListFromIndex(dataset , Utility.createKey(a) , baseClassPath + "getSubjects" , a );
        }
        
        return super.getSubjects(a);
    } 
    
    @Override
    public List<String> getCommonObjects(R a , R b){
        if(useIndex){
           return commonObjectsIndex.getListFromIndex(dataset , Utility.createKey(a , b) , baseClassPath + "getCommonObjects" , a , b);
        }
        
        return super.getCommonObjects(a, b);
    }
    
    @Override
    public List<String> getCommonSubjects(R a , R b){
        if(useIndex){
           return commonSubjectsIndex.getListFromIndex(dataset , Utility.createKey(a , b) , baseClassPath + "getCommonSubjects" , a , b);
        }
        
       return super.getCommonSubjects(a, b);
    }
    
    ////////////////////////////////////////////
    @Override
    public List<String> getCommonObjects(R a){
        if(useIndex){
           return commonObjectsIndex.getListFromIndex(dataset , Utility.createKey(a) , baseClassPath + "getCommonObjects" , a);
        }
        
        return super.getCommonObjects(a);
    }
    
    @Override
    public List<String> getCommonSubjects(R a){
        if(useIndex){
           return commonSubjectsIndex.getListFromIndex(dataset , Utility.createKey(a) , baseClassPath + "getCommonSubjects" , a);
        }
        
       return super.getCommonSubjects(a);
    }
    /////////////////////////////////////////////
    
    @Override
    public int countShareCommonObjects(URI link , R a){
        if(useIndex){
            return countShareCommonObjectsIndex.getIntegerFromIndex(dataset , Utility.createKey(a , link), baseClassPath + "countShareCommonObjects" , link , a);
        }
        
        return super.countShareCommonObjects(link, a);
    }
    
    
    public List<URI> getEdges(R a , R b) {

        URIFactory factory = URIFactoryMemory.getSingleton();

        List<URI> edges = new ArrayList<>();
        List<String> edges_a = getEdges(a);
        List<String> edges_b = getEdges(b);

        if(( edges_a == null || edges_a.contains("-1")) ||  ( edges_b == null || edges_b.contains("-1")) )
            return null;
        
        if(! edges_a.contains("-1") && ! edges_b.contains("-1")){
            edges_a.addAll(edges_b);
        
            edges_a.forEach((edge) -> {
                URI e = factory.getURI(Ontology.decompressValue(edge));
                if(!edges.contains(e))
                    edges.add(e);
            });
        }
        return edges;
    }
    
    /**
     * Number of distinct objects that resource a links to (i.e. a --l--> o).
     * @param a subject resource
     */
    public int countObject(R a) {

        List<String> list = getObjects(a);
        if(list == null) { return 0; }
        
        List<String> objects = new ArrayList<>();
        
        for(String item: list){
            String string[] =  item.split("\\|");
            String object = string[0];

            if(objects.isEmpty() || ! objects.contains(object))
                objects.add(object);
        }
        
        return objects.size();
    }

    /**
     * Count how many resources are connected to <code>a</code> by link <code>l</code>.<br/>
     * Effectively, this returns the (amount of) objects of triples having <code>a</code> as subject.
     * @param l the link (URI)
     * @param a the subject resource
     */
    public int countObject(URI l,  R a) {

        List<String> objects_a = getObjects(a);
        if(objects_a == null) { return 0; }
        
        int count = 0;
        for(String objects: objects_a){
            String string[] =  objects.split("\\|");
            if (string.length < 2) { continue; } // prevent index out of bounds
            if (string[1].equals(Ontology.compressValue(l))) { count++; }
        }
        
        return count;
    }
    
    /**
     * Number of distinct subjects that link to resource a (i.e. s --l--> a).
     * @param a object resource
     */
    public int countSubject(R a) {
    
        List<String> list = getSubjects(a);
        if(list == null) { return 0; }
        
        List<String> subjects = new ArrayList<>();
        
        for(String item: list){
            String string[] = item.split("\\|");
            String subject = string[0];

            if(subjects.isEmpty() || !subjects.contains(subject) )
                subjects.add(subject);
        }
        
        return subjects.size();
    }

    public int countSubject(URI l, R a) {

        // FIX by S1r0hub: You store "-1" in the index if the result was previously null.
        // Null is returned for empty lists (e.g. when retrieving common subjects).
        // Therefore, this list can contain only "-1" but you don't check for it.
        // Added catching this case in the for-loop below.
        List<String> subjects_a = getSubjects(a);
        if(subjects_a == null) { return 0; }
        
        String uri = Ontology.compressValue(l);
        
        int count = 0;
        for(String subjects: subjects_a){
            if (subjects.equals("-1")) { continue; } // skip to deal with "-1" entries from indexing
            String string[] = subjects.split("\\|", 2);
            if (string[1].equals(uri)) { count++; }
        }
        
        return count;
    }
    
    /*@Override
    public List<String> getCommonObjects(R a , R b){
       List<String> objects = getCommonObjects(a);
        
       List<String> commonObjects = new ArrayList<>();
                
       if(objects == null || objects.contains("-1"))
            return null;
        
        
       for(String items:objects){
            String string[] =  items.split("\\|");
            String object = string[0];
            if(b.toString().equals(Ontology.decompressValue(object)))
                commonObjects.add(object);
       } 
        
       return commonObjects;
    }
    
    @Override
    public List<String> getCommonSubjects(R a , R b){
        List<String> subjects = getCommonSubjects(a);
        
        List<String> commonSubjects = new ArrayList<>();
                
        if(subjects == null || subjects.contains("-1"))
            return null;
        
        
        for(String items:subjects){
            String string[] =  items.split("\\|");
            String subject = string[0];
            
            if(b.toString().equals(Ontology.decompressValue(subject)))
                commonSubjects.add(subject);
        } 
        
        return commonSubjects;
    }*/
    
    /**
     * Checks if resource a and b are directly connected through link l.<br/>
     * Retrieves all objects a is connected to and checks if they are linked to a.
     * @param l the link (URI)
     * @param a first resource
     * @param b second resource
     */
    public boolean isDirectlyConnected(URI l, R a, R b) {
        List<String> objects = getObjects(a);
        
        if(objects == null)
            return false;
        else if(objects.contains(Ontology.compressValue(b)+"|"+Ontology.compressValue(l)))
            return true;
        
        return false;
    }
    
    public int countCommonObjects(URI link, R a , R b){

        int count = 0;
        List<String> commonObjects = getCommonObjects(a , b);
        if (commonObjects == null || commonObjects.contains("-1")) { return count; }
        String l = Ontology.compressValue(link);

        for(String items : commonObjects){
            String string[] =  items.split("\\|");
            String property1 = string[1];
            String property2 = string[2];
            
            if(property1.equals(l) && property2.equals(l))
                count++;
        }

        return count;
    }
    
    public int countShareCommonObjects(URI l, R a, R b) {

        int count = 0;
        List<String> commonObjects = getCommonObjects(a , b);
        if(commonObjects == null || commonObjects.contains("-1"))
            return count;
        
        String link = Ontology.compressValue(l);
        
        for(String items:commonObjects){
            String string[] =  items.split("\\|");
            String property1 = string[1];
            String property2 = string[2];
            
            if(property1.equals(link) && property2.equals(link))
                count++;
        }
    
        return count;
    }
    
    public int countTyplessCommonObjects(URI li, URI lj, R a , R b) {

        int count = 0;
        List<String> commonObjects = getCommonObjects(a , b);
        if(commonObjects == null || commonObjects.contains("-1"))
            return count;
        
        for(String items:commonObjects){
            String string[] =  items.split("\\|");
            String property1 = string[1];
            String property2 = string[2];
            
            if(property1.equals(Ontology.compressValue(li)) && property2.equals(Ontology.compressValue(lj)))
                count++;
        }
    
        return count;
    }

    public int countShareCommonSubjects(URI l, R a, R b) {

        int count = 0;
        List<String> commonSubjects = getCommonSubjects(a , b);
        if(commonSubjects == null || commonSubjects.contains("-1"))
            return count;
        
        String link = Ontology.compressValue(l);
        for(String items:commonSubjects){
            String string[] =  items.split("\\|");
            String property1 = string[1];
            String property2 = string[2];
            
            if(property1.equals(link) && property2.equals(link))
                count++;
        }

        return count;
    }
    
    public int countTyplessCommonSubjects(URI li, URI lj , R a , R b){

        int count = 0;
        List<String> commonSubjects = getCommonSubjects(a , b);
        if(commonSubjects ==  null || commonSubjects.contains("-1"))
            return count;
        
        for(String items:commonSubjects){
            String string[] =  items.split("\\|");
            String property1 = string[1];
            String property2 = string[2];
            
            if(property1.equals(Ontology.compressValue(li)) && property2.equals(Ontology.compressValue(lj)))
                count++;
        }

        return count;
    }

    
    public int countCommonSubjects(URI link, R a , R b) {

        int count = 0;
        List<String> commonSubjects = getCommonSubjects(a , b);
        if(commonSubjects == null || commonSubjects.contains("-1"))
            return count;

        String l = Ontology.compressValue(link);
        for(String items:commonSubjects){
            String string[] =  items.split("\\|");
            String property1 = string[1];
            String property2 = string[2];
            
            if(property1.equals(l) && property2.equals(l))
                count++;
        }
    
        return count;
    }

}
