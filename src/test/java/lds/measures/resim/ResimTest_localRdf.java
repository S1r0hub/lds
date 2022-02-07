package lds.measures.resim;

import lds.LdManager.ResimLdManager;
import lds.config.Config;
import lds.config.ConfigParam;
import lds.resource.R;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import ldq.LdDataset;
import ldq.LdDatasetFactory;

/**
 * @author Fouad Komeiha
 */
public class ResimTest_localRdf {
	public static final String dataSetDir = System.getProperty("user.dir") + "/src/test/resources/data.rdf";
	public static ResimLdManager resimLdManager;

	@Test
	public void isResimWorksCorrectlyOnPaperExample() throws Exception{

		LdDataset dataSet = null;

		try {
			// FIX by S1r0hub: the following caused the test to fail, as there is no default graph name in the data!
			dataSet = LdDatasetFactory.getInstance()
				.name("example").file(dataSetDir)
				//.defaultGraph("http://graphResim/dataset")
				.defaultGraph(null) // FIXED
				.create();

		} catch (Exception e) {
			fail(e.getMessage());
		}

		Config config = new Config();
		config.addParam(ConfigParam.useIndexes, false);
		config.addParam(ConfigParam.LdDatasetMain , dataSet);

		ResourceSimilarity resim = new Resim(config);
		resim.loadIndexes();

		double pptySim, ldsdsim, sim;

		R r1 = new R("http://www.example.org#Fish");
		R r2 = new R("http://www.example.org#Whale");

		// result = (7/60 + 1.5) / 3 = 0.5388...
		sim = resim.compare(r1, r2 , 1, 2);
		assertEquals(0.5388888888888889, sim, 0.0);

		// result = 7/60 = 0.1166...
		pptySim = resim.PropertySim(r1, r2);
		assertEquals(0.11666666666666665, pptySim, 0.0);

		ldsdsim = resim.LDSDsim(r1, r2);
		assertEquals(0.75, ldsdsim, 0.0);

		sim = resim.compare(r2, r1 , 1 , 2);
		assertEquals(0.5388888888888889, sim, 0.0);

		sim = resim.compare(r1, r1);
		assertEquals(1.0, sim, 0.0);

		resim.closeIndexes();
	}
}
