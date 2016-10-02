package game.aho.lps;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import aho.util.mysql2PHP2Java.MySQLException;
import aho.util.mysql2PHP2Java.MySQLRespond;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JColorChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JComboBox;
import javax.swing.JDialog;

@SuppressWarnings("serial")
public class DialogCharacterCustomization extends JDialog {

    private JPanel contentPane;
    private Avatar avatar;
    private JLabel lblPrice;

    private static int priceBackground = 4000;
    private static int priceCircle = 2000;

    private boolean colorBackgroundChanged = false;
    private boolean colorCircleChanged = false;
    private static final JComboBox<Cosmetic> comboFace = new JComboBox<Cosmetic>();;
    private static final JComboBox<Cosmetic> comboHat = new JComboBox<Cosmetic>();;
    private static final JComboBox<Cosmetic> comboGlasses = new JComboBox<Cosmetic>();;
    private static final JComboBox<Cosmetic> comboAddon = new JComboBox<Cosmetic>();;

    /**
     * Create the frame.
     */
    public DialogCharacterCustomization(final Player player) {
	setModalityType(ModalityType.APPLICATION_MODAL);
	setBounds(100, 100, 343, 592);
	setResizable(false);
	contentPane = new JPanel() {
	    @Override
	    public void paintComponent(Graphics g) {
		g.drawImage(Vars.imgBackgroundWood, 0, 0, getWidth(), getHeight(), null);
	    }
	};
	
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);

	Box horizontalBox = Box.createHorizontalBox();
	horizontalBox.setBounds(10, 282, 313, 23);
	contentPane.add(horizontalBox);

	JLabel lblSelectColorOf = new JLabel("Select color of ");
	horizontalBox.add(lblSelectColorOf);

