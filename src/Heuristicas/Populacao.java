/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Heuristicas;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author RaulFreire
 */
public class Populacao {

    private Individuo[] Individuos;
    private int tamPopulacao;
    private boolean percorre = true;

    public Populacao(int tamPop, boolean fully) {

        tamPopulacao = tamPop;
        Individuos = new Individuo[tamPop];
        for (int i = 0; i < Individuos.length; i++) {
            if (fully) {
                Individuos[i] = new Individuo(percorre);
            } else {
                Individuos[i] = null;
            }
            percorre = false;
        }
    }

    //coloca um indivíduo em uma certa posição da população
    public void setIndividuo(Individuo individuo, int posicao) {
        Individuos[posicao] = individuo;
    }

    //coloca um indivíduo na próxima posição disponível da população
    public void setIndividuo(Individuo individuo) {
        for (int i = 0; i < Individuos.length; i++) {
            if (Individuos[i] == null) {
                Individuos[i] = individuo;
                return;
            } else if (i == Individuos.length - 1) {
                Individuos[i] = individuo;
            }
        }
        ordenaPopulacao();
    }

    public Individuo[] gerarRoleta(Populacao populacao) {

        List<Individuo> roleta = new ArrayList<>();
        Integer menorFitness = populacao.getIndividuo(populacao.getTamPopulacao() - 1).getAptidao();
        if (menorFitness == 0) {
            menorFitness = 1;
        }

        for (Individuo individuo : Individuos) {
            // garante que o inviduo sempre terá ao menos uma chance de ser escolhido
            Integer fim = individuo.getAptidao();
            for (int j = 0; j <= fim / menorFitness; j++) {
                roleta.add(individuo);
            }
        }
        return (Individuo[]) roleta.toArray(new Individuo[roleta.size()]);
    }

    public Individuo rodarRoleta(Individuo[] roleta) {
        Integer IndividuoEscolhido = (int) (Math.random() * (roleta.length)); // faz a escolha entre 0 e o numero maximo de elementos; 
        return roleta[IndividuoEscolhido];
    }

    //ordena a população pelo valor de aptidão de cada indivíduo, do maior valor para o menor, assim se eu quiser obter o melhor indivíduo desta população, 
    //acesso a posição 0 do array de indivíduos
    public void ordenaPopulacao() {
        boolean trocou = true;
        while (trocou) {
            trocou = false;
            for (int i = 0; i < Individuos.length - 1; i++) {
                if (Individuos[i].getAptidao() < Individuos[i + 1].getAptidao()) {
                    Individuo temp = Individuos[i];
                    Individuos[i] = Individuos[i + 1];
                    Individuos[i + 1] = temp;
                    trocou = true;
                }
            }
        }
    }

    //número de indivíduos existentes na população
    public int getNumIndividuos() {
        int num = 0;
        for (int i = 0; i < Individuos.length; i++) {
            if (Individuos[i] != null) {
                num++;
            }
        }
        return num;
    }

    public int getTamPopulacao() {
        return tamPopulacao;
    }

    public Individuo getIndividuo(int pos) {
        return Individuos[pos];
    }
}
