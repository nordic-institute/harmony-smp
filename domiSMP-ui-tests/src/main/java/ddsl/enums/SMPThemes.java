package ddsl.enums;

import java.util.Random;

public enum SMPThemes {
    Default_theme("SMP default theme"),
    Blue_theme("Blue theme"),
    Indigo_Pink_theme("Indigo & Pink theme"),
    Pink_Blue_grey_theme("Pink & Blue grey"),
    Purple_Green_theme("Purple & Green theme");


    private final String name; // field to store the string value

    SMPThemes(String name) { // constructor to assign the string value
        this.name = name;
    }

    public static SMPThemes getRandomTheme() {
        SMPThemes[] themes = values();
        int size = themes.length;
        Random random = new Random();
        int index = random.nextInt(size);
        return themes[index];
    }

    public String getName() { // getter method to access the string value
        return name;
    }

}
