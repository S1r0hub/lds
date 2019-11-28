/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lds.LdManager.ontologies;

import java.util.List;
import lds.indexing.LdIndexer;
import lds.resource.R;
import sc.research.ldq.LdDataset;

/**
 *
 * @author Fouad Komeiha
 */
public class DBpediaLdManager extends OntologiesLdManager {

    private boolean useIndex;
    private LdIndexer conceptsIndex;
    private LdDataset dataSetInitial;
    
   
    
    public DBpediaLdManager(LdDataset dataSetInitial , boolean useIndex) throws Exception {
        super(dataSetInitial);
        this.useIndex = useIndex;
        this.dataSetInitial = dataSetInitial;
    
    }
    
    public void loadIndexes() throws Exception {
        String conceptsIndexFile = System.getProperty("user.dir") + "/Indexes/Ontologies/DBpedia/concepts_index_" + dataSetInitial.getName().toLowerCase().replace(" ", "_") + ".db";
        conceptsIndex = new LdIndexer(conceptsIndexFile);
             
            
    }
    
    public void closeIndexes(){
        if (useIndex) {
            conceptsIndex.close();
        }
        
    }
    

    @Override
    public List<String> getConcepts(R a , List<String> namespaces , boolean dataAugmentation) {
        if(useIndex){
             return LdIndexer.getListFromIndex(dataSetInitial , conceptsIndex , a.getUri().stringValue() , baseClassPath + "getConcepts"  , a , namespaces , dataAugmentation);
        }
        
        return super.getConcepts(a , namespaces , dataAugmentation);
    }
    

        
    
    
}
