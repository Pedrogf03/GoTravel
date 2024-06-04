package com.gotravel.gotraveldesktop.Utils;

import com.gotravel.gotraveldesktop.GoTravel;
import javafx.scene.text.Font;

import java.util.Objects;

public class Fonts {

    public static Font titleMedium = Font.loadFont(Objects.requireNonNull(GoTravel.class.getResource("fonts/poetsen_one_regular.ttf")).toExternalForm(), 35);
    public static Font titleSmall = Font.loadFont(Objects.requireNonNull(GoTravel.class.getResource("fonts/poetsen_one_regular.ttf")).toExternalForm(), 25);

    public static Font label = Font.loadFont(Objects.requireNonNull(GoTravel.class.getResource("fonts/poppins_regular.ttf")).toExternalForm(), 20);

}
