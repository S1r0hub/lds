/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lds.measures.lods.ontologies;

import java.util.List;
import lds.resource.R;
import slib.utils.i.Conf;

/**
 *
 * @author Fouad Komeiha
 */
public class O_Schema implements O {

    @Override
    public List<String> getConcepts(R r) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void initializeOntology(Conf config) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String toString(){
        return "Schema";
    }

    @Override
    public O getOntology() {
        return this;
    }
    
    @Override
    public boolean equals(Object o){
        
        if (o == this) { 
            return true; 
        } 
  
        if (!(o instanceof O)) { 
            return false; 
        } 
           
        O ontology = (O) o; 
          
        return ontology.toString().equals("Schema");
        
    }

    
    
}
