package game.aho.lps;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import game.aho.lps.Vars.NewIconListener;

@SuppressWarnings("serial")
public class Avatar extends JLabel {
    public static final Color bacgroundColorPattern = new Color(42, 42, 42);
    public static final Color circleColorPattern = new Color(143, 143, 143);

    private List<Cosmetic> cosmetics = new ArrayList<Cosmetic>();

    private Color backgroundColor;
    private Color circleColor;

    private class Cosmetic {
	public String type;
	public Image image;
    }

    public Avatar(ImageIcon avatarFace, Color backgroundColor, Color circleColor) {
	super();
	this.backgroundColor = backgroundColor;
	this.circleColor = circleColor;
	updateIcon();
	setCosmetic(avatarFace, CosmeticType.FACE);
	this.setHorizontalTextPosition(JLabel.CENTER);
	this.setVerticalTextPosition(JLabel.NORTH);
	setVisible(true);
	setForeground(circleColor);
	Vars.reloadIconsListeners.add(new NewIconListener() {
	    @Override
	    public void loadNewIcon() {
		updateIcon();
	    }
	});
	repaint();
    }

    /**
     * Updates icon (in case of changing texture quality or something like it)
     */
    private synchronized void updateIcon() {
	ImageIcon newIcon = Vars.imgCharacterBackground;
	newIcon = TheGame.changeColorByPattern(newIcon, circleColorPattern, circleColor);
	newIcon = TheGame.changeColorByPattern(newIcon, bacgroundColorPattern, backgroundColor);
	setCosmetic(newIcon, CosmeticType.BACKGROUND);
    }

    public synchronized void setBackgroundColor(Color newColor) {
	backgroundColor = newColor;
	updateIcon();
    }

    public synchronized void setCircleColor(Color newColor) {
	circleColor = newColor;
	updateIcon();
    }

    public Color getBackgroundColor() {
	return backgroundColor;
    }

    public Color getCircleColor() {
	return circleColor;
    }

    /**
     * Sets cosmetics to new image.
     * If type already exists, replaces it.
     * If newCosmetic is null, removes it
     * 
     * @param newCosmetic
     *            new Image of cosmetic
     * @param type
     *            type of new cosmetic
     */
    public synchronized void setCosmetic(ImageIcon newCosmetic, CosmeticType type) {
	setCosmetic(newCosmetic, type.toString());
    }

    /**
     * * Sets cosmetics to new image.
     * If type already exists, replaces it.
     * If newCosmetic is null, removes it
     * 
     * @param newCosmetic
     *            new Image of cosmetic
     * @param type
     *            codename of new cosmetic
     */
    public synchronized void setCosmetic(ImageIcon newCosmetic, String type) {
	final Cosmetic newItem = new Cosmetic();
	newItem.image = newCosmetic.getImage();
	newItem.type = type.toString();

	for (int i = 0; i < cosmetics.size(); i++)
	    if (cosmetics.get(i).type.equalsIgnoreCase(type)) {
		cosmetics.set(i, newItem);
		return;
	    }
	cosmetics.add(newItem);
	repaint();
    }

    /**
     * Copies all cosmetic and text from origin
     * Useful when you dont want to create new object instance
     * 
     * @param origin
     *            player used as source
     */
    public void copyFrom(Avatar origin) {
	cosmetics = origin.cosmetics;
	setText(origin.getText());
    }

    @Override
    public void paintComponent(Graphics g) {
	final int width = (int) (((double) getWidth()) * 0.9);
	final int height = (int) (((double) getHeight()) * 0.9);
	setIcon(TheGame.scaleImage(Vars.imgCharacterIconSize, width, height));
	for (Cosmetic cosmetic : cosmetics)
	    g.drawImage(cosmetic.image, 0, 0, width, height, null);
	super.paintComponent(g);
    }

}
