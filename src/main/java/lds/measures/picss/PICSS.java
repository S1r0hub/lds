/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lds.measures.picss;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lds.LdManager.PicssLdManager;
import lds.LdManager.ontologies.Ontology;
import lds.config.Config;
import lds.config.ConfigParam;
import lds.feature.Feature;
import lds.resource.R;
import ldq.*;
import lds.measures.LdSimilarity;
import lds.utility.Utility;

/**
 * @author Fouad Komeiha
 */
public class PICSS implements LdSimilarity {
    protected PicssLdManager ldManager;
    protected boolean useIndexes;
    protected int numberOfResources;

    public PICSS(Config config) throws Exception {
        if (config.getParam(ConfigParam.LdDatasetMain) == null || config.getParam(ConfigParam.useIndexes) == null || config.getParam(ConfigParam.resourcesCount) == null)
            throw new Exception("Some configuration parameters are missing (probably resourcesCount?)");

        this.ldManager = new PicssLdManager((LdDataset) config.getParam(ConfigParam.LdDatasetMain), (Boolean) config.getParam(ConfigParam.useIndexes));
        this.useIndexes = (Boolean) config.getParam(ConfigParam.useIndexes);
        this.numberOfResources = (Integer) config.getParam(ConfigParam.resourcesCount);
    }

    @Override
    public void closeIndexes() {
        //load prefixes and namespaces index
        Ontology.closeIndexes();
        if (useIndexes) {
            ldManager.closeIndexes();
        }
    }


    @Override
    public void loadIndexes() throws Exception {
        if (useIndexes) {
            ldManager.loadIndexes();
        }
        Ontology.loadIndexes();
    }


    @Override
    public double compare(R a, R b) {
        try{
            double sim = 0;
            sim = PICSS(a, b);
            return sim;
        } catch (Exception ex) {
                Logger.getLogger(PICSS.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
        }
    }

    private double PICSS(R a, R b) {

        List<String> features_a = ldManager.getFeatures(a);
        List<String> features_b = ldManager.getFeatures(b);

        if (features_a.isEmpty() && features_b.isEmpty()) { return 0; }

        List<String> common_features = Feature.commonFeatures(features_a, features_b);
        if (common_features == null || common_features.isEmpty()) { return 0; }

        features_a.removeAll(common_features);
        features_b.removeAll(common_features);

        List<String> unique_features_a = Feature.uniqueFeatures(features_a, features_b);
        List<String> unique_features_b = Feature.uniqueFeatures(features_b, features_a);

        // unique features can be a lot, causing lots of sparql queries!
        double x = PIC(common_features);   // PIC(Fa intersection Fb)
        double y = PIC(unique_features_a); // PIC(Fa - Fb)
        double z = PIC(unique_features_b); // PIC(Fb - Fa)

        if ((x + y + z) == 0) { return 0; }

        double sim = (x / (x + y + z)) < 0 ? 0 : (x / (x + y + z));
        return sim;
    }


    /**
     * Compute sum of information content of the given features.
     * @param F a list of features encoded as String
     */
    protected double PIC(List<String> F) {

        double s = 0.0;

        for (String f : F) {
            double phi_ = phi(f);
            if (phi_ != 0) {
                // Compute log2 of relative frequency of a feature f
                double x = Utility.log2(phi_ / this.numberOfResources);
                double log = -x;
                s = s + log;
            }
        }

        return s;
    }


    /**
     * Returns the frequency of this feature (can be 0).<br/>
     * E.g. if we initially had (Movie1 -- starring --> Actor1)
     * and want features of Actor1, we will get as Feature F1: (starring, Movie1, Ingoing).<br/>
     * The frequency of F1 is the amount of triples, that have Movie1 as subject,
     * starring as predicate and any resource (not Literals) as object.<br/>
     * I.e. the amount of actors starring in the movie.<br/>
     * For outgoing direction of the relation, this is the other way around.
     * @param feature The feature encoded as a String: "relationType|resource|direction".
     */
    protected double phi(String feature) {

        String direction = Feature.getDirection(feature);
        if (direction == null) { return 0; }

        String property = Feature.getLink(feature);
        if (property == null) { return 0; }

        String resource = Feature.getVertex(feature);
        if (resource == null) { return 0; }

        if (direction.equals("In")) {
            return ldManager.getIngoingFeatureFrequency(property, resource);
        }

        if (direction.equals("Out")) {
            return ldManager.getOutgoingFeatureFrequency(property, resource);
        }

        return 0;
    }

    @Override
    public LdSimilarity getMeasure() {
        return this;
    }


}
