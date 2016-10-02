package game.aho.lps;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class DialogOfflineGameStarter extends JDialog {

    private JPanel contentPane;

    /**
     * Create the frame.
     */
    public DialogOfflineGameStarter() {
	setModalityType(ModalityType.APPLICATION_MODAL);
    	setResizable(false);
	setBounds(100, 100, 295, 332);
	
	contentPane = new JPanel() {
	    @Override
	    public void paintComponent(Graphics g) {
		g.drawImage(Vars.imgBackground, 300, 300, null);
	    }
	};
	
	Vars.reloadIconsListeners.add(new Vars.NewIconListener() {

	    @Override
	    public void loadNewIcon() {
		setIconImage(Vars.imgFrameIcon);
		contentPane.repaint();
	    }
	});
	
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	setContentPane(contentPane);
	contentPane.setLayout(null);

	JLabel lblGamemode = new JLabel("Gamemode:");
	lblGamemode.setBounds(10, 11, 80, 14);
	contentPane.add(lblGamemode);

	final JComboBox<GameMode> comboBox = new JComboBox<GameMode>();
	comboBox.setModel((ComboBoxModel<GameMode>) new DefaultComboBoxModel<GameMode>(GameMode.values()));

	comboBox.setBounds(100, 8, 173, 17);
	contentPane.add(comboBox);

	JLabel lblDifficulty = new JLabel("Difficulty:");
	lblDifficulty.setBounds(10, 36, 80, 14);
	contentPane.add(lblDifficulty);

	final JSpinner spinner = new JSpinner();
	spinner.setModel(new SpinnerNumberModel(3, 1, 10, 1));
	spinner.setBounds(100, 36, 173, 17);
	contentPane.add(spinner);

	JLabel lblYourCash = new JLabel("Your cash: ");
	lblYourCash.setBounds(10, 61, 80, 14);
	contentPane.add(lblYourCash);

	JLabel lblEnemyCash = new JLabel("Enemy cash:");
	lblEnemyCash.setBounds(10, 122, 80, 14);
	contentPane.add(lblEnemyCash);

	final JSpinner spinner_1 = new JSpinner();
	spinner_1.setEnabled(false);
	spinner_1.setModel(new SpinnerNumberModel(new Long(5000), new Long(1), null, new Long(10000)));
	spinner_1.setBounds(100, 59, 173, 17);
	contentPane.add(spinner_1);

	final JCheckBox chckbxRandom = new JCheckBox("Random");
	chckbxRandom.setEnabled(false);
	chckbxRandom.setBounds(176, 82, 97, 23);
	contentPane.add(chckbxRandom);

	final JCheckBox chckbxDefault = new JCheckBox("Default");
	chckbxDefault.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		spinner_1.setEnabled(!chckbxDefault.isSelected());
		chckbxRandom.setSelected(false);
		chckbxRandom.setEnabled(!chckbxDefault.isSelected());
	    }
	});
	chckbxRandom.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		spinner_1.setEnabled(!chckbxRandom.isSelected());
		chckbxDefault.setSelected(false);
		chckbxDefault.setEnabled(!chckbxRandom.isSelected());
	    }
	});
	chckbxDefault.setSelected(true);
	chckbxDefault.setBounds(64, 82, 97, 23);
	contentPane.add(chckbxDefault);

	final JSpinner spinner_2 = new JSpinner();
	spinner_2.setEnabled(false);
	spinner_2.setModel(new SpinnerNumberModel(new Long(5000), new Long(1), null, new Long(10000)));
	spinner_2.setBounds(100, 122, 173, 17);
	contentPane.add(spinner_2);

	final JCheckBox checkBox = new JCheckBox("Random");
	checkBox.setEnabled(false);
	checkBox.setBounds(176, 145, 97, 23);
	contentPane.add(checkBox);

	final JCheckBox checkBox_1 = new JCheckBox("Default");
	checkBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		spinner_2.setEnabled(!checkBox.isSelected());
		checkBox_1.setSelected(false);
		checkBox_1.setEnabled(!checkBox.isSelected());
	    }
	});
	checkBox_1.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		spinner_2.setEnabled(!checkBox_1.isSelected());
		checkBox.setSelected(false);
		checkBox.setEnabled(!checkBox_1.isSelected());
	    }
	});
	checkBox_1.setSelected(true);
	checkBox_1.setBounds(64, 145, 97, 23);
	contentPane.add(checkBox_1);

	JButton btnPlay = new JButton("Play!");

	btnPlay.setFont(new Font("Tahoma", Font.BOLD, 31));
	btnPlay.setBounds(10, 240, 263, 55);
	contentPane.add(btnPlay);

	final JCheckBox chckbxLockpicksEnabled = new JCheckBox("Lockpicks enabled");
	chckbxLockpicksEnabled.setSelected(true);
	chckbxLockpicksEnabled.setBounds(10, 171, 263, 23);
	contentPane.add(chckbxLockpicksEnabled);

	JLabel lblNormal = new JLabel("Normal");
	lblNormal.setBounds(10, 201, 46, 14);
	contentPane.add(lblNormal);

	final JSpinner spinner_3 = new JSpinner();
	spinner_3.setModel(new SpinnerNumberModel(new Integer(7), new Integer(0), null, new Integer(1)));
	spinner_3.setBounds(64, 198, 46, 20);
	contentPane.add(spinner_3);

	JLabel lblPlatinum = new JLabel("Skeleton keys");
	lblPlatinum.setBounds(120, 201, 97, 14);
	contentPane.add(lblPlatinum);

	final JSpinner spinner_4 = new JSpinner();
	spinner_4.setModel(new SpinnerNumberModel(new Integer(2), new Integer(0), null, new Integer(1)));
	spinner_4.setBounds(227, 201, 46, 17);
	contentPane.add(spinner_4);

	chckbxLockpicksEnabled.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		spinner_3.setEnabled(chckbxLockpicksEnabled.isSelected());
		spinner_4.setEnabled(chckbxLockpicksEnabled.isSelected());
	    }
	});

	btnPlay.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		final TheGame game = new TheGame((int) spinner.getValue(), chckbxLockpicksEnabled.isSelected(), null, null, (GameMode) comboBox.getSelectedItem());
		if (!checkBox_1.isSelected()) {
		    if (checkBox.isSelected())
			game.cashEnemy.set(Vars.nextRandomLong(Long.MAX_VALUE / 100));
		    else
			game.cashEnemy.set((long) spinner_2.getValue());
		}

		if (!chckbxDefault.isSelected()) {
		    if (chckbxRandom.isSelected())
			game.cashPlayer.set(Vars.nextRandomLong(Long.MAX_VALUE / 100));
		    else
			game.cashPlayer.set((long) spinner_1.getValue());
		}

		if (chckbxLockpicksEnabled.isSelected()) {
		    game.lockpicksNormalLeft = (int) spinner_3.getValue();
		    game.lockpicksPlatinumLeft = (int) spinner_4.getValue();
		}

		game.countTarget();
		
		setVisible(false);
		game.setVisible(true);
	    }
	});
    }
}