	JButton btnInnerCircle = new JButton("background");
	btnInnerCircle.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		Color selectedColor = JColorChooser.showDialog(null, "Select your background color", avatar.getBackgroundColor());
		if (selectedColor != null) {
		    if (!selectedColor.equals(avatar.getBackground()) && !colorBackgroundChanged) {
			colorBackgroundChanged = true;
			changePrice(priceBackground);
		    } else if (selectedColor.equals(player.getBackgroundColor())) {
			colorBackgroundChanged = false;
			changePrice(-1 * priceBackground);
		    }
		    avatar.setBackgroundColor(selectedColor);
		}
	    }
	});
	horizontalBox.add(btnInnerCircle);

	JLabel lblOr = new JLabel("or");
	horizontalBox.add(lblOr);

	JButton btnCircleLine = new JButton("circle line");
	btnCircleLine.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		Color selectedColor = JColorChooser.showDialog(null, "Select your background color", avatar.getBackgroundColor());
		if (selectedColor != null) {
		    if (!selectedColor.equals(avatar.getBackground()) && !colorCircleChanged) {
			colorBackgroundChanged = true;
			changePrice(priceCircle);
		    } else if (selectedColor.equals(player.getCircleColor())) {
			colorBackgroundChanged = false;
			changePrice(-1 * priceCircle);
		    }
		    avatar.setCircleColor(selectedColor);
		}
	    }
	});
	horizontalBox.add(btnCircleLine);

	if (player == null)
	    avatar = new Avatar(new ImageIcon(Vars.imgDataCharacterPrefix + "char_def.png"), Vars.randomColor(), Color.BLACK);
	else
	    avatar = player.getAvatar();

	contentPane.add(avatar);

	avatar.setBounds(10, 11, 320, 260);

	Box horizontalBox_1 = Box.createHorizontalBox();
	horizontalBox_1.setBounds(10, 315, 317, 23);
	contentPane.add(horizontalBox_1);

	JLabel lblFace = new JLabel("Face: ");
	horizontalBox_1.add(lblFace);

	horizontalBox_1.add(comboFace);

	Box horizontalBox_2 = Box.createHorizontalBox();
	horizontalBox_2.setBounds(10, 347, 317, 23);
	contentPane.add(horizontalBox_2);

	JLabel lblHat = new JLabel("Hat: ");
	horizontalBox_2.add(lblHat);

	horizontalBox_2.add(comboHat);

	Box horizontalBox_3 = Box.createHorizontalBox();
	horizontalBox_3.setBounds(10, 380, 317, 23);
	contentPane.add(horizontalBox_3);

	JLabel lblEye = new JLabel("Glasses: ");
	horizontalBox_3.add(lblEye);

	horizontalBox_3.add(comboGlasses);

	Box horizontalBox_4 = Box.createHorizontalBox();
	horizontalBox_4.setBounds(10, 414, 317, 23);
	contentPane.add(horizontalBox_4);

	JLabel lblAddon = new JLabel("Addon: ");
	horizontalBox_4.add(lblAddon);

	horizontalBox_4.add(comboAddon);

	JButton btnReset = new JButton("Reset");
	btnReset.setBackground(new Color(0, 0, 0));
	btnReset.setForeground(new Color(255, 255, 255));
	btnReset.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		avatar.setVisible(false);
		if (player == null)
		    avatar.copyFrom(new Avatar(new ImageIcon(Vars.imgDataCharacterPrefix + "char_def.png"), Vars.randomColor(), Color.BLACK));
		else
		    avatar.copyFrom(player.getAvatar());
		avatar.setVisible(true);
		avatar.repaint();
	    }
	});
	btnReset.setBounds(234, 526, 89, 23);
	contentPane.add(btnReset);

	JButton btnBuy = new JButton("Pick");
	btnBuy.setBackground(new Color(255, 255, 255));
	btnBuy.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (player == null)
		    return;
		player.setBackgroundColor(avatar.getBackgroundColor());
		player.setCircleColor(avatar.getCircleColor());
		final StringBuilder sb = new StringBuilder();
		final Cosmetic[] cosmetics = { (Cosmetic) comboHat.getSelectedItem(), (Cosmetic) comboFace.getSelectedItem(), (Cosmetic) comboAddon.getSelectedItem(),
			(Cosmetic) comboGlasses.getSelectedItem() };
		for (Cosmetic c : cosmetics) {
		    if (c.filename.contentEquals(Vars.imgCharacterIconSizeFilename))
			continue;
		    sb.append(c.type);
		    sb.append("=");
		    sb.append(c.filename);
		    sb.append(";");
		}
		player.setWormItems(sb.toString());
	    }
	});
	btnBuy.setBounds(10, 526, 214, 23);
	contentPane.add(btnBuy);
	
		lblPrice = new JLabel("0");
		lblPrice.setForeground(Vars.colorCoin);
		lblPrice.setHorizontalAlignment(SwingConstants.CENTER);
		lblPrice.setFont(new Font("Tahoma", Font.BOLD, 27));
		lblPrice.setBounds(10, 482, 313, 33);
		lblPrice.setIcon(TheGame.scaleImage(Vars.imgCoin, lblPrice.getHeight(), lblPrice.getHeight()));
		contentPane.add(lblPrice);
	
	JButton btnChangePassword = new JButton("Change password");
	btnChangePassword.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    Vars.dialogChangePassword.setVisible(true);
		}
	});
	btnChangePassword.setBackground(new Color(204, 255, 255));
	btnChangePassword.setBounds(10, 448, 313, 23);
	contentPane.add(btnChangePassword);

	if (player != null) {
	    final JLabel lblConnectionStable = new JLabel("Connection stable");
	    lblConnectionStable.setForeground(Color.GREEN);
	    lblConnectionStable.setBounds(0, 0, 154, 23);
	    contentPane.add(lblConnectionStable);
	    Vars.listLabelConnectionInfo.add(lblConnectionStable);
	}

	avatar.setVisible(true);

	comboFace.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		setNewCosmetics(comboFace.getSelectedItem());
	    }
	});
	comboHat.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		setNewCosmetics(comboHat.getSelectedItem());
	    }
	});
	comboAddon.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		setNewCosmetics(comboAddon.getSelectedItem());
	    }
	});
	comboGlasses.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		setNewCosmetics(comboGlasses.getSelectedItem());
	    }
	});
    }

    public void changePrice(int delta) {
	Integer price = Integer.parseInt(lblPrice.getText());
	price += delta;
	lblPrice.setText(price.toString());
    }

    public static void reloadCosmetic() {
	final List<Cosmetic> list = new ArrayList<Cosmetic>();

	class CosmeticTypeGather {
	    /**
	     * Gathers all cosmetics of some type and saves it into list
	     * 
	     * @param type
	     *            type of cosmetic to download
	     * @throws MySQLException
	     * @throws IOException
	     */
	    public void gatherType(final CosmeticType type) throws MySQLException, IOException {
		list.clear();
		list.add(new Cosmetic("None", Vars.imgCharacterIconSizeFilename, type, 0));
		MySQLRespond rs = Vars.conn.executeSQL(String.format("SELECT name,filename, price FROM items WHERE type='%s'", type.toString()));
		while (rs.next()) {
		    final Cosmetic Cosmetic = new Cosmetic(rs.getString("name"), rs.getString("filename"), type, rs.getInteger("price"));
		    if (!Cosmetic.isDownloaded())
			Cosmetic.download();
		    list.add(Cosmetic);
		}
	    }
	}
	final CosmeticTypeGather ctg = new CosmeticTypeGather();

	try {
	    MySQLRespond rs = Vars.conn.executeSQL("SELECT filename, price FROM items WHERE type='COLOR'");
	    while (rs.next()) {
		if (rs.getString("filename").charAt(0) == 'B') {
		    priceBackground = rs.getInteger("price");
		} else if (rs.getString("filename").charAt(0) == 'C') {
		    priceCircle = rs.getInteger("price");
		}
	    }

	    ctg.gatherType(CosmeticType.HAT);
	    comboHat.setModel(new DefaultComboBoxModel<Cosmetic>(list.toArray(new Cosmetic[list.size()])));

	    ctg.gatherType(CosmeticType.FACE);
	    comboFace.setModel(new DefaultComboBoxModel<Cosmetic>(list.toArray(new Cosmetic[list.size()])));

	    ctg.gatherType(CosmeticType.ADDON);
	    comboAddon.setModel(new DefaultComboBoxModel<Cosmetic>(list.toArray(new Cosmetic[list.size()])));

	    ctg.gatherType(CosmeticType.GLASSES);
	    comboGlasses.setModel(new DefaultComboBoxModel<Cosmetic>(list.toArray(new Cosmetic[list.size()])));

	} catch (MySQLException | IOException e) {
	    e.printStackTrace();
	}

    }

    private static class Cosmetic {
	public final String filename;
	public final String name;
	public final int price;
	public final CosmeticType type;

	public Cosmetic(final String name, final String filename, CosmeticType type, final int price) {
	    this.filename = filename;
	    this.name = name;
	    this.type = type;
	    this.price = price;
	}

	/**
	 * Checks if this Cosmetic is saved locally
	 * 
	 * @return true if file with this name is found, false otherwise
	 */
	public boolean isDownloaded() {
	    return getFile().exists();
	}

	private File getFile() {
	    return new File(Resolution.ULTRA.getPath() + "character/" + filename + ".png");
	}

	/**
	 * Downloads file and saves it locally
	 * 
	 * @throws MySQLException
	 * @throws IOException
	 */
	public void download() throws MySQLException, IOException {
	    System.out.println("Downloading new cosmetic " + name);
	    final byte[] imgBytes = Vars.conn.executeSQL(String.format("SELECT file FROM items WHERE filename='%s'", filename)).getBytes("file");
	    Files.write(getFile().toPath(), imgBytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
	}

	public ImageIcon getImageIcon() {
	    return new ImageIcon(Vars.imgDataCharacterPrefix + filename + ".png");
	}

	@Override
	public String toString() {
	    return name;
	}
    }

    private void setNewCosmetics(final Object obj) {
	final Cosmetic selectedCosmetic = (Cosmetic) obj;
	if (selectedCosmetic == null)
	    return;
	avatar.setCosmetic(selectedCosmetic.getImageIcon(), selectedCosmetic.type);
	changePrice(selectedCosmetic.price);
    }
}
