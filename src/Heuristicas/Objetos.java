/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Heuristicas;

import ab.demo.other.ActionRobot;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.Vision;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author RaulFreire
 */
public final class Objetos {

    private static List<ABObject> wood;
    private static List<ABObject> ice;
    private static List<ABObject> stone;

    public Objetos() {
/*       
        wood = new LinkedList<>();
        ice = new LinkedList<>();
        stone = new LinkedList<>();

        wood.addAll(findWood(getTodosObjetos()));
        ice.addAll(findIce(getTodosObjetos()));
        stone.addAll(findStone(getTodosObjetos()));        
*/        
    }

    //metodo adicionado para gravar em um List todos wood encontrados
    public List<ABObject> findWood(List<ABObject> objects) {
        List<ABObject> ans = new LinkedList<>();
        for (ABObject obj : objects) {
            if (obj.type == ABType.Wood) {
                ans.add(obj);
            }
        }
        return ans;
    }

    //metodo adicionado para gravar em um List todos os ice encontrados
    public List<ABObject> findIce(List<ABObject> objects) {
        List<ABObject> ans = new LinkedList<>();
        for (ABObject obj : objects) {
            if (obj.type == ABType.Ice) {
                ans.add(obj);
            }
        }
        return ans;
    }

    //metodo adicionado para gravar em um List todos os wood encontrados
    public List<ABObject> findStone(List<ABObject> objects) {
        List<ABObject> ans = new LinkedList<>();
        for (ABObject obj : objects) {
            if (obj.type == ABType.Stone) {
                ans.add(obj);
            }
        }
        return ans;
    }
    
    public int size(List<ABObject> objeto){
        int i;        
        i = objeto.size();        
        return i;
    }
  
     public static List<ABObject> getTodosObjetos() {
        BufferedImage screenshot = ActionRobot.doScreenShot();
        Vision vision = new Vision(screenshot);
        List<ABObject> allObjects = vision.getVisionRealShape().findObjects();
        allObjects.addAll(vision.getVisionRealShape().findPigs());
        allObjects.addAll(vision.findTNTs());
        return allObjects;
    }
}
