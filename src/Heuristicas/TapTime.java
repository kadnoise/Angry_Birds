/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Heuristicas;

import ab.planner.TrajectoryPlanner;
import ab.vision.ABObject;
import ab.vision.ABType;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author RaulFreire
 */ 
// Classe que calcula melhor tempo de TAP 
public class TapTime {

    Rectangle sling;
    ArrayList<ABObject> allObject = new ArrayList<>();
    ArrayList<ABObject> wood = new ArrayList<>();
    ArrayList<ABObject> ice = new ArrayList<>();
    ArrayList<ABObject> stone = new ArrayList<>();
    ArrayList<ABObject> tnt = new ArrayList<>();

    public TapTime(Rectangle sling, List<ABObject> allObjects) {
        this.sling = sling;
        this.allObject.addAll(allObjects);

        for (ABObject obj : allObject) {
            if (obj.type == ABType.Wood) {
                wood.add(obj);
            } else if (obj.type == ABType.Ice) {
                ice.add(obj);
            } else if (obj.type == ABType.Stone) {
                stone.add(obj);
            } else if (obj.type == ABType.TNT) {
                tnt.add(obj);
            } else {
                continue;
            }
        }
    }

    public double getWhitebirdTapTime(Point targetPoint) {
        TrajectoryPlanner tp = new TrajectoryPlanner();
        Point releasePoint = tp.findReleasePoint(sling, Math.PI / 4);
        List<Point> trajPoints = tp.predictTrajectory(sling, releasePoint);

        Collections.sort(trajPoints, new Comparator<Point>() {

            public int compare(Point p1, Point p2) {
                return Integer.compare((int) p1.getX(), (int) p2.getY());
            }
        });

        int cnt = 0;
        Line2D line = new Line2D.Double(targetPoint, new Point(targetPoint.x, 0));
        for (Point point : trajPoints) {
            cnt++;
            if (line.contains(point)) {
                break;
            }
        }

        return cnt / trajPoints.size() * 86;
    }
    
    public double getYellowbirdTapTime(Point releasePoint)
    {
        TrajectoryPlanner tp = new TrajectoryPlanner();
        List<Point> trajPoints = tp.predictTrajectory(sling, releasePoint);

        Collections.sort(trajPoints, new Comparator<Point>() {

            public int compare(Point p1, Point p2) {
                return Integer.compare((int) p1.getX(), (int) p2.getY());
            }
        });

        int i=0;
        boolean flag = false;
        for(i=0; i<trajPoints.size(); i++)
        {
            for(ABObject obj : allObject)
            {
                if(obj.contains(trajPoints.get(i)))
                {
                    System.out.println("Block Type: "+obj.type+" -- Point is: "+trajPoints.get(i)+" & Block Center is: "+obj.getCenter());
                    flag = true;
                    break;
                }
            }
            if(flag)
                break;
        }
//        System.out.println("cnt Point is: "+trajPoints.get(i-50)+" & Total Trajectory size is: "+trajPoints.size());
        i-=5;
        return tp.getTapTime(sling, releasePoint, trajPoints.get(i));
    }
}
