import javax.swing.*;
import java.awt.Font;

import javax.sound.sampled.*;
import java.io.File;
import java.awt.Color;

public class Pomodoro {

    private int secondsLeft;
    private int minutes;
    private int seconds;
    private int status; // 1, 3, or 5: pomodoro |  2, 4, or 6: break |  7: long break
    private Timer timer;

    private JButton startButton;
    private JButton resumeButton;
    private JButton pauseButton;
    private JButton resetButton;

    private JLabel timeLabel;
    private JLabel statusLabel;

    private JPanel mainPanel;
    private JPanel buttonsPanel;
    private JPanel dropDownPanel;

    private JComboBox <Integer> focusBox;
    private JComboBox <Integer> breakBox;
    private JComboBox <Integer> longBox;

    private File chime = new File("chime-audio.wav");
    private File notif = new File("notif-sound.wav");

    public Pomodoro(){
        
        //********* Setting up the display **********
        JFrame frame = new JFrame("Pomodoro Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        //------- status label --------
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Georgia", Font.BOLD, 20));
        statusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        mainPanel.add(statusLabel);

        //--------- time label ---------
        timeLabel = new JLabel("10:00");
        timeLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        timeLabel.setFont(new Font("Georgia", Font.PLAIN, 80));
        mainPanel.add(timeLabel);

        //**************** Buttons ********************
        startButton = new JButton("Start");
        resumeButton = new JButton("Resume");
        pauseButton = new JButton("Pause");
        resetButton = new JButton("Reset");

        startButton.addActionListener(e ->{ 
            startPomodoro();
            startButton.setVisible(false);
            timer.start();
        });

        resumeButton.addActionListener(e->{
            pauseButton.setVisible(true);
            resumeButton.setVisible(false);
            timer.start();
        });

        pauseButton.addActionListener(e ->{ //pause button function
            timer.stop();
            pauseButton.setVisible(false);
            resumeButton.setVisible(true);
        });

        resetButton.addActionListener(e -> 
            reset()
        );

        //-------- buttons panel ----------
        buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        buttonsPanel.add(startButton);
        buttonsPanel.add(resumeButton);
        buttonsPanel.add(pauseButton);
        buttonsPanel.add(resetButton);

        resetButton.setVisible(false);
        resumeButton.setVisible(false);
        pauseButton.setVisible(false);

        mainPanel.add(buttonsPanel);
        

        //************ Drop down menus ****************
        Integer [] focusOptions = {10, 15, 20, 25, 30, 45, 60};
        focusBox = new JComboBox<>(focusOptions);
        focusBox.addActionListener(e-> reset());

        Integer [] breakOptions = {3, 5, 7, 10, 15, 20};
        breakBox = new JComboBox<>(breakOptions);
        breakBox.addActionListener(e-> reset());

        Integer [] longOptions = {10, 15, 20, 25, 30};
        longBox = new JComboBox<>(longOptions);
        longBox.addActionListener(e-> reset());

        //---------- drop down panels -----------
        JPanel focusPanel = new JPanel();
        focusPanel.setLayout(new BoxLayout(focusPanel, BoxLayout.Y_AXIS));
        JLabel focusLabel = new JLabel("Focus");
        focusPanel.add(focusLabel);
        focusPanel.add(focusBox);

        JPanel breakPanel = new JPanel();
        breakPanel.setLayout(new BoxLayout(breakPanel, BoxLayout.Y_AXIS));
        JLabel breakLabel = new JLabel("Break");
        breakPanel.add(breakLabel);
        breakPanel.add(breakBox);

        JPanel longPanel = new JPanel();
        longPanel.setLayout(new BoxLayout(longPanel, BoxLayout.Y_AXIS));
        JLabel longLabel = new JLabel("Long break");
        longPanel.add(longLabel);
        longPanel.add(longBox);

        dropDownPanel = new JPanel();
        dropDownPanel.setLayout(new BoxLayout(dropDownPanel, BoxLayout.X_AXIS));

        dropDownPanel.add(focusPanel);
        dropDownPanel.add(breakPanel);
        dropDownPanel.add(longPanel);

        mainPanel.add (dropDownPanel);

        secondsLeft = (Integer) focusBox.getSelectedItem() * 60;
        status = 0;
        

        frame.setSize(400, 240);
        mainPanel.setBackground(Color.decode("#848d94"));
        frame.add(mainPanel);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //******************* Timer *******************
        timer = new Timer(1000, event ->{
            displayTime();
            secondsLeft--;
            if (secondsLeft == 0 && status == 7)   //if long
                startLong();
            else if (secondsLeft == 0 && status % 2 == 0)   //if break
                startPomodoro();
            else if (secondsLeft == 0 && status % 2 == 1) //if pomodoro
                startBreak();
        });
    }

    public void startPomodoro(){
        status++;
        statusLabel.setText("Pomodoro");
        secondsLeft = (Integer) focusBox.getSelectedItem() * 60;
        displayTime();
        playSound(chime);
        mainPanel.setBackground(Color.decode("#9d6161"));
        pauseButton.setVisible(true);
        resetButton.setVisible(true);
    }

    public void startBreak(){
        status++;
        statusLabel.setText("Break");
        playSound(notif);
        secondsLeft = (Integer) breakBox.getSelectedItem() * 60;
        mainPanel.setBackground(Color.decode("#818a80"));
        displayTime();
    }

    public void startLong(){
        statusLabel.setText("Long Break");
        secondsLeft = (Integer) longBox.getSelectedItem() * 60;
        mainPanel.setBackground(Color.decode("#8b93a3"));
        status = 1;
        displayTime();
    }

    public void reset(){
        timer.stop();
        secondsLeft = (Integer) focusBox.getSelectedItem() * 60;
        status = 0;
        
        startButton.setVisible(true);
        pauseButton.setVisible(false);
        resumeButton.setVisible(false);
        mainPanel.setBackground(Color.decode("#848d94"));
        displayTime();
        statusLabel.setText(" ");
    }

    public void displayTime(){
        minutes = secondsLeft / 60;
        seconds = secondsLeft % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public void playSound(File sound){
        try{
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(sound);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
