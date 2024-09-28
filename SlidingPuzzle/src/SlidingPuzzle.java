import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Stack;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.Border;

public class SlidingPuzzle extends JFrame {
    private JPanel controlPanel = new JPanel();
	private JPanel puzzlePanel;
	private JPanel infoPanel = new JPanel();
	private JPanel contentPane;
	private JButton[][] buttons;
	private BubbleButton shuffleButton;
	private BubbleButton undoButton;
	private JLabel[][] labels;
	private JLabel moveLabel;
	private int moveCount = 0;
	private JLabel timeLabel;
	private long startTime;
	private JLabel originalImageLabel; // PUZZLE'da SAĞ ALTTA örnek foto olarak duracak.
	private BufferedImage backgroundImage;
	private BufferedImage congratBackground;
	private ImageIcon[] icon; // Resim parçalarını icon olarak tutar.
	private Stack<int[][]> lastMove;	//Its gonna be initalized after each puzzle creation.
	private String imagePath;
	private int emptyRow, emptyCol;
	private int SIZE;
	private int cellSize; 

	public class BubbleButton extends JButton{
		public BubbleButton(String buttonName){
        super(buttonName);
		setContentAreaFilled(false);
		}

		protected void paintComponent(Graphics g) {
			if (getModel().isArmed()) {
				g.setColor(getBackground().darker());
			} else {
				g.setColor(getBackground());
			}
			// Dairenin kendisini çiziyor
			g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
			super.paintComponent(g);
		}

		protected void paintBorder(Graphics g) {
			g.setColor(getForeground());
			// Dairenin kenar çizgisini çiziyor
			g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
		}
	}
	
