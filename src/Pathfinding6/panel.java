/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pathfinding6;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;

/**
 *
 * @author Alex
 */
public class panel extends javax.swing.JPanel {

    public boolean[][] map;
    public Point[] pathPoints;
    //public Point[] vertices;
    //public Edge[] edges;

    /**
     * Creates new form panel
     */
    public panel() {
        initComponents();
        setSize(200,200);
        map = new boolean[0][0];
        pathPoints = new Point[0];
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.gray);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.red);
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j]) g.fillRect(i * frame.size, j*frame.size, frame.size, frame.size);
            }
        }
        g.setColor(Color.blue);
        System.out.println(Arrays.toString(pathPoints));
        if (pathPoints != null) {
            for (int i = 0; i < pathPoints.length; i++) {
                Point p = pathPoints[i];
                if (p != null) {
                    g.fillRect(p.x * frame.size, p.y * frame.size, frame.size, frame.size);
                }
                if (i < pathPoints.length - 1) {
                    Point p2 = pathPoints[i+1];
                    g.drawLine(p.x * frame.size + frame.size / 2, p.y * frame.size + frame.size / 2, p2.x * frame.size + frame.size / 2, p2.y * frame.size + frame.size / 2);
                }
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
