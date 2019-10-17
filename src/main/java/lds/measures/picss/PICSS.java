/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lds.measures.picss;


import java.util.List;
import lds.LdManager.PicssLdManager;
import lds.measures.LdSimilarityMeasure;
import lds.resource.R;
import sc.research.ldq.*;
import slib.utils.i.Conf;

/**
 *
 * @author Fouad Komeiha
 */
public class PICSS implements LdSimilarityMeasure{
    private PicssLdManager ldManager;
    private boolean useIndeses;
    private int NumberOfResources;
    
    public PICSS(Conf config) throws Exception{
        if( config.getParam("LdDatasetMain")== null || config.getParam("useIndexes") == null || config.getParam("useIndexes")== null)
            throw new Exception("Some configuration parameters missing"); 
        
        this.ldManager = new PicssLdManager((LdDataset) config.getParam("LdDatasetMain") , (Boolean) config.getParam("useIndexes") );
        this.useIndeses = (Boolean) config.getParam("useIndexes");
        this.NumberOfResources = (Integer) config.getParam("resourcesCount");
    }

    
    @Override
    public void closeIndexes(){
        if(useIndeses){
            ldManager.closeIndexes();
        }
        
    }
    
    
    @Override
    public void loadIndexes() throws Exception{
        if(useIndeses){
            ldManager.loadIndexes();
        }
    }
    
    
    @Override
    public double compare(R a, R b) {
	double sim = 0;

        sim= PICSS(a , b);


        return sim;

    }

    private double PICSS(R a , R b) {

        List<String> features_a = ldManager.getFeatures(a);
        List<String> features_b = ldManager.getFeatures(b);
        
        if(features_a.isEmpty() &&  features_b.isEmpty())
            return 0;
        
        List<String> common_features = Utility.commonFeatures(features_a, features_b);
        
        if(common_features.isEmpty())
            return 0;
        
        List<String> unique_features_a = Utility.uniqueFeatures(features_a , features_b);
	List<String> unique_features_b = Utility.uniqueFeatures(features_b, features_a);

	double x = PIC(common_features);
	double y = PIC(unique_features_a);
	double z = PIC(unique_features_b);

	if ((x + y + z) == 0)
            return 0;
        
	return (x / (x + y + z));
    }
    
    
    private double PIC(List<String> F) {
	double s = 0.0;
//        int N = ldManager.countResource();
//        int N = 9;
//        int N = 2350906;
        
	for (String f : F) {
            
            double phi_ = phi(f);
            if (phi_ != 0) {
                double x = Math.log(phi_ / this.NumberOfResources);
                double log = -x;
                s = s + log;
            }
            
	}
        
	return s;
    }
    
 
    
    private double phi(String feature) {

	int count = 0;
        
        String direction = Utility.getDirection(feature);
        String property = Utility.getLink(feature);
        String resource = Utility.getVertex(feature);
        
        if(direction.equals("In")){
            count = ldManager.getIngoingFeatureFrequency(property , resource);
        }
        
        if(direction.equals("Out")){
            count = ldManager.getOutgoingFeatureFrequency(property , resource);
        }

	return count;

    }
    
    @Override
    public LdSimilarityMeasure getMeasure(){
        return this;
    }
    
    
    
    
}
