/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lds.measures.picss;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import lds.resource.R;
import static org.junit.Assert.fail;
import sc.research.ldq.LdDataset;
import sc.research.ldq.LdDatasetFactory;
import slib.utils.i.Conf;

/**
 *
 * @author Fouad Komeiha
 */
public class PICSSTest_localRdf {
    public static final String dataSetDir = System.getProperty("user.dir") + "/src/test/resources/data.rdf";

	@Test
	public void isPICSSWorksCorrectlyOnPaperExample() throws Exception {
		LdDataset dataSet = null;
		

		try {
			dataSet = LdDatasetFactory.getInstance().name("example").file(dataSetDir).create();

		} catch (Exception e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
                
                 R r1 = new R("http://www.example.org#Fish");
                 R r2 = new R("http://www.example.org#Whale");
                
                Conf config = new Conf();
                config.addParam("useIndexes", true);
                config.addParam("LdDatasetMain" , dataSet);
                config.addParam("resourcesCount" , 9);
                

		PICSS picss = new PICSS(config);
                picss.loadIndexes();



		double comp;               
                
                comp = picss.compare(r1, r2);
		assertEquals(0.11663433805905217, comp, 0.0);

		comp = picss.compare(r2, r1);
		assertEquals(0.11663433805905217, comp, 0.0);

		comp = picss.compare(r1, r1);
		assertEquals(1.0, comp, 0.0);
               
                picss.closeIndexes();
                
                

	}
}
