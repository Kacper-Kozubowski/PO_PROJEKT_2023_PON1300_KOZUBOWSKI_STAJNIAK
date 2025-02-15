package agh.opp.model.tools.interfaces;

import agh.opp.model.tools.Vector2d;
import javafx.scene.paint.Color;

import java.util.Set;

public interface PlantingController {
    void plant(int toPlant);
    Set<Vector2d> getSpecialArea();
    Color getSpecialAreaColor();
}