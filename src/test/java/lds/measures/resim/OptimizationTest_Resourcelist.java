/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lds.measures.resim;

import lds.engine.LdSimilarityEngine;
import lds.measures.Measure;
import org.junit.Test;
import sc.research.ldq.LdDataset;
import slib.utils.i.Conf;
import test.utility.Util;

/**
 *
 * @author Fouad Komeiha
 */
public class OptimizationTest_Resourcelist {
    public static final String resourcesFilePath = System.getProperty("user.dir") + "/src/test/resources/OptimTest/facebook_book_resources_Resim.txt";
    
    @Test
    public void OptimizationTest() throws Exception{ 
        
        double startTime , endTime , duration;
        
        LdDataset datasetMain = Util.getDBpediaDataset();
        
        Conf configSim = new Conf();            
        
        configSim.addParam("LdDatasetMain" , datasetMain);
        configSim.addParam("useIndexes" , false);
        
        LdSimilarityEngine engine = new LdSimilarityEngine();

        engine.load(Measure.ResimO  ,configSim);
        
        startTime = System.nanoTime(); 
        
        engine.similarity(resourcesFilePath , false , false);
        
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000 ;
        System.out.println("ResimO finished in " + duration + " second(s)");
        System.out.println();
        
        engine.close();
        
        engine.load(Measure.Resim  ,configSim);
        
        startTime = System.nanoTime(); 
        
        engine.similarity(resourcesFilePath , false , false);
        
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1000000000 ;
        System.out.println("Resim finished in " + duration + " second(s)");
        System.out.println();
        
        engine.close();
    }
}