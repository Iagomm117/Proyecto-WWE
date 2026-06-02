package util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;

public class TemaUtil {

    public static void alternarTema() {
        if (FlatLaf.isLafDark()) {
            FlatLightLaf.setup();
        } else {
            FlatDarkLaf.setup();
        }
        FlatLaf.updateUI();
    }

    public static boolean esModoOscuro() {
        return FlatLaf.isLafDark();
    }
}
