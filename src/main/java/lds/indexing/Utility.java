/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lds.indexing;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import lds.resource.R;
import org.openrdf.model.URI;
import sc.research.ldq.LdDataset;

/**
 *
 * @author Fouad komeiha
 */
public class Utility {
    
    
    //Used only for Weight class 
    public static void executeMethod(String classPath , String methodName , Object... args){
            
            try {  
                Class<?> params[] = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof R) {
                        params[i] = R.class;
                    } else if (args[i] instanceof URI) {
                        params[i] = URI.class;
                    }
                }
                
                Class<?> cls = Class.forName(classPath);
                Object _instance = cls.newInstance();
                Method method = cls.getDeclaredMethod(methodName, params);
                method.invoke(_instance , args);
            }
             catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException ex) {
                Logger.getLogger(LdIndexer.class.getName()).log(Level.SEVERE, null, ex);
 
            }
            
        }
    
    public static Object executeMethod(LdDataset dataset , String classPath , String methodName , Object... args) {
            
            Object returnedItem = null;
            
            try {  
                Class<?> params[] = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof R) {
                        params[i] = R.class;
                    } else if (args[i] instanceof URI) {
                        params[i] = URI.class;
                    } else if (args[i] instanceof String) {
                        params[i] = String.class;
                    }
                }
                
                Class<?> cls = Class.forName(classPath);
                Constructor<?> cons = cls.getConstructor(LdDataset.class);
                Object _instance = cons.newInstance(dataset);
//                Object _instance = cls.newInstance();
                Method method = cls.getDeclaredMethod(methodName, params);
                returnedItem = method.invoke(_instance , args);
            }
             catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException ex) {
                Logger.getLogger(LdIndexer.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
            
            return returnedItem;
        }
    
    
        public static boolean checkPath(String path){
            File file = new File(path);

            if (!file.isDirectory()){
               file = file.getParentFile();

                if (! file.exists()){
                    File dir = new File(file.getPath());
                    return dir.mkdirs();

                }
            }
            
            return true;
        }
        
        public static String getClassPath(String methodPath){
            
            return methodPath.substring(0, methodPath.lastIndexOf(".")).trim();
        }
        
        public static String getMethodName(String methodPath){
            
            return methodPath.substring(methodPath.lastIndexOf(".") + 1).trim();
        }
    
}