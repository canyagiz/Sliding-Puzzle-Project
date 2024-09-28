import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File; 
import java.io.IOException;
import javax.sound.sampled.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainMenu extends JFrame {
    private ImageIcon backgroundIcon;
    private static Clip backgroundMusicClip;
    private JPanel cards;
    private CardLayout cardLayout;
    private JSlider volumeControl; // music level slider
    static boolean buttonSoundEnabled = true;
    private int SIZE;
    
    public MainMenu() {
        setTitle("Puzzle Game");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Arka plan müziğini sadece ilk kez başlat (çünkü puzzledan menüye dönerken ses çakışıyor)
        if (backgroundMusicClip == null) {
            playBackgroundMusic("Sounds/Closing-Theme-Song.wav");
        }

        backgroundIcon = new ImageIcon("images/town.jpg");
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        // Add panels to card layout
        cards.add(createMainPanel(), "Main");
        cards.add(createSettingsPanel(), "Settings");
        cards.add(createCreditsPanel(), "Credits");
        cards.add(createPuzzleTypePanel(), "PuzzleType");
        cards.add(createSizePanel(),"Size");

        setContentPane(cards);
        cardLayout.show(cards, "Main");
    }

    private JPanel createSizePanel(){  // choose your puzzle matrix
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel sizePanel = createBackgroundPanel();
        sizePanel.setLayout(new GridBagLayout());
        JButton easyButton = new JButton("EASY");
        JButton mediumButton = new JButton("MEDIUM");
        JButton hardButton = new JButton("HARD");

        easyButton.setPreferredSize(new Dimension(180, 80));
        easyButton.setFont(new Font("Showcard Gothic", Font.BOLD, 30));
        easyButton.setForeground(Color.BLACK);
        easyButton.setBackground(Color.YELLOW);
        easyButton.setFocusPainted(false);
        easyButton.setBorderPainted(true);
        easyButton.setContentAreaFilled(true);
        Border thickBorder = BorderFactory.createLineBorder(Color.BLACK, 5);
        easyButton.setBorder(thickBorder);
        
        
        gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 60, 0); 
        sizePanel.add(easyButton,gbc);

        mediumButton.setPreferredSize(new Dimension(180, 80));
        mediumButton.setFont(new Font("Showcard Gothic", Font.BOLD, 30));
        mediumButton.setForeground(Color.BLACK);
        mediumButton.setBackground(Color.YELLOW);
        mediumButton.setFocusPainted(false);
        mediumButton.setBorderPainted(true);
        mediumButton.setContentAreaFilled(true);
        mediumButton.setBorder(thickBorder);

        gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 150, 60, 150); 
        sizePanel.add(mediumButton,gbc);

        hardButton.setPreferredSize(new Dimension(180, 80));
        hardButton.setFont(new Font("Showcard Gothic", Font.BOLD, 30));
        hardButton.setForeground(Color.BLACK);
        hardButton.setBackground(Color.YELLOW);
        hardButton.setFocusPainted(false);
        hardButton.setBorderPainted(true);
        hardButton.setContentAreaFilled(true);
        hardButton.setBorder(thickBorder);

        gbc.gridx = 0;
		gbc.gridy = 2;
        gbc.insets = new Insets(0, 150, 60, 150); 
        sizePanel.add(hardButton,gbc);

        easyButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
            	playSound("Sounds/buttonsound.wav");
                cardLayout.show(cards, "PuzzleType");
             SIZE = 3;
            }

        });

        mediumButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
            	playSound("Sounds/buttonsound.wav");
                cardLayout.show(cards, "PuzzleType");
             SIZE = 4;
            }
            
        });

        hardButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
            	playSound("Sounds/buttonsound.wav");
                cardLayout.show(cards, "PuzzleType");
             SIZE = 5;
            }
            
        });
        
        return sizePanel;
    }

    private JPanel createBackgroundPanel() {
        return new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundIcon != null) {
                    g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = createBackgroundPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        createMenu(mainPanel);
        return mainPanel;
    }

    private void createMenu(JPanel panel) {
        JLabel welcomeLabel = new JLabel("WELCOME TO THE PUZZLE GAME", JLabel.CENTER);
        welcomeLabel.setForeground(Color.WHITE); // Yazı rengini beyaz yap
        welcomeLabel.setFont(new Font("Showcard Gothic", Font.BOLD, 48)); // Yazı tipi ve boyutunu değiştir
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Yazıyı şeffaf yap
        welcomeLabel.setOpaque(false);

        // Gölge efekti eklemek için özel bir UI ayarı
        welcomeLabel.setUI(new javax.swing.plaf.basic.BasicLabelUI() {
           
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gölge efekti
                g2d.setColor(Color.BLACK);
                g2d.drawString(welcomeLabel.getText(), 4, 34); // Gölgenin pozisyonu
                super.paint(g, c);
            }
        });

        panel.add(Box.createRigidArea(new Dimension(0, 150)));
        panel.add(welcomeLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 50)));

        addButton("START", "Sounds/buttonsound.wav", panel);
        addButton("SETTINGS", "Sounds/buttonsound.wav", panel);
        addButton("CREDITS", "Sounds/buttonsound.wav", panel);
        addButton("QUIT", "Sounds/buttonsound.wav", panel);
    }
    

    private JPanel createButtonPanel(String buttonText, String imagePath, int imgWidth, int imgHeight) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false); // Set panel to be transparent

        ImageIcon icon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH));
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton button = new JButton(buttonText);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 50)); // puzzle names button size
        button.setFont(new Font("Showcard Gothic", Font.BOLD, 16));
        button.setForeground(Color.BLACK);
        button.setBackground(Color.YELLOW);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        Border thickBorder = BorderFactory.createLineBorder(Color.black, 5);
        button.setBorder(thickBorder);
        
        
    // Action listener for running puzzle
    button.addActionListener(e -> {
        playSound("Sounds/buttonsound.wav");
        switch (buttonText) {
            case "NUMBER PUZZLE":
                dispose();
                new SlidingPuzzle(SIZE);
                break;
            case "SPONGEBOB PUZZLE":
                dispose();
                new SlidingPuzzle("images/sungerbob.jpg",SIZE);
                break;
            case "PATRICK PUZZLE":
                dispose();
                new SlidingPuzzle("images/picture_seven.jpg",SIZE);
                break;
            case "SANDY PUZZLE":
                dispose();
                new SlidingPuzzle("images/sandy.jpg",SIZE);
                break;
            case "SQUIDWARD PUZZLE":
                dispose();
                new SlidingPuzzle("images/squidward.jpg",SIZE);
                break;
            default:
                dispose();
                new SlidingPuzzle("images/picture_six.jpg",SIZE);
                break;
        }
    });

        buttonPanel.add(imageLabel);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between image and button
        buttonPanel.add(button);

        return buttonPanel;
    }

    private JPanel createSettingsPanel() {
        JPanel settingsPanel = createBackgroundPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        JLabel settingsLabel = new JLabel("SET THE MUSIC VOLUME AND BUTTON SOUND", SwingConstants.CENTER);
        settingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsLabel.setFont(new Font("Showcard Gothic", Font.BOLD, 30));

        settingsPanel.add(Box.createVerticalGlue());
        settingsPanel.add(settingsLabel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Boşluk azaltıldı

        JPanel volumePanel = new JPanel();
        volumePanel.setLayout(new BoxLayout(volumePanel, BoxLayout.Y_AXIS));
        volumePanel.setOpaque(false);

        volumeControl = new JSlider(0, 100, 50);
        volumeControl.setMajorTickSpacing(10);
        volumeControl.setPaintTicks(true);
        volumeControl.setPaintLabels(true);
        volumeControl.setAlignmentX(Component.CENTER_ALIGNMENT);
        volumeControl.setMaximumSize(new Dimension(1400, 50)); // Slider boyutları

        // Slider'ın arka plan rengini ve kenar çizgisi
        volumeControl.setBackground(Color.YELLOW);
        volumeControl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        volumeControl.addChangeListener(e -> adjustVolume(volumeControl.getValue()));

        JLabel volumeLabel = new JLabel("Music Volume Control");
        volumeLabel.setFont(new Font("Showcard Gothic", Font.BOLD, 22));
        volumeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        volumePanel.add(volumeLabel);
        volumePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        volumePanel.add(volumeControl);

        settingsPanel.add(volumePanel);

        // Button sound toggle button with icon
        JButton toggleButtonSoundButton = new JButton();
        toggleButtonSoundButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleButtonSoundButton.setPreferredSize(new Dimension(50, 60)); // Buton boyutunu ikona göre ayarla
        toggleButtonSoundButton.setContentAreaFilled(false);
        toggleButtonSoundButton.setBorderPainted(false);
        toggleButtonSoundButton.setFocusPainted(false);

        // Initial icon setup
        updateButtonSoundIcon(toggleButtonSoundButton);

        toggleButtonSoundButton.addActionListener(e -> {
            buttonSoundEnabled = !buttonSoundEnabled;
            updateButtonSoundIcon(toggleButtonSoundButton);
        });

        // Create a panel to hold the button and the label
        JPanel buttonSoundPanel = new JPanel();
        buttonSoundPanel.setLayout(new BoxLayout(buttonSoundPanel, BoxLayout.Y_AXIS));
        buttonSoundPanel.setOpaque(false);

        // Create and add the label above the button
        JLabel buttonSoundLabel = new JLabel("Button Sound");
        buttonSoundLabel.setFont(new Font("Showcard Gothic", Font.BOLD, 22));
        buttonSoundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonSoundPanel.add(buttonSoundLabel);
        buttonSoundPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonSoundPanel.add(toggleButtonSoundButton);

        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Boşluk azaltıldı
        settingsPanel.add(buttonSoundPanel);
        settingsPanel.add(Box.createVerticalGlue());
        settingsPanel.add(createMainMenuButton("MAIN MENU"));

        return settingsPanel;
    }

    private void updateButtonSoundIcon(JButton button) {
        String iconPath = buttonSoundEnabled ? "images/soundon.png" : "images/soundoff.png";
        ImageIcon icon = new ImageIcon(new ImageIcon(iconPath).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        button.setIcon(icon);
    }

    // Ses seviyesini ayarlama metodu
    private void adjustVolume(int value) {
        if (backgroundMusicClip != null) {
            // Ses seviyesini logaritmik bir ölçekte ayarla
            float volume = (float) (Math.pow(value / 100f, 2));
            FloatControl gainControl = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume <= 0.0f ? 0.0001f : volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }

    private JPanel createCreditsPanel() {
        JPanel creditsPanel = createBackgroundPanel();
        creditsPanel.setLayout(new BoxLayout(creditsPanel, BoxLayout.Y_AXIS));

        JLabel creditsLabel1 = new JLabel("Puzzle Game is Developed by Yağız, İrem, Eda, Buğra, Atakan", SwingConstants.CENTER);
        creditsLabel1.setFont(new Font("Arial", Font.BOLD, 24));
        creditsLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel creditsLabel2 = new JLabel("Thanks to ChatGPT for helping and informations.", SwingConstants.CENTER);
        creditsLabel2.setFont(new Font("Arial", Font.BOLD, 22));
        creditsLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);

        creditsPanel.add(Box.createVerticalGlue());
        creditsPanel.add(creditsLabel1);
        creditsPanel.add(creditsLabel2);
        creditsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        creditsPanel.add(createMainMenuButton("MAIN MENU"));
        creditsPanel.add(Box.createVerticalGlue());

        return creditsPanel;
    }

    private JPanel createPuzzleTypePanel() {
        JPanel panel = createBackgroundPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalGlue());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 20, 10));
        buttonPanel.setOpaque(false); // Set panel to be transparent
        buttonPanel.add(createButtonPanel("NUMBER PUZZLE", "images/numbers.jpg", 200, 200));
        buttonPanel.add(createButtonPanel("SPONGEBOB PUZZLE", "images/sungerbob.jpg", 200, 200));
        buttonPanel.add(createButtonPanel("PATRICK PUZZLE", "images/picture_seven.jpg", 200, 200));
        buttonPanel.add(createButtonPanel("SANDY PUZZLE", "images/sandy.jpg", 200, 200));
        buttonPanel.add(createButtonPanel("SQUIDWARD PUZZLE", "images/squidward.jpg", 200, 200));
        buttonPanel.add(createButtonPanel("OTHER PUZZLE", "images/picture_six.jpg", 200, 200));
        panel.add(buttonPanel);
        panel.add(createMainMenuButton("MAIN MENU"));

        return panel;
    }