	public SlidingPuzzle(int size) {
		this.SIZE =size;
		
		puzzlePanel = new JPanel(new GridLayout(SIZE,SIZE));
		cellSize = 550/SIZE; 

		setTitle("Button Puzzle");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 setExtendedState(JFrame.MAXIMIZED_BOTH);
	    setUndecorated(true);
		
	        
		controlPanel.setOpaque(false);
		controlPanel.setLayout(new GridBagLayout());

		 // Create the shuffle button and add an ActionListener
		 shuffleButton = new BubbleButton("SHUFFLE");
		 shuffleButton.setFont(new Font("Arial", Font.BOLD, 24));
		 shuffleButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Ortalamak için
		 shuffleButton.setPreferredSize(new Dimension(150, 150));
		 shuffleButton.addActionListener(new ActionListener() {
			 @Override
			 public void actionPerformed(ActionEvent e) {
				 shuffleButtons();
				 startTime = System.currentTimeMillis();
			 }
		 });
		 
		undoButton = new BubbleButton("UNDO");
		undoButton.setFont(new Font("Arial", Font.BOLD, 24));
		undoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		undoButton.setPreferredSize(new Dimension(150, 150)); 
		undoButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				undoButtons();
			}
		});

		

		GridBagConstraints gbc1 = new GridBagConstraints();

		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.insets = new Insets(0, 100, 90, 0); 
		controlPanel.add(shuffleButton,gbc1);

        gbc1.gridx = 0;
        gbc1.gridy = 1;
		gbc1.insets = new Insets(0, 100, 0, 0); 
        controlPanel.add(undoButton,gbc1);

		
		infoPanel.setOpaque(false);
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

		timeLabel = new JLabel("Elapsed Time: 0:00");
		timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		timeLabel.setFont(new Font("Arial", Font.BOLD, 30));
		timeLabel.setForeground(Color.BLACK);

		moveLabel = new JLabel("Moves: " + moveCount);
		moveLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		moveLabel.setFont(new Font("Arial", Font.BOLD, 30));
		moveLabel.setForeground(Color.BLACK);

		infoPanel.add(timeLabel); 
		infoPanel.add(Box.createRigidArea(new Dimension(0, 60)));
        infoPanel.add(moveLabel);
		
		startTime = System.currentTimeMillis(); // Oyun başlangıç zamanını kaydet
		startTimer(); // Timer'ı başlat
		buttons = new JButton[SIZE][SIZE];
        labels = new JLabel[SIZE][SIZE];
		createPuzzle();

		
		try {
            backgroundImage = ImageIO.read(new File("images/mainBackground.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }

		 contentPane = new JPanel() { // JFrame deki ana içerik alanıdır.
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                g.setFont(new Font("Showcard Gothic", Font.BOLD, 50));
                g.setColor(Color.BLACK);
                g.drawString("Puzzle Game", getWidth()/2 - 150, 90);
            }
        };

		contentPane.setLayout(new GridBagLayout());

		GridBagConstraints gbc2 = new GridBagConstraints();

		gbc2.gridx = 0;
		gbc2.gridy = 0;
		gbc2.insets = new Insets(0, 0, 0, 150); 
		contentPane.add(controlPanel,gbc2);

        gbc2.gridx = 1;
        gbc2.gridy = 0;
		gbc2.insets = new Insets(50, 0, 0, 60); 
        contentPane.add(puzzlePanel, gbc2);

		gbc2.gridx = 2;
        gbc2.gridy = 0;
		gbc2.insets = new Insets(0, 0, 200, 0); 
        contentPane.add(infoPanel, gbc2);
		
     // ------PUZZLE'DAN MENÜYE GEÇİŞ------
    	JButton mainMenuButton = new JButton("BACK TO MENU");
    	mainMenuButton.setFont(new Font("Showcard Gothic", Font.BOLD, 24));
    	mainMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    	mainMenuButton.setPreferredSize(new Dimension(300, 50));
    	mainMenuButton.setBackground(Color.YELLOW);
    	mainMenuButton.setForeground(Color.BLACK);
    	mainMenuButton.setFocusPainted(false); // düğmedeki kenarlığı kaldırır
    	Border thickBorder = BorderFactory.createLineBorder(Color.BLACK, 5); // Kalın çerçeve ekler
    	mainMenuButton.setBorder(thickBorder);

    	 mainMenuButton.addActionListener(e -> {
             if (MainMenu.buttonSoundEnabled) {
                 playSlideSound("Sounds/buttonsound.wav");
             }
             dispose();
             new MainMenu().setVisible(true);
         });
    	 
    	 
    	GridBagConstraints gbc3 = new GridBagConstraints();
    	gbc3.gridx = -1;
    	gbc3.gridy = 3; // Butonu kontrol panelinin altına yerleştirmek için uygun bir y koordinatı
    	gbc3.insets = new Insets(20, 0, 0, 0); // 
    	contentPane.add(mainMenuButton, gbc3);

        setContentPane(contentPane);
	}


	public SlidingPuzzle(String imagePath, int SIZE)  {

		this.SIZE =SIZE;
		this.imagePath = imagePath;

		puzzlePanel = new JPanel(new GridLayout(SIZE,SIZE));
		icon = new ImageIcon[SIZE*SIZE];
		cellSize = 550/SIZE; 

		// It shows the full image on the right, to make it easier to play
		BufferedImage originalBufferedImage = null;
		try {
			originalBufferedImage = ImageIO.read(new File(imagePath));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Image scaledOriginalImage = originalBufferedImage.getScaledInstance(250,250,Image.SCALE_SMOOTH);
		ImageIcon originalIcon = new ImageIcon(scaledOriginalImage);
		originalImageLabel = new JLabel(originalIcon);
		originalImageLabel.setPreferredSize(new Dimension(250,250));

		setTitle("Button Puzzle");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	    setUndecorated(true);
		
		controlPanel.setOpaque(false);
		controlPanel.setLayout(new GridBagLayout());

		 // Create the shuffle button and add an ActionListener
		 shuffleButton = new BubbleButton("SHUFFLE");
		 shuffleButton.setFont(new Font("Arial", Font.BOLD, 24));
		 shuffleButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Ortalamak için
		 shuffleButton.setPreferredSize(new Dimension(150, 150));
		 shuffleButton.addActionListener(new ActionListener() {
			 @Override
			 public void actionPerformed(ActionEvent e) {
				 shuffleButtons();
				 startTime = System.currentTimeMillis();
			 }
		 });
		 
		undoButton = new BubbleButton("UNDO");
		undoButton.setFont(new Font("Arial", Font.BOLD, 24));
		undoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		undoButton.setPreferredSize(new Dimension(150, 150)); 
		undoButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				undoButtons();
			}
		});
		
	
		// to position the elements inside the controlpanel
		GridBagConstraints gbc1 = new GridBagConstraints();

		gbc1.gridx = 0;
		gbc1.gridy = 0;
		gbc1.insets = new Insets(0, 100, 90, 0); 
		controlPanel.add(shuffleButton,gbc1);

        gbc1.gridx = 0;
        gbc1.gridy = 1;
		gbc1.insets = new Insets(0, 100, 0, 0); 
        controlPanel.add(undoButton,gbc1);

	
		
		infoPanel.setOpaque(false);
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

		timeLabel = new JLabel("Elapsed Time: 0:00");
		timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		timeLabel.setFont(new Font("Arial", Font.BOLD, 30));
		timeLabel.setForeground(Color.BLACK);

		moveLabel = new JLabel("Moves: " + moveCount);
		moveLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		moveLabel.setFont(new Font("Arial", Font.BOLD, 30));
		moveLabel.setForeground(Color.BLACK);

		originalImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		infoPanel.add(originalImageLabel);
		infoPanel.add(Box.createRigidArea(new Dimension(0, 60)));
		infoPanel.add(timeLabel); 
		infoPanel.add(Box.createRigidArea(new Dimension(0, 60)));
        infoPanel.add(moveLabel);
		
		
		startTime = System.currentTimeMillis(); // Save the game start time
		startTimer(); // start the timer
		buttons = new JButton[SIZE][SIZE];
        labels = new JLabel[SIZE][SIZE];
		createPuzzle();

		
		try {
            backgroundImage = ImageIO.read(new File("images/mainBackground.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }

		 contentPane = new JPanel() { // It's the main content area in JFrame
		 
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
                g.setFont(new Font("Showcard Gothic", Font.BOLD, 50));
                g.setColor(Color.BLACK);
                g.drawString("Puzzle Game", getWidth()/2 - 150, 90);
            }
        };

		contentPane.setLayout(new GridBagLayout());

		// to position the elements inside the contentPane
		GridBagConstraints gbc2 = new GridBagConstraints();

		gbc2.gridx = 0;
		gbc2.gridy = 0;
		gbc2.insets = new Insets(0, 0, 0, 150); 
		contentPane.add(controlPanel,gbc2);

        gbc2.gridx = 1;
        gbc2.gridy = 0;
		gbc2.insets = new Insets(50, 0, 0, 60); 
        contentPane.add(puzzlePanel, gbc2);

		gbc2.gridx = 2;
        gbc2.gridy = 0;
		gbc2.insets = new Insets(0, 0, 200, 0); 
        contentPane.add(infoPanel, gbc2);

        setContentPane(contentPane);
        

	// ------PUZZLE'DAN MENÜYE GEÇİŞ------
	JButton mainMenuButton = new JButton("BACK TO MENU");
	mainMenuButton.setFont(new Font("Showcard Gothic", Font.BOLD, 24));
	mainMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	mainMenuButton.setPreferredSize(new Dimension(300, 50));
	mainMenuButton.setBackground(Color.YELLOW);
	mainMenuButton.setForeground(Color.BLACK);
	mainMenuButton.setFocusPainted(false); // düğmedeki kenarlığı kaldırır
	Border thickBorder = BorderFactory.createLineBorder(Color.BLACK, 5); // Kalın çerçeve ekler
	mainMenuButton.setBorder(thickBorder);
   
	
	 mainMenuButton.addActionListener(e -> {
	        if (MainMenu.buttonSoundEnabled) {
	            playSlideSound("Sounds/buttonsound.wav");
	        }
	        dispose();
	        new MainMenu().setVisible(true);
	    });

	GridBagConstraints gbc3 = new GridBagConstraints();
	gbc3.gridx = -1;
	gbc3.gridy = 3; // Butonu kontrol panelinin altına yerleştirmek için uygun bir y koordinatı
	gbc3.insets = new Insets(20, 0, 0, 0); // 
	contentPane.add(mainMenuButton, gbc3);
	}
	

	
	private void updateElapsedTime() {

        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000; // Calculates the elapsed time in milliseconds and then converts it to seconds
        long minutes = elapsedTime / 60; // Calculate the minutes
        long seconds = elapsedTime % 60; // Calculate the seconds

        timeLabel.setText("Elapsed Time: " + minutes + ":" + String.format("%02d", seconds)); // "%02d", this expression adds a leading zero when seconds are single-digit

    }



	private void startTimer() {
		Timer timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateElapsedTime(); // Update elapsed time every second
			}
		});
		timer.start(); // starts timer
	} 

