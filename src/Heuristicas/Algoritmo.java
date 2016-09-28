/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Heuristicas;

import java.util.Random;

/**
 *
 * @author RaulFreire
 */
public class Algoritmo {
    
    private static Individuo solucao;
    private static double taxaDeCrossover;
    
    public static Populacao novaGeracao (Populacao populacao, boolean elitismo){
        Random r = new Random();
        
        //nova população do mesmo tamanho da antiga
        Populacao novaPopulacao = new Populacao(populacao.getTamPopulacao(), false);
        
        //se tiver elitismo, mantém o melhor indivíduo da geração atual
        if (elitismo) {
            novaPopulacao.setIndividuo(populacao.getIndividuo(0));
        }
        
        //insere novos indivíduos na nova população, até atingir o tamanho máximo
        while(novaPopulacao.getNumIndividuos() < populacao.getTamPopulacao())
        {
            //seleciona os 2 pais mais aptos
            Individuo[] pais = selecaoMaisAptos(populacao);
            
            Individuo[] filhos = new Individuo[2];
            
            //verifica a taxa de crossover, se sim realiza o crossover, se não, mantém os pais selecionados para a próxima geração
            if(r.nextDouble() <= taxaDeCrossover)
            {
                filhos = crossover(pais[1], pais[0]);
            } else {
                filhos[0] = new Individuo(pais[0].getObjeto(), pais[0].getScore());
                filhos[1] = new Individuo(pais[1].getObjeto(), pais[1].getScore());
            }
            //adiciona os filhos na nova geração
            novaPopulacao.setIndividuo(filhos[0]);
            novaPopulacao.setIndividuo(filhos[1]);            
        }
        //ordena a nova população
        novaPopulacao.ordenaPopulacao();
        return novaPopulacao;
    }
    
    public static Individuo[] selecaoMaisAptos(Populacao populacao) {
        Random r = new Random();
        Populacao populacaoTemp = new Populacao(5, false);
        
        //seleciona 2 indivíduos mais aptos da populacao
        populacaoTemp.setIndividuo(populacao.rodarRoleta(populacao.gerarRoleta(populacao)));
        populacaoTemp.setIndividuo(populacao.rodarRoleta(populacao.gerarRoleta(populacao)));    
        populacaoTemp.setIndividuo(populacao.rodarRoleta(populacao.gerarRoleta(populacao)));
        populacaoTemp.setIndividuo(populacao.rodarRoleta(populacao.gerarRoleta(populacao)));     
        populacaoTemp.setIndividuo(populacao.rodarRoleta(populacao.gerarRoleta(populacao)));  
        
        //ordena a população
        populacaoTemp.ordenaPopulacao();
        
        Individuo[] pais = new Individuo[2];
        
        //seleciona os 2 melhores desta população
        pais[0] = populacaoTemp.getIndividuo(0);
        pais[1] = populacaoTemp.getIndividuo(1);
        
        return pais;
    }

//****************************** VERIFICAR **************************************    
    public static Individuo[] crossover(Individuo pai1, Individuo pai2) {
        
        Individuo[] filhos = new Individuo[2];        
        
        filhos[0] = pai2;
        filhos[1] = pai1;
        
        //troca os genes dos pais
        filhos[0].objeto = pai1.objeto;
        filhos[0].score = pai1.score;
        filhos[1].objeto = pai2.objeto;
        filhos[1].score = pai2.score;
        
        filhos[0].aptidao = pai1.aptidao;
        filhos[1].aptidao = pai2.aptidao;      
        
        filhos[0].objeto.x = pai2.objeto.x;
        filhos[0].objeto.y = pai1.objeto.y;
        
        filhos[1].objeto.x = pai2.objeto.x;
        filhos[1].objeto.y = pai1.objeto.y;
        
        filhos[1].objeto.angle = pai2.objeto.angle;
        filhos[0].objeto.angle = pai1.objeto.angle;
        
        return filhos;
    }
    
    public static double getTaxaDeCrossover() {
        return taxaDeCrossover;
    }

    public static void setTaxaDeCrossover(double taxaDeCrossover) {
        Algoritmo.taxaDeCrossover = taxaDeCrossover;
    }
}
