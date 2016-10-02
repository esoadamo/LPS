package game.aho.lps;

import java.io.File;

public enum Resolution {
    ULTRA("4k/", 0, "4K"), HIGH("high/", 1, "High"), MID("mid/", 2, "Good"), LOW("low/", 3, "Low"), MINIMAL("min/", 4, "Minimal"), SUPEROLD("ms-dos/", 5,
	    "Running on MS/Free-DOS"), POTATO("potato/", 6, "Potato battery"), PIXELART("pixelart/", 7,"Pixel art");

    private final String path;
    private final int index;
    private final String name;
    private final String directoryName;

    Resolution(String directoryName, int index, String name) {
	if (System.getProperty("os.name").toLowerCase().contains("win")) { // Windows are not case-sensitive, so we have to compare filepaths all in (lower) cases
	    this.directoryName = new File(directoryName).getName().toLowerCase();
	    this.path = Vars.dataPrefix.toLowerCase() + directoryName.toLowerCase();
	} else {
	    this.directoryName = new File(directoryName).getName();
	    this.path = Vars.dataPrefix + directoryName;
	}
	this.index = index;
	this.name = name;
    }

    @Override
    public String toString() {
	return name;
    }

    public String getPath() {
	return path;
    }

    public String getDirectoryName() {
	return directoryName;
    }

    public Integer getIndex() {
	return index;
    }

    public static Resolution fromIndex(int index) {
	for (Resolution r : values())
	    if (r.getIndex() == index)
		return r;
	return null;
    }

}
