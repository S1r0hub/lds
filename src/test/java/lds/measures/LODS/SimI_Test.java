/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lds.measures.LODS;

import java.util.ArrayList;
import java.util.List;
import lds.config.Config;
import lds.config.ConfigParam;
import lds.dataset.LdDatasetCreator;
import lds.measures.lods.ontologies.*;
import ldq.LdDataset;
import lds.measures.lods.SimI;
import lds.resource.R;
import org.junit.Test;

/**
 *
 * @author Fouad Komeiha
 */
public class SimI_Test {
    
    @Test
    public void SimI_Test() throws Exception{
        LdDataset dataSetMain = LdDatasetCreator.getDBpediaDataset();
                
        Config config = new Config();
        config.addParam(ConfigParam.useIndexes, false);
        config.addParam(ConfigParam.LdDatasetMain , dataSetMain);
        config.addParam(ConfigParam.dataAugmentation , true);
        
        List<O> ontologyList = new ArrayList<>();
        
        O dbpedia = new O_DBpedia();
        ontologyList.add(dbpedia);
        
        O dbpedia_de = new O_DBpedia_de();
        ontologyList.add(dbpedia_de);
        
        O dbpedia_fr = new O_DBpedia_fr();
        ontologyList.add(dbpedia_fr);
        
        O yago = new O_Yago();
        ontologyList.add(yago);

//        O wikiData = new O_WikiData();
//        ontologyList.add(wikiData);        
        
        config.addParam(ConfigParam.ontologyList, ontologyList);
        
        SimI simi = new SimI(config);
        
        simi.loadIndexes();
                
        R r1 = new R("http://dbpedia.org/resource/Paris");
        R r2 = new R("http://dbpedia.org/resource/New_York");
       
        System.out.println(simi.compare(r1, r2)); 

        simi.closeIndexes();
    }
}
