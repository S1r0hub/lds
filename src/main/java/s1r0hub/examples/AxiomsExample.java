package s1r0hub.examples;

import ldq.LdDataset;
import lds.config.Config;
import lds.config.ConfigParam;
import lds.dataset.LdDatasetCreator;
import lds.engine.LdSimilarityEngine;
import lds.measures.Measure;
import lds.resource.R;

/**
 * Example for some computations showing the violation
 * of fundamental axioms as presented by Resim paper:
 * <i>Guangyuan Piao. Computing the Semantic Similarity of Resources in DBpedia for Recommendation Purposes (2015)</i>
 * @author S1r0hub
 */
public class AxiomsExample {

	public static void main(String[] args) {

		String endpoint = "http://localhost:9999/blazegraph/sparql";
		LdDataset dataset = LdDatasetCreator.getRemoteDataset(endpoint, null, "dataset");

		// create configuration
		Config conf = new Config();
		conf.addParam(ConfigParam.useIndexes, false);
		conf.addParam(ConfigParam.LdDatasetMain, dataset);

		// setup engines
		LdSimilarityEngine engine_ldsd = new LdSimilarityEngine();
		LdSimilarityEngine engine_resim = new LdSimilarityEngine();
		engine_ldsd.load(Measure.LDSD_cw, conf);
		engine_resim.load(Measure.Resim, conf);

		// define entities
		String obo = "http://purl.obolibrary.org/obo/";
		R a = new R(obo + "FOODON_03411245");
		R b = new R(obo + "FOODON_03414363");
		EntityPair aa = new EntityPair(a, a); // apple tree & apple tree
		EntityPair ab = new EntityPair(a, b); // apple tree & kiwi
		EntityPair ba = new EntityPair(b, a); // kiwi & apple tree
		EntityPair bb = new EntityPair(b, b); // kiwi & kiwi

		// Show reliability of results based on axioms (see Resim paper):
		// 1) sim(a,a) = 1 and sim(a,a) = sim(b,b) => equal self-similarity
		// 2) sim(a,b) = sim(b,a) => symmetry
		// 3) sim(a,a) > sim(a,b) => minimality
		// => 1) and 2) are not always true for LDSD
		// => All 3 should hold true for Resim

		double sim_LDSD = 0, sim_Resim = 0;
		sim_LDSD = engine_ldsd.similarity(aa.first(), aa.second());
		sim_Resim = engine_resim.similarity(aa.first(), aa.second());
		System.out.println(String.format("sim(a,a): LDSD=%f, Resim=%f", sim_LDSD, sim_Resim));
		
		sim_LDSD = engine_ldsd.similarity(bb.first(), bb.second());
		sim_Resim = engine_resim.similarity(bb.first(), bb.second());
		System.out.println(String.format("sim(b,b): LDSD=%f, Resim=%f", sim_LDSD, sim_Resim));
		
		sim_LDSD = engine_ldsd.similarity(ab.first(), ab.second());
		sim_Resim = engine_resim.similarity(ab.first(), ab.second());
		System.out.println(String.format("sim(a,b): LDSD=%f, Resim=%f", sim_LDSD, sim_Resim));
		
		sim_LDSD = engine_ldsd.similarity(ba.first(), ba.second());
		sim_Resim = engine_resim.similarity(ba.first(), ba.second());
		System.out.println(String.format("sim(b,a): LDSD=%f, Resim=%f", sim_LDSD, sim_Resim));
		
		// Result:
		// sim(a,a): LDSD=0,845437, Resim=1,000000
		// sim(b,b): LDSD=0,521786, Resim=1,000000
		// sim(a,b): LDSD=0,166451, Resim=0,083261
		// sim(b,a): LDSD=0,166451, Resim=0,083261
		// => One can see that axiom 1) is clearly violated by LDSD.
		// => We could not show a violation of 2) here but one can see it in the LDSD equation.
	}
}
