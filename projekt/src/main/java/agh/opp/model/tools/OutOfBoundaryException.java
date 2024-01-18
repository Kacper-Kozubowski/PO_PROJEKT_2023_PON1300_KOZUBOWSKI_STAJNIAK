package agh.opp.model.tools;

public class OutOfBoundaryException extends Exception{
    public OutOfBoundaryException(Vector2d position) {
        super("Position " + position + "is out of boundary.");
    }
}
