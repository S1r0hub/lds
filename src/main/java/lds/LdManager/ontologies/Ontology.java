/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lds.LdManager.ontologies;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lds.indexing.LdIndex;
import lds.indexing.LdIndexer;
import lds.measures.lods.ontologies.*;
import lds.resource.R;
import org.openrdf.model.URI;

/**
 *
 * @author Fouad Komeiha
 */
public class Ontology {
    private static LdIndexer manager;
    private static String prefixIndexPath = System.getProperty("user.dir") + "/Indexes/Prefixes/prefixes_index.db";
    private static String namespaceIndexPath = System.getProperty("user.dir") + "/Indexes/Prefixes/namespaces_index.db";
    private static LdIndex prefixIndex;
    private static LdIndex nameSpaceIndex;

    public static O getOntologyFromNameSpace(String nameSpace) {
        O ontology = null;
        switch (nameSpace) {
            case "http://dbpedia.org/ontology/":
                ontology = new O_DBpedia();
                break;
            case "http://dbpedia.org/class/yago/":
                ontology = new O_Yago();
                break;
            case "http://schema.org/":
                ontology = new O_Schema();
                break;
            case "http://umbel.org/umbel/rc/":
                ontology = new O_Umbel();
                break;
            case "http://www.wikidata.org/entity/":
                ontology = new O_WikiData();
                break;
            case "http://de.dbpedia.org/resource/":
                ontology = new O_DBpedia_de();
                break;
            case "http://cs.dbpedia.org/resource/":
                ontology = new O_DBpedia_cs();
                break;
            case "http://el.dbpedia.org/resource/":
                ontology = new O_DBpedia_el();
                break;
            case "http://es.dbpedia.org/resource/":
                ontology = new O_DBpedia_es();
                break;
            case "http://eu.dbpedia.org/resource/":
                ontology = new O_DBpedia_eu();
                break;
            case "http://fr.dbpedia.org/resource/":
                ontology = new O_DBpedia_fr();
                break;
            default:
                break;
        }
        return ontology;
    }

    public static List<O> getListOntologyFromPrefixes(List<String> listPrefixes) {
        List<O> ontologies = new ArrayList<>();
        for (String ontologyPrefix : listPrefixes) {
            O ontology = null;
            ontology = Ontology.getOntologyFromNameSpace(ontologyPrefix);
            if (ontology != null) {
                ontologies.add(ontology);
            }
        }
        return ontologies;
    }
    
    public static String getPrefixFromNamespace(R r) {
        return Ontology.getPrefixFromNamespace(r.getNamespace());
    }

    public static String getPrefixFromNamespace(URI uri) {
        return Ontology.getPrefixFromNamespace(uri.getNamespace());
    }

    public static String getPrefixFromNamespace(String uri) {
        prepareIndexes();
        
        if(uri.charAt(uri.length()-1) != '/'){
               return uri;         
        }
                    
        String prefix = prefixIndex.getValue(uri);
        if(prefix != null && prefix.contains(":") ){            
            return prefix;
        }
        
        String p = prefixIndex.generateRandomKey(4);
        
        nameSpaceIndex.addValue(p , uri);
        prefixIndex.addValue(uri, p+":");
        return p+":";

    }

    public static String getNamespaceFromPrefix(String prefix) {
        
        String namespace = nameSpaceIndex.getValue(prefix);
        if(namespace != null){
               return namespace;       
        }
        
        return prefix;
    }
    
