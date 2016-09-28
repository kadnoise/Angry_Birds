/**
 * ***************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK * Copyright (c) 2014, XiaoYu (Gary) Ge,
 * Stephen Gould, Jochen Renz * Sahan Abeyasinghe,Jim Keys, Andrew Wang, Peng
 * Zhang * All rights reserved. *This work is licensed under the terms of the
 * GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version. *To view a copy of this license, visit http://www.gnu.org/licenses/
 * ***************************************************************************
 */
package ab.demo;

import Heuristicas.*;
import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.utils.StateUtil;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.GameStateExtractor;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class NaiveAgent implements Runnable {

    private ActionRobot aRobot;
    private Random randomGenerator;
    public int currentLevel = 1;
    public static int time_limit = 12;
    private Map<Integer, Integer> scores = new LinkedHashMap<Integer, Integer>();
    TrajectoryPlanner tp;
    private boolean firstShot;
    private Point prevTarget;
    private static int scoreShot = 0;
    private static int temp = 0;
    private ABObject alvo = null;
    private ABType bird = null;
    //variavel conta numero de vzs restart
    private static int cnt = 0;
    //este arquivo contém os scores de cada level
    File file = new File("Level-Score.txt");
    //Se o arquivo nao existir, então crie-o.
    public FileWriter fw;
    public BufferedWriter bw;
    private static final List<ABObject> objeto = new LinkedList<>();
    private static final List<Integer> scoreshot = new LinkedList<>();
    private static final List<Integer> MemID = new LinkedList<>();
    private static final List<Integer> MemObjID = new LinkedList<>();

    // Implementação padrão do naive agent.
    public NaiveAgent() {

        aRobot = new ActionRobot();
        tp = new TrajectoryPlanner();
        prevTarget = null;
        firstShot = true;
        randomGenerator = new Random();

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file);
        } catch (IOException e) {
            System.err.println("erro ao gravar arquivo file: " + e);
        }

        bw = new BufferedWriter(fw);
        // --- go to the Poached Eggs episode level selection page ---
        ActionRobot.GoFromMainMenuToLevelSelection();

    }

    // run the client
    @Override
    public void run() {

        aRobot.loadLevel(currentLevel);

        while (true) {

            GameState state = solve();
            if (state == GameState.WON) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int score = StateUtil.getScore(ActionRobot.proxy);
                if (!scores.containsKey(currentLevel)) {
                    scores.put(currentLevel, score);
                } else {
                    if (scores.get(currentLevel) < score) {
                        scores.put(currentLevel, score);
                    }
                }
                int totalScore = 0;
                int lastKey = 0;
                //imprime os scores de todos os levels jagados ao final de cada level
                for (Integer key : scores.keySet()) {

                    lastKey = key;
                    totalScore += scores.get(key);
                    System.out.println("Level " + key + " Score: " + scores.get(key) + " ");
                }
                //armazena na variavel contet informações para gravar em file
                String content = "Level " + lastKey + " Score: " + scores.get(lastKey) + " qtd de restarts: " + cnt;
                //gravar file
                try {
                    bw.write(content);
                    bw.newLine();
                    bw.flush();
                } catch (IOException e) {
                    System.err.println("erro ao gravar arquivo file: " + e);
                }

                System.out.println("Total Score: " + totalScore);
                System.out.println("Dados gravados em file\n");
                objeto.clear();
                scoreshot.clear();
                MemID.clear();
                //re-size cnt
                cnt = 0;
                //zerar contadores de score
                scoreShot = 0;
                temp = 0;
                //quando xegar em level 21 sair do laço e ir pra seleção e menu novamente
                if (currentLevel > 21) {
                    break;
                }

                aRobot.loadLevel(++currentLevel);

                // make a new trajectory planner whenever a new level is entered
                tp = new TrajectoryPlanner();

                // first shot on this level, try high shot first
                firstShot = true;

            } else if (state == GameState.LOST) {
                System.out.println("Restart Level");
                MemID.clear();
                cnt++;
                scoreShot = 0;
                temp = 0;
                //se repetir mais que 15 vezes o mesmo level, pular.
                if (cnt >= 40) {
                    String content = " Level " + currentLevel + " Score: " + 0 + " qtd de restarts: " + cnt;
                    //grava content em file
                    try {
                        bw.write(content);
                        bw.newLine();
                        bw.flush();
                    } catch (IOException e) {
                        System.err.println("erro ao gracar file: " + e);
                    }
                    System.out.println("Dados gravados em file\n");
                    cnt = 0;
                    aRobot.loadLevel(++currentLevel);
                    objeto.clear();
                    scoreshot.clear();
                }

                aRobot.restartLevel();

            } else if (state == GameState.LEVEL_SELECTION) {
                System.out.println("Unexpected level selection page, go to the last current level : "
                        + currentLevel);
                aRobot.loadLevel(currentLevel);
            } else if (state == GameState.MAIN_MENU) {
                System.out.println("Unexpected main menu page, go to the last current level : "
                        + currentLevel);
                ActionRobot.GoFromMainMenuToLevelSelection();
                aRobot.loadLevel(currentLevel);
            } else if (state == GameState.EPISODE_MENU) {
                System.out.println("Unexpected episode menu page, go to the last current level : "
                        + currentLevel);
                ActionRobot.GoFromMainMenuToLevelSelection();
                aRobot.loadLevel(currentLevel);
            }

        }
    }

    private double distance(Point p1, Point p2) {
        return Math.sqrt((double) ((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
                * (p1.y - p2.y)));
    }

    public GameState solve() {

        // capture Image
        BufferedImage screenshot = ActionRobot.doScreenShot();

        // process image
        Vision vision = new Vision(screenshot);

        // find the slingshot //alterado para realShape
        Rectangle sling = vision.findSlingshotRealShape();

        // confirm the slingshot
        while (sling == null && aRobot.getState() == GameState.PLAYING) {
            System.out.println("No slingshot detected. Please remove pop up or zoom out");
            ActionRobot.fullyZoomOut();
            screenshot = ActionRobot.doScreenShot();
            vision = new Vision(screenshot);
            sling = vision.findSlingshotRealShape(); //alterado para realShape
        }
        // get all the pigs //alterado para realShape
        //apenas alterando para realShape ja foi observado uma melhora no agente
        List<ABObject> pigs = vision.findPigsRealShape();
        List<ABObject> birds = vision.findBirdsRealShape();

        GameState state = aRobot.getState();
        if (state != GameState.PLAYING) {
            return state;
        }

        // if there is a sling, then play, otherwise just skip.
        if (sling != null) {

            if (!pigs.isEmpty()) {
                Point releasePoint = null;
                Shot shot = new Shot();
                int dx, dy;
                {
                    //adicionei Lists para captura de objetos em realShape
                    List<ABObject> allObjects = vision.getVisionRealShape().findObjects();
                    allObjects.addAll(vision.findPigsMBR());
                    Objetos obj = new Objetos();
                    TapTime time = new TapTime(sling, allObjects);
                    List<ABObject> wood = new LinkedList(obj.findWood(allObjects));
                    List<ABObject> ice = new LinkedList(obj.findIce(allObjects));
                    List<ABObject> stone = new LinkedList(obj.findStone(allObjects));

                    Point refPoint = tp.getReferencePoint(sling);

                    boolean check = true; //variavel controla se existe mais de um pig
                    Point targetPoint = null; //variavel que marca ponto do alvo

                    //condição adicionada para estimar trajetoria se existir apenas um pig
                    if (pigs.size() == 1) {
                        pigs = vision.findPigsRealShape();
                        System.out.println("Apenas 1 pig!");
                        alvo = pigs.get(0);
                        System.out.println("Definido Alvo:\nType: " + alvo.getType() + ", Point: " + alvo.getCenter());
                        check = false;
                    }
                    if (check) //****************| caso haja mais de 1 pig, os objetos em maior volume serao selecionados aleatoriamente para hit |****************************\\
                    {
                        System.out.println("MAIS DE 1 PIG !!");
                        //birds = vision.findBirdsRealShape();
                        boolean flag = true;

                        if (cnt % 3 != 0 || cnt % 5 == 0) {
                            if (birds.size() % 2 != 0 || cnt % 5 == 0) {
                                Random r = new Random();
                                if (ice.size() > wood.size() && ice.size() > stone.size()) {
                                    alvo = ice.get(r.nextInt(ice.size()));
                                    flag = false;
                                } else if (wood.size() > ice.size() && wood.size() > stone.size()) {
                                    alvo = wood.get(r.nextInt(wood.size()));
                                    flag = false;
                                } else {
                                    alvo = stone.get(r.nextInt(stone.size()));
                                    flag = false;
                                }
                            }
                        }

                        if (flag) {

                            //taxa de crossover de 2%
                            Algoritmo.setTaxaDeCrossover(0.2);

                            boolean elitismo = true;

                            //tamanho da população
                            int tamPop = 300;

                            int numMaxGeracoes = 25;

                            //cria a primeira população aleatérioa
                            Populacao populacao = new Populacao(tamPop, true);
                            //System.out.println(MemID.size());

                            int j = 0;
                            while (j < allObjects.size() - 1) {
                                MemObjID.add(allObjects.get(j += 1).id);
                            }

                            for (int i = 0; i < objeto.size(); i++) {

                                if (!MemID.contains(objeto.get(i).id) && MemObjID.contains(objeto.get(i).id)) {
                                    Individuo ind = new Individuo(objeto.get(i), scoreshot.get(i));
                                    populacao.setIndividuo(ind);
                                }
                                populacao.ordenaPopulacao();
                            }

                            int geracao = 0;
                            while (geracao < numMaxGeracoes) {
                                geracao++;

                                //cria nova populacao
                                populacao = Algoritmo.novaGeracao(populacao, elitismo);

                                System.out.println("Geração " + geracao + " | Aptidão: " + populacao.getIndividuo(0).getAptidao() + " | Melhor Objeto: " + populacao.getIndividuo(0).getObjeto());

                                if (geracao == numMaxGeracoes) {
                                    System.out.println("Número Maximo de Gerações, Alvo:| " + populacao.getIndividuo(0).getObjeto() + " (Aptidão " + populacao.getIndividuo(0).getAptidao() + ")");
                                }
                            }
                            alvo = populacao.getIndividuo(0).getObjeto();
                            populacao.setIndividuo(null, 0);
                        }
                    }
                    System.out.println("Definido Alvo, id:" + alvo.id + " cnt: " + cnt + "\nType: " + alvo.getType() + ", Point: " + new Point((int) alvo.getMinX(), (int) alvo.getCenterY()));
                    MemID.add(alvo.id);

                    if (alvo.type != ABType.Pig) {
                        targetPoint = new Point((int) alvo.getMinX(), (int) alvo.getCenterY());
                    } else {
                        targetPoint = alvo.getCenter();
                    }

                    // estimate the trajectory
                    ArrayList<Point> pts = tp.estimateLaunchPoint(sling, targetPoint);

                    // do a high shot when entering a level to find an accurate velocity
                    if (firstShot && pts.size() > 1) {
                        releasePoint = pts.get(1);
                    } else if (pts.size() == 1) {
                        releasePoint = pts.get(0);
                    } else if (pts.size() == 2) {
                        // randomly choose between the trajectories, with a 1 in
                        // 6 chance of choosing the high one
                        if (randomGenerator.nextInt(6) == 0) {
                            releasePoint = pts.get(1);
                        } else {
                            releasePoint = pts.get(0);
                        }
                    } else if (pts.isEmpty()) {
                        System.out.println("No release point found for the target");
                        System.out.println("Try a shot with 45 degree");
                        releasePoint = tp.findReleasePoint(sling, Math.PI / 4);
                    }

                    //Calculate the tapping time according the bird type 
                    if (releasePoint != null) {
                        double releaseAngle = tp.getReleaseAngle(sling, releasePoint);
                        System.out.println("Release Point: " + releasePoint);
                        System.out.println("Release Angle: " + Math.toDegrees(releaseAngle));

                        int tapInterval = 0;
                        int tapTime = 0;

                        bird = aRobot.getBirdTypeOnSling();
                        switch (bird) {

                            case RedBird:
                                tapInterval = 0;
                                break;               // start of trajectory
                            case YellowBird:
                                tapInterval = 65 + randomGenerator.nextInt(23);
                                break; // 65-90% of the way
                            case WhiteBird:
                                tapInterval = (int) time.getWhitebirdTapTime(targetPoint);
                                break; // 70-90% of the way
                            case BlackBird:
                                tapInterval = 90 + randomGenerator.nextInt(45); //alterado tempo de tap
                                break; // 70-90% of the way
                            case BlueBird:
                                tapInterval = 65 + randomGenerator.nextInt(20);
                                break; // 65-85% of the way
                            default:
                                tapInterval = 60;
                        }

                        tapTime = tp.getTapTime(sling, releasePoint, targetPoint, tapInterval);

                        dx = (int) releasePoint.getX() - refPoint.x;
                        dy = (int) releasePoint.getY() - refPoint.y;
                        shot = new Shot(refPoint.x, refPoint.y, dx, dy, 0, tapTime);

                    } else {
                        System.err.println("No Release Point Found");
                        return state;
                    }
                }

                // check whether the slingshot is changed. the change of the slingshot indicates a change in the scale.
                {
                    ActionRobot.fullyZoomOut();
                    screenshot = ActionRobot.doScreenShot();
                    vision = new Vision(screenshot);
                    Rectangle _sling = vision.findSlingshotRealShape();
                    if (_sling != null) {
                        double scale_diff = Math.pow((sling.width - _sling.width), 2) + Math.pow((sling.height - _sling.height), 2);
                        if (scale_diff < 25) {
                            if (dx < 0) {
                                aRobot.cFastshoot(shot);
                                state = aRobot.getState();
                                if (state == GameState.PLAYING) {
                                    screenshot = ActionRobot.doScreenShot();
                                    vision = new Vision(screenshot);

                                    //captura score e cria memória temporaria
                                    GameStateExtractor game = new GameStateExtractor();
                                    int novotemp = game.getScoreInGame(screenshot);
                                    temp += scoreShot;
                                    scoreShot = novotemp - temp;
                                    objeto.add(alvo);
                                    scoreshot.add(scoreShot);

                                    System.out.println("Score: " + scoreShot);
                                    List<Point> traj = vision.findTrajPoints();
                                    tp.adjustTrajectory(traj, sling, releasePoint);
                                    firstShot = false;
                                }
                            }
                        } else {
                            System.out.println("Scale is changed, can not execute the shot, will re-segement the image");
                        }
                    } else {
                        System.out.println("no sling detected, can not execute the shot, will re-segement the image");
                    }
                }
            }
        }
        return state;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }
}
