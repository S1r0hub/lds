package lds.LdManager;

import static java.lang.Double.NaN;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.HttpException;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.vocabulary.OWL;
import org.openrdf.model.URI;

import lds.graph.GraphManager;
import lds.graph.LdGraphManager;
import lds.resource.LdResourceFactory;
import lds.resource.R;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Resource;
import sc.research.ldq.LdDataset;
import slib.graph.model.graph.G;
import slib.graph.model.impl.repo.URIFactoryMemory;
import slib.graph.model.repo.URIFactory;
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.i.Conf;

public class LdManagerBase implements LdManager {

	protected static LdDataset dataset;
	protected Conf config = null;

	public LdManagerBase(LdDataset dataset) {
		this.dataset = dataset;
	}

	public LdManagerBase(LdDataset dataset, Conf config) {
		this.dataset = dataset;
		this.config = config;
                
	}

	public G generateGraph(LdDataset dataset, R a, R b, String graphURI) throws HttpException, SLIB_Ex_Critic {

		// TODO: We have to store the graph locally, to avoid quering everytime ?

		G graph = new GraphManager().generateGraph(graphURI);

		LdGraphManager.getInOutResources(graph, a, 2, dataset);
		LdGraphManager.getInOutResources(graph, b, 2, dataset);

		return graph;
	}
        


