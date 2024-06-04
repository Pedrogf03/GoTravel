package com.gotravel.Utils;

import com.gotravel.GoTravel;
import javafx.scene.text.Font;

import java.util.Objects;

public class Fonts {

    public static Font titleMedium = Font.loadFont(Objects.requireNonNull(GoTravel.class.getResource("fonts/poetsen_one_regular.ttf")).toExternalForm(), 35);
    public static Font titleSmall = Font.loadFont(Objects.requireNonNull(GoTravel.class.getResource("fonts/poetsen_one_regular.ttf")).toExternalForm(), 25);

    public static Font labelMedium = Font.loadFont(Objects.requireNonNull(GoTravel.class.getResource("fonts/poppins_regular.ttf")).toExternalForm(), 20);
    public static Font labelSmall = Font.loadFont(Objects.requireNonNull(GoTravel.class.getResource("fonts/poppins_regular.ttf")).toExternalForm(), 15);

}