    public static void prepareIndexes(){
        manager = LdIndexer.getManager();
        
        try {
        prefixIndex = manager.loadIndex(prefixIndexPath); 
        nameSpaceIndex = manager.loadIndex(namespaceIndexPath);
        } 
        catch (Exception ex) {
                Logger.getLogger(Ontology.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        /*File prefixIndexFile = new File(prefixIndexPath);
        File nameSpaceIndexFile = new File(namespaceIndexPath);
        
        try {
            if(prefixIndexFile.exists()){
                prefixIndex = manager.loadIndex(prefixIndexPath);               
            }
            else {
                prefixIndex = manager.loadIndex(prefixIndexPath);
                updatePrefixIndex();
            }

            if(nameSpaceIndexFile.exists()){
                nameSpaceIndex = manager.loadIndex(namespaceIndexPath);                
            }
            else {
                nameSpaceIndex = manager.loadIndex(namespaceIndexPath);
                updateNameSpaceIndex();
            }
        } 
        catch (Exception ex) {
                Logger.getLogger(Ontology.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    
    public static void updatePrefixIndex(){
               
        prefixIndex.addValue("http://dbpedia.org/resource/", "dbpedia:");
        prefixIndex.addValue("http://dbpedia.org/ontology/", "dbo:");
        prefixIndex.addValue("http://dbpedia.org/property/", "dbp:");
        prefixIndex.addValue("http://en.wikipedia.org/wiki/", "wiki:");
        prefixIndex.addValue("http://xmlns.com/foaf/0.1/", "foaf:");
        prefixIndex.addValue("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:");
        prefixIndex.addValue("http://www.w3.org/ns/rdfa#", "rdfa:");
        prefixIndex.addValue("http://www.openlinksw.com/virtrdf-data-formats#", "rdfdf:");
        prefixIndex.addValue("http://www.w3.org/2000/01/rdf-schema#", "rdfs:");
        prefixIndex.addValue("http://www.w3.org/2002/07/owl#", "dbo:");
        prefixIndex.addValue("http://www.w3.org/XML/1998/namespace", "xml:");
        prefixIndex.addValue("http://www.w3.org/2001/XMLSchema#", "xsd:");
        prefixIndex.addValue("http://purl.org/dc/terms/", "dc:");
        prefixIndex.addValue("http://purl.org/linguistics/gold/", "gold:");
        prefixIndex.addValue("http://www.w3.org/ns/prov#", "prov:");
        prefixIndex.addValue("http://www.w3.org/ns/ldp#", "ldp:");
        prefixIndex.addValue("http://dbpedia.org/class/yago/", "dbo:");
        prefixIndex.addValue("http://www.wikidata.org/entity/", "wikidata:");
        prefixIndex.addValue("http://schema.org/", "schema:");
        prefixIndex.addValue("http://www.w3.org/2006/vcard/ns#", "vcard:");
        prefixIndex.addValue("http://rdf.freebase.com/ns/", "freebase:");                     

    }
    
    public static void updateNameSpaceIndex(){
        
        nameSpaceIndex.addValue("dbpedia", "http://dbpedia.org/resource/");
        nameSpaceIndex.addValue("dbo", "http://dbpedia.org/ontology/");
        nameSpaceIndex.addValue("dbp", "http://dbpedia.org/property/");
        nameSpaceIndex.addValue("wiki", "http://en.wikipedia.org/wiki/");
        nameSpaceIndex.addValue("foaf", "http://xmlns.com/foaf/0.1/");
        nameSpaceIndex.addValue("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        nameSpaceIndex.addValue("rdfa", "http://www.w3.org/ns/rdfa#");
        nameSpaceIndex.addValue("rdfdf", "http://www.openlinksw.com/virtrdf-data-formats#");
        nameSpaceIndex.addValue("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        nameSpaceIndex.addValue("owl", "http://www.w3.org/2002/07/owl#");
        nameSpaceIndex.addValue("xml", "http://www.w3.org/XML/1998/namespace");
        nameSpaceIndex.addValue("xsd", "http://www.w3.org/2001/XMLSchema#");
        nameSpaceIndex.addValue("dc", "http://purl.org/dc/terms/");
        nameSpaceIndex.addValue("gold", "http://purl.org/linguistics/gold/");
        nameSpaceIndex.addValue("prov", "http://www.w3.org/ns/prov#");
        nameSpaceIndex.addValue("ldp", "http://www.w3.org/ns/ldp#");
        nameSpaceIndex.addValue("yago", "http://dbpedia.org/resource/");
        nameSpaceIndex.addValue("wikidata", "http://www.wikidata.org/entity/");
        nameSpaceIndex.addValue("schema", "http://schema.org/");
        nameSpaceIndex.addValue("vcard", "http://www.w3.org/2006/vcard/ns#");
        nameSpaceIndex.addValue("freebase", "http://rdf.freebase.com/ns/");
        
    }
    
       
}
