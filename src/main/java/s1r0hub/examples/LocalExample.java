package s1r0hub.examples;

import java.util.ArrayList;
import java.util.List;

import ldq.LdDataset;
import lds.config.Config;
import lds.config.ConfigParam;
import lds.dataset.LdDatasetCreator;
import lds.engine.LdSimilarityEngine;
import lds.measures.Measure;
import lds.resource.R;

/**
 * Example using a local blazegraph RDF store, loaded with the
 * <a href="https://databus.dbpedia.org/ontologies/purl.obolibrary.org/obo--foodon--owl">Food Ontology</a> from DBpedia.
 * @see <a href="https://github.com/blazegraph/database">Blazegraph on Github</a>
 * @author S1r0hub
 */
public class LocalExample {

	// can take a while and issue many requests if true
	private static final boolean COMPUTE_PICSS = true; 
	
	
	public static void main(String[] args) {
		
		String endpoint = "http://localhost:9999/blazegraph/sparql";
		LdDataset dataset = LdDatasetCreator.getRemoteDataset(endpoint, null, "dataset");

		// create configuration
		Config conf = new Config();
		conf.addParam(ConfigParam.useIndexes, true);
		conf.addParam(ConfigParam.LdDatasetMain, dataset);
		
		// Create separate config for PICSS as Resim wont otherwise correctly compile it.
		Config conf_piccs = new Config();
		conf_piccs.addParam(ConfigParam.useIndexes, true);
		conf_piccs.addParam(ConfigParam.LdDatasetMain, dataset);
		
		// For PICSS: set number of resources in the dataset as described in PICSS paper from 2016
		// (based on subjects that are of type owl:Thing) 
		int resourceCount = 10182; // based on owl:Class
		conf_piccs.addParam(ConfigParam.resourcesCount, resourceCount);
		System.out.println("PICSS using resource count: " + resourceCount);
		
		// setup
		LdSimilarityEngine engine_ldsd = new LdSimilarityEngine();
		LdSimilarityEngine engine_resim = new LdSimilarityEngine();
		LdSimilarityEngine engine_picss = new LdSimilarityEngine();
		engine_ldsd.load(Measure.LDSD_cw, conf);
		engine_resim.load(Measure.Resim, conf);
		engine_picss.load(Measure.PICSS, conf_piccs);

		// entities
		String obo = "http://purl.obolibrary.org/obo/";
		List<EntityPair> entities = new ArrayList<>();
		entities.add(new EntityPair(new R(obo + "FOODON_03411245"), new R(obo + "FOODON_03411529"))); // apple tree & apricot tree
		entities.add(new EntityPair(new R(obo + "FOODON_03411276"), new R(obo + "FOODON_03414363"))); // tomato plant & kiwi
		entities.add(new EntityPair(new R(obo + "FOODON_03412767"), new R(obo + "FOODON_03411339"))); // pineapple guava plant & orange plant
		
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
			if (COMPUTE_PICSS) { picssInfo = String.format(", PICSS = %.4f", p.getRelatedness(Measure.PICSS)); }
			
			System.out.println(String.format("sim(%s, %s): LDSD = %.4f, Resim = %.4f"
				, p.first(), p.second()
				, p.getRelatedness(Measure.LDSD_cw)
				, p.getRelatedness(Measure.Resim)
			) + picssInfo);
		}
		
		System.out.println("\nTime required: " + time + "ms");
	}
}