// --------------------------------SOME BUTTONS--------------------------------------
    private JButton createMainMenuButton(String text) {
        JButton mainMenuButton = new JButton(text);
        mainMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainMenuButton.setMaximumSize(new Dimension(150, 60));
        mainMenuButton.setPreferredSize(new Dimension(300, 50));
        mainMenuButton.setFont(new Font("Showcard Gothic", Font.BOLD, 18));
        mainMenuButton.setForeground(Color.BLACK);
        mainMenuButton.setBackground(Color.YELLOW);
        mainMenuButton.setFocusPainted(false);
        Border thickBorder = BorderFactory.createLineBorder(Color.BLACK, 5); // Kalın çerçeve ekler
        mainMenuButton.setBorder(thickBorder);
        
        mainMenuButton.addActionListener(e -> {
            if (buttonSoundEnabled) {
                playSound("Sounds/buttonsound.wav");
            }
            cardLayout.show(cards, "Main");
        });

        return mainMenuButton;
    }

    private void addButton(String text, String soundFile, JPanel panel) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(350, 60));
        button.setFont(new Font("Showcard Gothic", Font.BOLD, 20));
        button.setForeground(Color.BLACK);
        button.setBackground(Color.YELLOW);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        Border softBevel = BorderFactory.createSoftBevelBorder(BevelBorder.RAISED);
        Border thickBorder = BorderFactory.createLineBorder(Color.BLACK, 5); // Kalın çerçeve ekler
        button.setBorder(BorderFactory.createCompoundBorder(thickBorder, softBevel));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.CYAN);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.YELLOW);
            }
        });

        button.addActionListener(e -> {
            playSound(soundFile);
            switch (text) {
                case "START":
                    cardLayout.show(cards, "Size");
                    break;
                case "SETTINGS":
                    cardLayout.show(cards, "Settings");
                    break;
                case "CREDITS":
                    cardLayout.show(cards, "Credits");
                    break;
                case "MAIN MENU":
                    cardLayout.show(cards, "Main");
                    break;
                case "QUIT":
                    System.exit(0);
                    break;
            }
        });
        
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
    }
// ----------------------------------HATA AYIKLAMA----------------------------------------

    public static void playSound(String soundFileName) {
        if (!buttonSoundEnabled) return; // Eğer sesler kapalıysa, metottan çık

        try {
            File soundFile = new File(soundFileName);
            AudioInputStream audio = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            JOptionPane.showMessageDialog(null, "File cannot be played: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void playBackgroundMusic(String musicFilePath) {
        try {
            File musicFile = new File(musicFilePath);
            AudioInputStream audio = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audio);
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            JOptionPane.showMessageDialog(this, "Background music cannot be played: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new MainMenu().setVisible(true));
    }
}