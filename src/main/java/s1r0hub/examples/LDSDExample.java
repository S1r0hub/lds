package s1r0hub.examples;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.ResultSet;

import ldq.LdDataset;
import lds.config.Config;
import lds.config.ConfigParam;
import lds.dataset.LdDatasetCreator;
import lds.engine.LdSimilarityEngine;
import lds.measures.Measure;
import lds.resource.R;

/**
 * Example using <a href="https://github.com/FouadKom/lds">LDS-Library</a>.
 * @see <a href="https://github.com/FouadKom/lds/blob/master/doc/General_Explanation_of_the_Library.md">General Explanation of LDS Library</a>
 * @see <a href="https://github.com/FouadKom/lds/blob/master/doc/Similarity_Calculation_using_LDS.md">Library usage Example</a>
 * @author S1r0hub
 */
public class LDSDExample {
	
	// probably wont event work on full DBpedia as entity pair 4 has 12 common features
	// and needs to compute 9993 unique features for just a (which results in that many requests if not yet in index)
	private static final boolean COMPUTE_PICSS = false; // can take a while and issue many requests if true
	
	// Application Entry Point
	public static void main(String[] args) {
		
		String endpoint = "http://dbpedia.org/sparql";
		LdDataset dbpedia = LdDatasetCreator.getRemoteDataset(endpoint, null, "dbpedia");
		
		// create configuration
		Config conf = new Config();
		conf.addParam(ConfigParam.useIndexes, true);
		conf.addParam(ConfigParam.LdDatasetMain, dbpedia);
		
		// Create separate config for PICSS as Resim wont otherwise correctly compile it.
		Config conf_piccs = new Config();
		conf_piccs.addParam(ConfigParam.useIndexes, true);
		conf_piccs.addParam(ConfigParam.LdDatasetMain, dbpedia);
		
		// For PICSS: set number of resources in the dataset as described in PICSS paper from 2016
		int dbpResourceCount = getDBpediaResourceCount(dbpedia);
		conf_piccs.addParam(ConfigParam.resourcesCount, dbpResourceCount);
		System.out.println("PICSS using resource count: " + dbpResourceCount);
		
		// setup
		LdSimilarityEngine engine_ldsd = new LdSimilarityEngine();
		LdSimilarityEngine engine_resim = new LdSimilarityEngine();
		LdSimilarityEngine engine_picss = new LdSimilarityEngine();
		engine_ldsd.load(Measure.LDSD_cw, conf);
		engine_resim.load(Measure.Resim, conf);
		engine_picss.load(Measure.PICSS, conf_piccs);
		
		// entities
		String dbr = "http://dbpedia.org/resource/";
		String dbo = "http://dbpedia.org/ontology/";
		List<EntityPair> entities = new ArrayList<>();
		entities.add(new EntityPair(new R(dbo + "Bird"), new R(dbr + "Dog")));
		entities.add(new EntityPair(new R(dbo + "Bird"), new R(dbr + "House_sparrow")));
		entities.add(new EntityPair(new R(dbo + "Bird"), new R(dbr + "Cat")));
		entities.add(new EntityPair(new R(dbo + "Bird"), new R(dbr + "Car")));
		entities.add(new EntityPair(new R(dbo + "Bird"), new R(dbo + "Animal")));
		entities.add(new EntityPair(new R(dbo + "Mammal"), new R(dbo + "Animal")));
		entities.add(new EntityPair(new R(dbo + "Bird"), new R(dbo + "Fish")));
		entities.add(new EntityPair(new R(dbo + "Town"), new R(dbo + "City")));
		
		// compute relatedness
		System.out.println("Computing relatedness..");
		int pno = 0;
		long t1 = System.currentTimeMillis();
		
		for (EntityPair p : entities) {
			
			System.out.println("... pair " + (pno++));
			double sim_LDSD = engine_ldsd.similarity(p.first(), p.second());
			p.setRelatedness(Measure.LDSD_cw, sim_LDSD);
			System.out.println("    ... LDSD done: " + sim_LDSD);
			
			double sim_Resim = engine_resim.similarity(p.first(), p.second());
			p.setRelatedness(Measure.Resim, sim_Resim);
			System.out.println("    ... Resim done: " + sim_Resim);
			
			if (COMPUTE_PICSS) {
				double sim_PICSS = engine_picss.similarity(p.first(), p.second());
				p.setRelatedness(Measure.PICSS, sim_PICSS);
				System.out.println("    ... PICSS done: " + sim_PICSS);
			}
		}
		
		long time = System.currentTimeMillis() - t1; 
		System.out.println();
		
		// print results
		for (EntityPair p : entities) {
			
			String picssInfo = "";
			if (COMPUTE_PICSS) { String.format(", PICSS = %.4f", p.getRelatedness(Measure.PICSS)); }
			
			System.out.println(String.format("sim(%s, %s): LDSD = %.4f, Resim = %.4f"
				, p.first(), p.second()
				, p.getRelatedness(Measure.LDSD_cw)
				, p.getRelatedness(Measure.Resim)
			) + picssInfo);
		}
		
		System.out.println("\nTime required: " + time + "ms");
	}
	
	/**
	 * Try to retrieve number of resources from DBpedia or use hardcoded one (5275003).<br/>
	 * This number is based on subjects that are of type owl:Thing (see PICSS paper from 2016). 
	 * @param dbpedia The dataset to use (must use a DBpedia sparql endpoint to work properly)
	 */
	private static int getDBpediaResourceCount(LdDataset dbpedia) {
		
		int dbpResourceCount = 5275003; // count as of 31.01.2022
		
		String queryResourceCount = "SELECT (COUNT(?s) as ?N) WHERE { ?s a <http://www.w3.org/2002/07/owl#Thing> }";
		
		try {
			//QueryExecutionFactory.sparqlService(endpoint, queryResourceCount).execSelect(); ...
			ResultSet r = dbpedia.executeSelectQuery(queryResourceCount);
			if (r.hasNext()) {
				int c = r.nextSolution().get("N").asLiteral().getInt();
				if (c == 0) {
					System.out.println("Got DBpedia resource count but it is zero - ignoring it and using hardcoded value.");
				}
				else if (c < dbpResourceCount) {
					System.out.println("Got DBpedia resource count but it is smaller than the hardcoded: " + c);
					dbpResourceCount = c;
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Failed to retrieve current resource count from DBpedia. Using hardcoded one!");
		}
		
		return dbpResourceCount;
	}
}
