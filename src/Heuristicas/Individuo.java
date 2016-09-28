/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Heuristicas;

import ab.demo.NaiveAgent;
import ab.vision.ABObject;
import ab.vision.ABType;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author RaulFreire
 */
public class Individuo {

    public int score;    
    public ABObject objeto;
    public static List<ABObject> objetos = new LinkedList<>();
    public int aptidao = 0;

    //gera um gene com objeto aleatório
    public Individuo(boolean aleatorio) {
        objeto = new ABObject();
        score = 4300;
        

        if (aleatorio) {
            objetos = new LinkedList(Objetos.getTodosObjetos());

            System.out.println("Tamanho do List: " + objetos.size());
            aleatorio = false;
        }
        Random r = new Random();
        objeto = objetos.get(r.nextInt(objetos.size()));

        geraAptidao();
    }

    //cria um indivíduo com os genes definidos
    public Individuo(ABObject objeto, int score) {
        this.objeto = objeto;
        this.score = score;

        geraAptidao();
    }

    private void geraAptidao() {
   
        if(objeto.type.equals(ABType.Pig)){
            score += 50;
        }
        if(objeto.type.equals(ABType.TNT)){
            score += 70;            
        }
        
        this.aptidao = this.score;
    }

    public int getAptidao() {
        return aptidao;
    }

    public ABObject getObjeto() {
        return this.objeto;
    }

    public int getScore() {
        return this.score;
    }
}
