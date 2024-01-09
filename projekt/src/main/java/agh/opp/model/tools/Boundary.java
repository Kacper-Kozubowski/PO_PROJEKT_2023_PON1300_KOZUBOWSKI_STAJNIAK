package agh.opp.model.tools;

public record Boundary(int width, int height) {

    public Vector2d normalizePosition(Vector2d position) throws OutOfBoundaryException {
        if (withinY(position)) {
            int x = normalizeX(position.x());
            return new Vector2d(x, position.y());
        } else {
            throw new OutOfBoundaryException(position);
        }
    }

    private boolean withinY(Vector2d position) {return (0 <= position.y()) && (position.y() < height);}

    private int normalizeX(int x) {
        if (x < 0) {
            x = (x + width * ((Math.abs(x) / width) + 1)) % width;
        } else {
            x = x % width;
        }
        return x;
    }
}
