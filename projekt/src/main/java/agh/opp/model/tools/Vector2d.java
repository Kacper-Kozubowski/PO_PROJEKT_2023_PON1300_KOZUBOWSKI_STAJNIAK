package agh.opp.model.tools;

public record Vector2d(int x, int y) {
    public String toString() {return "(" + x + ", " + y + ")";}
    public Vector2d add(Vector2d other) {return new Vector2d(x + other.x, y + other.y);}
}
