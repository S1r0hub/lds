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
import lds.resource.R;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.vocabulary.OWL;
import org.openrdf.model.URI;

/**
 *
 * @author Fouad Komeiha
 */
public class LdManagerBase implements LdManager{

    protected LdDataset dataset;
    protected String baseClassPath = "lds.LdManager.LdManagerBase.";

    public  LdManagerBase(LdDataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public List<String> getSubjects(R a) {

        List<String> commonSubjects = new ArrayList<>();

        ParameterizedSparqlString query_cmd = dataset.prepareQuery();

        query_cmd.setCommandText("select distinct ?subject ?property "
            + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") 
            + " where {?subject ?property <" + a.getUri() + ">}");

        ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

        while (resultSet.hasNext()) {

            QuerySolution qs = resultSet.nextSolution();

            // S1r0hub: catch case that subject is no URI
            // RDF "subject" can be a blank node, see: https://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#section-triples
            if (!qs.getResource("subject").isURIResource()) { continue; }

            String resource = Ontology.compressValue(qs.getResource("subject"));
            String property = Ontology.compressValue(qs.getResource("property"));
            commonSubjects.add(resource+"|"+property);
        }

        if(!commonSubjects.isEmpty()) { return commonSubjects; }
        return null;
    } 


    @Override
    public List<String> getSameAsResources(R a) {

        List<String> sameAsresources = new ArrayList<>();
        
        ParameterizedSparqlString query_cmd = dataset.prepareQuery();

        query_cmd.setCommandText("select distinct ?object " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") 
                                                             + " where { <" + a.getUri() + ">  <" + OWL.sameAs + "> ?object }");

        ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

        while (resultSet.hasNext()) {
            QuerySolution qs = resultSet.nextSolution();
            String resource = Ontology.compressValue(qs.getResource("object"));
            sameAsresources.add(resource);
        }

        if(!sameAsresources.isEmpty()) { return sameAsresources; } 
        return null;
    }
    
    
    @Override
    public int countPropertyOccurrence(URI link){

        int count = 0;
        ParameterizedSparqlString query_cmd = dataset.prepareQuery();

        query_cmd.setCommandText("select (count(?subject) as ?count) " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where { ?subject <" + link + "> ?object. filter(isuri(?object))}");

        ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

        if (resultSet.hasNext()) {
            QuerySolution qs = resultSet.nextSolution();
            count = qs.getLiteral("count").getInt();
            return count;
        }

        return 0;
    }
    
    
    @Override
    public List<String> getCommonObjects(R a , R b){

        List<String> commonObjects = new ArrayList<>();
            
        ParameterizedSparqlString query_cmd = dataset.prepareQuery();

        query_cmd.setCommandText("select distinct ?object  ?property1  ?property2 " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") 
                                                                           + " where {<" + a.getUri() + "> ?property1 ?object . "
                                                                           + "<" + b.getUri() + "> ?property2 ?object."
                                                                           + "filter( isuri(?object) )}");

        ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

        while (resultSet.hasNext()) {
            QuerySolution qs = resultSet.nextSolution();
            String resource = Ontology.compressValue(qs.getResource("object"));
            String property1 = Ontology.compressValue(qs.getResource("property1"));
            String property2 = Ontology.compressValue(qs.getResource("property2"));
            commonObjects.add(resource+"|"+property1+"|"+property2);
        }

        if(!commonObjects.isEmpty()) { return commonObjects; } 
        return null;
    }
    
    @Override
      public List<String> getCommonObjects(R a){

        List<String> commonObjects = new ArrayList<>();
            
        ParameterizedSparqlString query_cmd = dataset.prepareQuery();

        query_cmd.setCommandText("select distinct ?object  ?property1  ?property2 " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") 
                                                                           + " where {<" + a.getUri() + "> ?property1 ?object . "
                                                                           + "[] ?property2 ?object. "
                                                                           + "filter( isuri(?object))}");
        ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

        while (resultSet.hasNext()) {
            QuerySolution qs = resultSet.nextSolution();
            String resource = Ontology.compressValue(qs.getResource("object"));
            String property1 = Ontology.compressValue(qs.getResource("property1"));
            String property2 = Ontology.compressValue(qs.getResource("property2"));
            commonObjects.add(resource+"|"+property1+"|"+property2);
        }

        if(!commonObjects.isEmpty()) { return commonObjects; }
        return null;
    }

    @Override
    public List<String> getCommonSubjects(R a , R b){

        List<String> commonSubjects = new ArrayList<>();
        
        ParameterizedSparqlString query_cmd = dataset.prepareQuery();

        String defaultGraph = "";
        if (dataset.getDefaultGraph() != null && !dataset.getDefaultGraph().isEmpty()) {
            defaultGraph = "from <" + dataset.getDefaultGraph()+ ">";
        }

        query_cmd.setCommandText("select distinct ?subject ?property1 ?property2 " + defaultGraph
                               + " where {?subject ?property1 <" + a.getUri() + ">. "
                               + " ?subject ?property2 <" + b.getUri() + "> }");

        ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

        while (resultSet.hasNext()) {
            QuerySolution qs = resultSet.nextSolution();
            // Skip bnodes as they always have different identifier (S1r0hub)
            // TODO: Is it ok to skip anonymous nodes? If not change this.
            if (qs.getResource("subject").isAnon()) { continue; }
            String resource = Ontology.compressValue(qs.getResource("subject"));
            String property1 = Ontology.compressValue(qs.getResource("property1"));
            String property2 = Ontology.compressValue(qs.getResource("property2"));
            commonSubjects.add(resource+"|"+property1+"|"+property2);
        }

        if(!commonSubjects.isEmpty()) { return commonSubjects; }
        return null;
    }
    
    @Override
    public List<String> getCommonSubjects(R a){

        List<String> commonSubjects = new ArrayList<>();
        
        ParameterizedSparqlString query_cmd = dataset.prepareQuery();

        query_cmd.setCommandText("select distinct ?subject  ?property1  ?property2 " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") 
                                                                           + " where {?subject ?property1 <" + a.getUri() + ">. "
                                                                           + " ?subject ?property2 []}");

        ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

        while (resultSet.hasNext()) {
            QuerySolution qs = resultSet.nextSolution();
            String resource = Ontology.compressValue(qs.getResource("subject"));
            String property1 = Ontology.compressValue(qs.getResource("property1"));
            String property2 = Ontology.compressValue(qs.getResource("property2"));
            commonSubjects.add(resource+"|"+property1+"|"+property2);
        }

        if(!commonSubjects.isEmpty()) { return commonSubjects; }
        return null;
    }
    
    @Override
    public List<String> getEdges(R a) { 

        List<String> edges = new ArrayList<>();
        
        String edge;

        ParameterizedSparqlString query_cmd = dataset.prepareQuery();

        // added by S1r0hub to better allow queries without "from", even if graph name is an empty string
        String from_part = "";
        if (dataset.getDefaultGraph() != null && !dataset.getDefaultGraph().isEmpty()) {
        	from_part = "from <" + dataset.getDefaultGraph()+ "> \n";
        }

        query_cmd.setCommandText("select distinct ?property \n"
                                //+ (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ "> \n")
                                + from_part
                                + "where { \n"
                                + "{ \n"
                                + "select distinct ?property \n"
                                + "where {<" + a.getUri() + "> ?property ?object. \n" 
                                + "filter(isuri(?object)) } \n"
                                + "} \n"
                                + "union \n"
                                + "{ \n"
                                + "select distinct ?property \n"
                                + "where {[] ?property <" + a.getUri() + ">} \n"
                                + "} \n"
                                + "}");            

        ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

        while (resultSet.hasNext()) {
            QuerySolution qs = resultSet.nextSolution();
            edge = Ontology.compressValue(qs.getResource("property"));
            edges.add(edge);
        }

      if(!edges.isEmpty()) { return edges; }
      return null;
    }

    
    @Override
    public List<String> getObjects(R a){

        List<String> objects =  new ArrayList<>();
        String object, property;

        ParameterizedSparqlString query_cmd = dataset.prepareQuery();

        query_cmd.setCommandText("select distinct ?object ?property " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where {<" + a.getUri() + "> ?property ?object ."
                + " filter(isuri(?object)) }");

        ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

        while (resultSet.hasNext()) {
            QuerySolution qs = resultSet.nextSolution();
            object = Ontology.compressValue(qs.getResource("object"));
            property = Ontology.compressValue(qs.getResource("property"));
            objects.add(object+"|"+property);
        }

        if(!objects.isEmpty()) { return objects; }
        return null;
    }
    
    @Override
    public int countShareCommonObjects(URI link , R a){
        int count = 0;
        ParameterizedSparqlString query_cmd = dataset.prepareQuery();

        query_cmd.setCommandText("select (count(distinct ?subject) as ?count) " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where {<" + a.getUri() + "> <" + link + "> ?object . "
                                                                                   + "?subject <" + link + "> ?object ."
                                                                                 + " filter(?subject != <" + a.getUri() + ">)}");
        

        ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

        if (resultSet.hasNext()) {
                QuerySolution qs = resultSet.nextSolution();
                count = qs.getLiteral("count").getInt();
                return count;
        }

        return 0;
    }

    @Override
    public int countShareCommonSubjects(URI link , R a){

        int count = 0;
        ParameterizedSparqlString query_cmd = dataset.prepareQuery();

        query_cmd.setCommandText("select (count(distinct ?object) as ?count) " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where { ?subject <" + link + "> <" + a.getUri() + ">. "
                                                                                   + "?subject <" + link + "> ?object."
                                                                           + "filter(?object != <" + a.getUri() + "> && isuri(?object) ) }");


        ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

        if (resultSet.hasNext()) {
                QuerySolution qs = resultSet.nextSolution();
                count = qs.getLiteral("count").getInt();
                return count;
        }

        return 0;
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public  int countResource() {

        int count = 0;
        ParameterizedSparqlString query_cmd = dataset.prepareQuery();

        query_cmd.setCommandText("select (count(distinct ?s) as ?count) " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") 
                                   + " WHERE\n" +
                                "  {\n" +
                                "    ?s ?p ?o .\n" +
                                "    FILTER ( REGEX (STR (?s), \"resource\" ) )\n" +
                               "  }");
        
        
        ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

        if (resultSet.hasNext()) {
                QuerySolution qs = resultSet.nextSolution();
                count = qs.getLiteral("count").getInt();
                return count;
        }

        return 0;
    }

}