// ----------------------------CREATE PUZZLE METHOD,SOME BUTTONS-------------------------------
	
    public void createPuzzle(){
    	if(imagePath != null) {
    		
    		try{
            	BufferedImage image = ImageIO.read(new File(imagePath)); // Inserts the image file.

                int pieceWidth = image.getWidth() / SIZE; // Sets the width of the part
                
                int pieceHeight = image.getHeight() / SIZE; // Sets the height of the part
                
                int count = 0;
                // Divides the image into pieces and puts them into the array.
    					for(int i =0;i<SIZE;i++){  
    						for(int j = 0;j<SIZE;j++){
                                BufferedImage imagePiece;
                              
                                if(i != SIZE - 1 || j != SIZE - 1){
                                // Divides the picture
                                imagePiece = image.getSubimage(j*pieceWidth, i*pieceHeight,pieceWidth,pieceHeight);

                                // Scales the image.
    		                    Image scaledPiece = imagePiece.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH);
                                
                                // Turns the pieces into icons.
    		                    icon[count] = new ImageIcon(scaledPiece);
                                count++;
                                
                                }
                            }
                        }        
            } catch(IOException exception){
                exception.printStackTrace();
            
    	}
        }
    	Color colourOfLabels;
    	if (imagePath != null) {
    		colourOfLabels = new Color(0,0,0,0);
    	}else {
    		colourOfLabels = Color.BLACK;
    	}
    	
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                buttons[i][j] = new JButton();
                if (!(i == SIZE - 1 && j == SIZE - 1)) {
                    labels[i][j] = new JLabel(String.valueOf(i * SIZE + j + 1));
                    labels[i][j].setForeground(colourOfLabels);
                    
                    // Center the text horizontally and vertically within the label
                    labels[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                    labels[i][j].setVerticalAlignment(SwingConstants.CENTER);
                    
                    buttons[i][j].setLayout(new BorderLayout()); // Set layout to BorderLayout
                    buttons[i][j].add(labels[i][j], BorderLayout.CENTER); // Add label to the center of the button
                } else {
                    labels[i][j] = new JLabel(" ");
                    buttons[i][j].add(labels[i][j]);
                }
                buttons[i][j].setPreferredSize(new Dimension(cellSize, cellSize));
                buttons[i][j].addActionListener(new ButtonListener());
                
                buttons[i][j].setBackground(Color.cyan); // puzzle rengi
                // Butonların kenarına siyah çerçeve ekle
                buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                labels[i][j].setFont(new Font("Arial", Font.BOLD, 24)); // numbers font

                if (imagePath != null) {
                    buttons[i][j].setIcon(icon[count]);
                }
                puzzlePanel.add(buttons[i][j]);
                count++;
            }
        }
        
		
		setVisible(true);
		emptyRow = SIZE - 1;
		emptyCol = SIZE - 1;
		buttons[emptyRow][emptyCol].setIcon(null);
		buttons[emptyRow][emptyCol].setBackground(new Color(255,202,213));
               
			shuffleButtons(); // puzzle must initialized as shuffled
			
			lastMove = new Stack();
			int[][] initialMove = new int[2][2];
			initialMove[0][0] = emptyRow;
			initialMove[0][1] = emptyCol;
			initialMove[1][0] = emptyRow;
			initialMove[1][1] = emptyCol;
			lastMove.push(initialMove);
			
			setVisible(true);
			
			
    }

    private void shakeButton(JButton button) {
        final int delay = 15; // delay between position changes
        final int duration = 200; // total duration of the shake animation
        final int amplitude = 3; // button moves at most 3 pixels 
        playSlideSound("Sounds/invalidmove.wav");

        Border originalBorder = button.getBorder(); // Save the original border

        // Check if the shake animation is already running
        Boolean isShaking = (Boolean) button.getClientProperty("isShaking");
        if (isShaking != null && isShaking) {
            return; // If already shaking, exit the method
        }

        // titreme animasyonunun başladığını işaretler.
        //Aynı anda birden fazla animasyonun çalışmasını engeller.
        button.putClientProperty("isShaking", true);

        Timer timer = new Timer(delay, new ActionListener() {
            int timeElapsed = 0;
            int offset = 0; // Butonun her adımda ne kadar hareket ediyor(sinüs)

            public void actionPerformed(ActionEvent e) {
                // Reset the previous offset
                button.setLocation(button.getX() - offset, button.getY());

                // Calculate the new offset
                offset = (int) (amplitude * Math.sin(timeElapsed / (double) delay));
                button.setLocation(button.getX() + offset, button.getY());

                // Set red border for shaking effect
                button.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

                timeElapsed += delay;
                if (timeElapsed >= duration) {
                    ((Timer) e.getSource()).stop();
                    button.setLocation(button.getX() - offset, button.getY()); // Reset position
                    button.setBorder(originalBorder); // Reset to original border
                    button.putClientProperty("isShaking", false); // buton titreme animasyonu içinde deği
                }
            }
        });

        timer.start();
    }
     
 
    protected class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton clickedButton = (JButton) e.getSource();

            // Tıklanan butonun konumunu bul
            int clickedRow = -1;
            int clickedCol = -1;
            
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (buttons[i][j] == clickedButton) {
                        clickedRow = i;
                        clickedCol = j;
                        break;
                    }
                }
            }

            // Check if the move is valid
            if (isValidMove(clickedRow, clickedCol)) {
                // Tıklanan buton ile boş butonun yerini değiştir
                swapButtons(clickedRow, clickedCol);
            } else {
                // Shake the button if the move is invalid
                shakeButton(clickedButton);
            }

            if (isSolved()) {
                    // Puzzle çözüldüğünde şarkıyı çal
                    playSlideSound("Sounds/congrats.wav");
            	
                  //create the congrats frame, then 2 panels Panel A for label, Panel B for restart/ close buttons
                
				  JFrame congratsFrame = new JFrame();
				  congratsFrame.setSize(600,300);
				  congratsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				  congratsFrame.setLocationRelativeTo(puzzlePanel);
				  
				  try {
					congratBackground = ImageIO.read(new File("images/congratBackground.jpg"));
				} catch (Exception exception) {
					exception.printStackTrace();
				}

				JPanel congratBackgroundPanel = new JPanel(){
					@Override
							protected void paintComponent(Graphics g) {
								super.paintComponent(g);
								if (congratBackground != null) {
									g.drawImage(congratBackground, 0, 0, getWidth(), getHeight(), this);
								}
								g.setFont(new Font("Showcard Gothic", Font.BOLD, 15));
								g.setColor(Color.BLACK);
								
							}
				};

				congratBackgroundPanel.setLayout(new BoxLayout(congratBackgroundPanel, BoxLayout.Y_AXIS));
				

				  //The label carrier panel
				  JPanel congratsPanel = new JPanel();
				  congratsPanel.setLayout(new BoxLayout(congratsPanel, BoxLayout.Y_AXIS));
				  congratsPanel.setOpaque(false);
				  
				  JLabel congratsLabel1 = new JLabel("Congratulations, you've solved the puzzle.");
				  congratsLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
				  congratsLabel1.setFont(new Font("Arial", Font.BOLD, 24));
				  JLabel congratsLabel2 = new JLabel("Please choose one option below.");
				  congratsLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
				  congratsLabel2.setFont(new Font("Arial", Font.BOLD, 24));

				  //The button carrier panel
				  JPanel congratsButtonPanel = new JPanel();
				  congratsButtonPanel.setLayout(new FlowLayout());
				  congratsButtonPanel.setOpaque(false);

				  //restart button
				  BubbleButton restartButton = new BubbleButton("RESTART");
				  restartButton.setPreferredSize(new Dimension(100, 50));
				  restartButton.addActionListener(new ActionListener() {
					  public void actionPerformed(ActionEvent e) {
						  puzzlePanel.removeAll();
						  moveCount = 0;
						  moveLabel.setText("Moves: " + moveCount);
						  createPuzzle();
						  startTime = System.currentTimeMillis();
						  congratsFrame.setVisible(false);
						  congratsFrame.dispose();
					  }
				  }); // butona basınca puzzle'ı yeniliyor
				  
				  BubbleButton quitButton = new BubbleButton("QUIT");
				  quitButton.setPreferredSize(new Dimension(100, 50));
				  quitButton.addActionListener(new ActionListener() {
					  public void actionPerformed(ActionEvent e) {
						  System.exit(0);
					  }
				  }); // butona basınca ekranı kapatıyor
  
				//add buttons to buttonPanel
				congratsButtonPanel.add(restartButton);
				congratsButtonPanel.add(quitButton);
				
				//add congratsLabel to congratsPanel
				congratsPanel.add(Box.createRigidArea(new Dimension(0, 40)));
				congratsPanel.add(congratsLabel1);
				congratsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
				congratsPanel.add(congratsLabel2);

				congratBackgroundPanel.add(congratsPanel);
				congratBackgroundPanel.add(Box.createRigidArea(new Dimension(0, 50)));
				congratBackgroundPanel.add(congratsButtonPanel);

				//add congratsPanel to congratsFrame
				congratsFrame.add(congratBackgroundPanel);
				congratsFrame.setVisible(true);


            }
        }
    }
	

	protected void changeButtons(int clickedRow, int clickedCol) {
		JButton temp = buttons[clickedRow][clickedCol];
		
		buttons[clickedRow][clickedCol] = buttons[emptyRow][emptyCol];
		buttons[emptyRow][emptyCol] = temp;
		
		JLabel tempL = labels[clickedRow][clickedCol];
		labels[clickedRow][clickedCol] = labels[emptyRow][emptyCol];
		labels[emptyRow][emptyCol] = tempL;
	

		// Panelin içeriğini güncelle
		puzzlePanel.removeAll();
		
		for (JButton[] row : buttons) {
			for (JButton button : row) {
				puzzlePanel.add(button);
			}
		}

		// Yeniden çiz
		puzzlePanel.revalidate();
		puzzlePanel.repaint();
	}
	
	//Overloaded for undo
	protected void changeButtons(int prevEmptyRow, int prevEmptyCol, int clickedRow, int clickedCol) {
		JButton temp = buttons[clickedRow][clickedCol];
		
		buttons[clickedRow][clickedCol] = buttons[prevEmptyRow][prevEmptyCol];
		buttons[prevEmptyRow][prevEmptyCol] = temp;
		
		JLabel tempL = labels[clickedRow][clickedCol];
		labels[clickedRow][clickedCol] = labels[prevEmptyRow][prevEmptyCol];
		labels[prevEmptyRow][prevEmptyCol] = tempL;
	

		// Panelin içeriğini güncelle
		puzzlePanel.removeAll();
		
		for (JButton[] row : buttons) {
			for (JButton button : row) {
				puzzlePanel.add(button);
			}
		}

		// Yeniden çiz
		puzzlePanel.revalidate();
		puzzlePanel.repaint();
		
		emptyRow = prevEmptyRow;
		emptyCol = prevEmptyCol;
		
		
	}
	
	 	protected void undoButtons() {
	 		
			try {
				int[][] lastMoveArr = lastMove.pop();
				int previousEmptyRow =  lastMoveArr[0][0] ;
				int previousEmptyCol =  lastMoveArr[0][1];
				int clickedRow = lastMoveArr[1][0];
				int clickedCol = lastMoveArr[1][1];
				changeButtons(previousEmptyRow ,previousEmptyCol ,clickedRow, clickedCol);
			}
			catch(Exception EmptyStackException) {
				
			}
			
			
		}

   // Butonların yerini değiştirme metodu
	protected void swapButtons(int clickedRow, int clickedCol) {
		// Tıklanan butonun boş butonun yanında olup olmadığını kontrol et
		if (isValidMove(clickedRow, clickedCol)) {
			
			//since clicked button is valid in aspect of move it, the undo stack must be updated.
			playSlideSound("Sounds/slidesound.wav");
			
			
			moveCount++;
			moveLabel.setText("Moves: " + moveCount);
			// Tıklanan buton ile boş butonun yerini değiştir
				
			changeButtons(clickedRow, clickedCol);
				
			int[][] initialMove = new int[2][2];
			initialMove[0][0] = emptyRow;
			initialMove[0][1] = emptyCol;
			initialMove[1][0] = clickedRow;
			initialMove[1][1] = clickedCol;
			lastMove.push(initialMove);
			
			// Boş butonun konumunu güncelle
			emptyRow = clickedRow;
			emptyCol = clickedCol;
			
			
		}
	}
    
    public boolean isValidMove(int clickedRow, int clickedCol){
		if ((Math.abs(clickedRow - emptyRow) == 1 && clickedCol == emptyCol)
				|| (Math.abs(clickedCol - emptyCol) == 1 && clickedRow == emptyRow)) {
			return true;
			
		} else {
			return false;
		}

	}

    protected void shuffleButtons() {
	    Random random = new Random();
	    
	    do {
	    for (int i = 0; i < SIZE * SIZE; i++) {
	        int j = random.nextInt(SIZE * SIZE);

	        // Convert the one-dimensional indices to two-dimensional
	        int rowI = i / SIZE; //0 0 0   1 1 1   2 2 2 
	        int colI = i % SIZE; // 0 1 2	0 1 2	0 1 2  	
	        int rowJ = j / SIZE;
	        int colJ = j % SIZE;

	        // Swap the buttons in the array
	        JButton temp = buttons[rowI][colI];
	        buttons[rowI][colI] = buttons[rowJ][colJ];
	        buttons[rowJ][colJ] = temp;
	        
	    	
			JLabel tempL = labels[rowI][colI];
			labels[rowI][colI] = labels[rowJ][colJ];
			labels[rowJ][colJ] = tempL;
	    moveCount = 0;
		moveLabel.setText("Moves: " + moveCount);
	        
	    }
	    }while(!isSolvable());

	    // Update the emptyRow and emptyCol variables if the empty button is shuffled
	    
        for (int i = 0; i < SIZE; i++) {
	        for (int j = 0; j < SIZE; j++) {
	            if (labels[i][j].getText().equals(" ")) {
	                emptyRow = i;
	                emptyCol = j;
	            }
	        }
	    }
		
		

	    // Refresh the panel to reflect the changes
	    puzzlePanel.removeAll();
	    for (int i = 0; i < SIZE; i++) {
	        for (int j = 0; j < SIZE; j++) {
	            puzzlePanel.add(buttons[i][j]);
	        }
	    }
	    puzzlePanel.revalidate();
	    puzzlePanel.repaint();
	    try{lastMove.clear();} //after shuffling, undo stack must be empty 
	    catch(Exception e){
	    	
	    }
    }
    

    public boolean isSolvable() {
		int inversion = 0;
		for (int i = 0; i < SIZE * SIZE - 1; i++) { // 0 <= i <= 7
			int rowI = i / SIZE; // 0 0 0 1 1 1 2 2 2
			int colI = i % SIZE; // 0 1 2 0 1 2 0 1 2
			int buttonIvalue;

			if (labels[rowI][colI].getText().equals(" ")) {
				continue;
			}
			else {
				 buttonIvalue = Integer.parseInt(labels[rowI][colI].getText());
			}
			for (int j = i + 1; j < SIZE * SIZE; j++) { // 1 =< =<8
				int rowJ = j / SIZE; // 0 0 0 1 1 1 2 2 2
				int colJ = j % SIZE; // 0 1 2 0 1 2 0 1 2
				int buttonJvalue;

				if (labels[rowJ][colJ].getText().equals(" ")) {
					continue;
				}
				else {
					 buttonJvalue = Integer.parseInt(labels[rowJ][colJ].getText());
				}

				if (buttonIvalue > buttonJvalue) {
					inversion++;
				}
			}
		}
		return (inversion % 2 == 0);
	}

	protected boolean isSolved() { // Butonların üstündeki textleri 0'dan 9'a sıralı mı diye kontrol eder.
	String[][] controlArray = new String[SIZE][SIZE];
	int value = 1;
	for (int row = 0; row < SIZE; row++) {
		for (int col = 0; col < SIZE; col++) {
			controlArray[row][col] = String.valueOf(value);
			
			if(row == SIZE-1 && col == SIZE-1) {
				if(!labels[row][col].getText().equals(" ")) {
				return false;}
			}
			else if(!controlArray[row][col].equals(labels[row][col].getText())){
               return false;
			}
			value++; 
		}
	}
	return true;
}
	
	
	private void playSlideSound(String soundFileName) {
	    try {
	        File soundFile = new File(soundFileName);
	        AudioInputStream audio = AudioSystem.getAudioInputStream(soundFile);
	        Clip clip = AudioSystem.getClip();
	        clip.open(audio);
	        clip.start();
	    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
	        JOptionPane.showMessageDialog(this, "Dosya oynatılamıyor: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
	    }
	}
}