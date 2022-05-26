package com.example.drawer;

import com.example.drawer.Vector;
import java.util.ArrayList;
import java.util.List;

public class VectorMap {

    private List<Vector> vectorList = new ArrayList<>();

    public List<Vector> getVectorList() {
        return vectorList;
    }

    public VectorMap(int x, int y) {
        vectorList.add(new Vector(0, 0, x, y));
    }

    public VectorMap() {

    }

    public void add(int x, int y) {
        Vector lastVector = vectorList.get(vectorList.size() - 1);   //gets last vector


        int vx = x - lastVector.absoluteX;
        int vy = y - lastVector.absoluteY;

        vectorList.add(new Vector(vx, vy, x, y));
    }

    public void clear() {
        vectorList.clear();
    }


    public ArrayList<Instruction> generateInstructions(double scale) {
        if( vectorList.isEmpty() )        return new ArrayList<Instruction>();

        ArrayList<Instruction> instructions = new ArrayList<>();

        for (int i = 1; i < vectorList.size()-1 ; i++){
            double distance = vectorList.get(i).getMagnitude();
            double angle = getVectorAngle(vectorList.get(i), vectorList.get(i+1));
            instructions.add(new Instruction(distance, angle));
        }
        instructions.add(new Instruction(vectorList.get(vectorList.size()-1).getMagnitude(), 0.0)  );

        return instructions;
    }



    public double calculateSize() {
        double total = 0;

        for (Vector vector : vectorList) {
            total += Math.sqrt(Math.pow(vector.posX, 2) + Math.pow(vector.posY, 2));
        }

        return total;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Vector vector : vectorList) {
            stringBuilder.append("-[X," + vector.posX + "; Y," + vector.posY + "] \n");
        }
        return stringBuilder.toString();
    }


    //positive = left ,   negative = right
    public double getVectorAngle(Vector vector1, Vector vector2){
        double angle = 0.0;
        Vector dotProductVec = new Vector(vector1);
        dotProductVec.multiply(vector2);
        double dotProduct = dotProductVec.posX + dotProductVec.posY;

        angle = (dotProduct / ( vector1.getMagnitude() * vector2.getMagnitude() ) ) ;
        //System.out.println("Mag" + vector1.getMagnitude());

        angle = Math.acos(angle) * (180/Math.PI);

        //left right??

        Vector newPoint = new Vector(vector1);
        newPoint.add(vector2);

        if(vector1.posX != 0){ //if not vertical
            double h = (double) vector1.posY / (double) vector1.posX;


            if(vector1.posX > 0){ // car pointing to the right
                if(newPoint.posY > (newPoint.posX * h)) angle *= -1;
            }else if(vector1.posX < 0){ // car pointing left
                if (newPoint.posY < (newPoint.posX * h)) angle *= -1;
            }

        }else if(newPoint.posY > 0 ){// if downward
            if(newPoint.posX < 0) angle *= -1;
        }else if(newPoint.posY < 0) {//if upward
            if(newPoint.posX > 0 ) angle *= -1;
        }


        return angle;
    }

    public void multiply(Vector vector) {
        for (Vector v : vectorList) {
            v.multiply(vector);
        }
    }

    public void multiply(int factor) {
        for (Vector v : vectorList) {
            v.multiply(factor);
        }
    }

    public void multiply(float factor) {
        for (Vector v : vectorList) {
            v.multiply(factor);
        }
    }

}