	public List<String> getSameAsResoures(R a) {

		ParameterizedSparqlString query_cmd = dataset.prepareQuery();

		query_cmd.setCommandText("select ?sameAs " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where { " + a.getTurtle() + " <" + OWL.sameAs + "> ?sameAs. }");

//		System.out.println("query = " + query_cmd.toString());

		ResultSet rs = dataset.executeSelectQuery(query_cmd.toString());
		List<String> sameAsResources = new ArrayList<String>();

		for (; rs.hasNext();) {
			QuerySolution qs = rs.nextSolution();
			String sameAsResource = qs.getResource("sameAs").getURI();
			sameAsResources.add(sameAsResource);
		}
                
                
                dataset.close();
		return sameAsResources;

	}

	public static int countPropertyOccurrence(URI link) {
		Literal count = null;
		ParameterizedSparqlString query_cmd = dataset.prepareQuery();

		query_cmd.setCommandText("select (count(?subject) as ?count) " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where { ?subject <" + link + "> ?object }");

		// logger.info("query = " + query_cmd.toString());

		ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

		if (resultSet.hasNext()) {
			QuerySolution qs = resultSet.nextSolution();
			count = (Literal) qs.getLiteral("count");
                        dataset.close();
                        return Integer.parseInt(count.toString().substring(0, count.toString().indexOf("^^")));

		}
                
                dataset.close();
                return 0;
                
	}
        
        public List<String> listShareCommonSubject(URI link , R a){
            List<String> shareSubjectwithA = new ArrayList();
             ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("select distinct ?object " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where { ?subject <" + link + "> <" + a.getUri() + ">. "
                                                                   + "?subject <" + link + "> ?object ."
                                                                   + "filter(?object != <" + a.getUri() + ">)}");

            ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

            while (resultSet.hasNext()) {
                QuerySolution qs = resultSet.nextSolution();
                String resource = qs.getResource("object").getURI();
                shareSubjectwithA.add(resource);
            }
            
            dataset.close();
            return shareSubjectwithA;
        }
        
       
         public List<String> listShareCommonObject(URI link , R a){
            List<String> shareObjectwithA = new ArrayList();
             ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("select distinct ?subject " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where {<" + a.getUri() + "> <" + link + "> ?object . "
                                                                               + "?subject <" + link + "> ?object ."
                                                                               + "filter(?subject != <" + a.getUri() + ">)}");

            ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

            while (resultSet.hasNext()) {
                QuerySolution qs = resultSet.nextSolution();
                String resource = qs.getResource("subject").getURI();
                shareObjectwithA.add(resource);

            }
//            
//            if(shareObjectwithA == null)
//                shareObjectwithA.add("Nothing");
            dataset.close();
            return shareObjectwithA;
        }
         
        public List<String> getOutgoingEdges(R a){
            
            List<String> directlyConnectedObjects = new ArrayList<String>();
            
            ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("select distinct ?object " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where {<" + a.getUri() + "> ?property ?object ."
                    + " filter(isuri(?object)) }");

            ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

            while (resultSet.hasNext()) {
                QuerySolution qs = resultSet.nextSolution();
//                count = (Literal) qs.getLiteral("count");
                directlyConnectedObjects.add(qs.getResource("object").getURI());

            }
            
            dataset.close();
            return directlyConnectedObjects;
        }
       
        
        public List<String> getIngoingEdges(R a){
            
            List<String> directlyConnectedSubjects = new ArrayList<>();
            
            ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("select distinct ?subject " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where {?subject ?property <" + a.getUri() + "> ."
                    + "filter(isuri(?subject))}");

            ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

            while (resultSet.hasNext()) {
                QuerySolution qs = resultSet.nextSolution();
                directlyConnectedSubjects.add(qs.getResource("subject").getURI());

            }
            
            dataset.close();
            return directlyConnectedSubjects;
        }
               

        public List<String> getIngoingEdges(URI link, R a) {
            List<String> directlyConnectedSubjects = new ArrayList<>();
            ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("select distinct ?subject " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where {?subject <" + link + "> <" + a.getUri() + "> .}");

            ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

            while (resultSet.hasNext()) {
                QuerySolution qs = resultSet.nextSolution();
//                count = (Literal) qs.getLiteral("count");
                directlyConnectedSubjects.add(qs.getResource("subject").getURI());

            }
            
            dataset.close();
            return directlyConnectedSubjects;
        }
        
        
        public List<String> getOutgoingEdges(URI link , R a) {
            List<String> directlyConnectedObjects = new ArrayList<>();
            ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("select distinct ?object " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where {<" + a.getUri() + "> <" + link + "> ?object ."
                    + " filter(isuri(?object)) }");

            ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

            while (resultSet.hasNext()) {
                QuerySolution qs = resultSet.nextSolution();
//                count = (Literal) qs.getLiteral("count");
                directlyConnectedObjects.add(qs.getResource("object").getURI());

            }
            
            dataset.close();
            return directlyConnectedObjects;
        }
        
                
        @Override
        public boolean isSameAs(R a, R b) {
		// TODO Auto-generated method stub
		return false;
	}

     

        @Override
        public boolean isDirectlyConnected(URI link, R a, R b) {
            ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("ask {<" + a.getUri() + ">  <" + link + "> <" + b.getUri() + "> . }");
            
            boolean result = dataset.executeAskQuery(query_cmd.toString());
            
            
            return result;
        }

        @Override
        public boolean shareCommonObject(URI link, R a, R b) {
            ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("ask {<" + a.getUri() + "> <" + link + "> ?object . "
                                        + "<" + b.getUri() + "> <" + link + "> ?object }");

            boolean result = dataset.executeAskQuery(query_cmd.toString());
            
            dataset.close();
            return result;
        }

        @Override
        public boolean shareCommonSubject(URI link, R a, R b) {
            ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("ask {?subject <" + link + "> <" + a.getUri() + "> . "
                                        + "?subject <" + link + "> <" + b.getUri() + "> }");

            boolean result = dataset.executeAskQuery(query_cmd.toString());
            
            dataset.close();
            return result;
        }
        
        
        @Override
        public int countIngoingEdges(URI link, R a) {
            
            Literal count = null;
            
            ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("select (count(distinct ?subject) as ?count) " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where {?subject <" + link + "> <" + a.getUri() + "> ."
                    + "filter(isuri(?subject))}");

            ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

            if (resultSet.hasNext()) {
			QuerySolution qs = resultSet.nextSolution();
			count = (Literal) qs.getLiteral("count");
                        dataset.close();
                        return Integer.parseInt(count.toString().substring(0, count.toString().indexOf("^^")));

		}
                
                dataset.close();
                return 0;
        }
        
        @Override
        public int countIngoingEdges(R a){
            
            Literal count = null;
            
            ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("select (count(distinct ?subject) as ?count) " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where {?subject ?property <" + a.getUri() + ">. }");

            ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

            if (resultSet.hasNext()) {
			QuerySolution qs = resultSet.nextSolution();
			count = (Literal) qs.getLiteral("count");
                        dataset.close();
                        return Integer.parseInt(count.toString().substring(0, count.toString().indexOf("^^")));

		}
                
                dataset.close();
                return 0;
            
        }
        
        @Override
       public int countOutgoingEdges(R a){
            
            Literal count = null;
            
            ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("select (count(distinct ?object) as ?count) " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where {<" + a.getUri() + "> ?property ?object ."
                    + " filter(isuri(?object)) }");

            ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

            if (resultSet.hasNext()) {
			QuerySolution qs = resultSet.nextSolution();
			count = (Literal) qs.getLiteral("count");
                        dataset.close();
                        return Integer.parseInt(count.toString().substring(0, count.toString().indexOf("^^")));

		}
                
                dataset.close();
                return 0;
            
        }
       
       
        @Override
      public int countOutgoingEdges(URI link , R a) {
            Literal count = null;
            
            ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("select (count(distinct ?object) as ?count) " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where {<" + a.getUri() + "> <" + link + "> ?object."
                    + " filter(isuri(?object)) }");

            ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

            if (resultSet.hasNext()) {
			QuerySolution qs = resultSet.nextSolution();
			count = (Literal) qs.getLiteral("count");
                        dataset.close();
                        return Integer.parseInt(count.toString().substring(0, count.toString().indexOf("^^")));

		}
                
                dataset.close();
                return 0;
        }      
   

        @Override
        public int countShareCommonObjects(URI link, R a) {
            Literal count = null;
            ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("select (count(distinct ?subject) as ?count) " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where {<" + a.getUri() + "> <" + link + "> ?object . "
                                                                                       + "?subject <" + link + "> ?object ."
                                                                                     + "filter(?subject != <" + a.getUri() + ">)}");

            ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

            if (resultSet.hasNext()) {
			QuerySolution qs = resultSet.nextSolution();
			count = (Literal) qs.getLiteral("count");
                        dataset.close();
                        return Integer.parseInt(count.toString().substring(0, count.toString().indexOf("^^")));

		}
                
                dataset.close();
                return 0;
            
        }
        

        @Override
        public int countShareCommonSubjects(URI link, R a) {
            Literal count = null;
            ParameterizedSparqlString query_cmd = dataset.prepareQuery();

            query_cmd.setCommandText("select (count(distinct ?object) as ?count) " + (dataset.getDefaultGraph() == null ? ("") : "from <" + dataset.getDefaultGraph()+ ">") + " where { ?subject <" + link + "> <" + a.getUri() + ">. "
                                                                                       + "?subject <" + link + "> ?object."
                                                                               + "filter(?object != <" + a.getUri() + ">)}");

            ResultSet resultSet = dataset.executeSelectQuery(query_cmd.toString());

            if (resultSet.hasNext()) {
			QuerySolution qs = resultSet.nextSolution();
			count = (Literal) qs.getLiteral("count");
                        dataset.close();
                        return Integer.parseInt(count.toString().substring(0, count.toString().indexOf("^^")));

		}
                
                dataset.close();
                return 0;
        }
        



}
