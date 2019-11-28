/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lds.resource;

/**
 *
 * @author Fouad Komeiha
 */
public class LdResourceTriple {
    
    private LdResourcePair pair;
    private double result;
    
    public LdResourceTriple(LdResourcePair pair , double result){
        this.pair = pair;
        this.result = result;
    }
    
    public LdResourceTriple(R firstResource , R secondResource , double result){
        this.pair = new LdResourcePair(firstResource , secondResource);
        this.result = result;
    }
    
    public LdResourcePair getResourcePair(){
        return this.pair;
    }
    
    public double getSimilarityResult(){
        return this.result;
    }
    
    public void setSimilarityResult(double result){
        this.result = result;
    }
    
    @Override
    public String toString(){
       return pair.toString() + " , " + result;
    }
    
    public String toString(char separator){
       return pair.toString(separator) + " " + separator + " " + result;
    } 
    
    public boolean equals(LdResourceTriple triple){
        return this.pair.equals(triple.getResourcePair()) && this.result == triple.getSimilarityResult();
    }
    
    
    
    
}