import javax.swing.*;
import javax.sound.sampled.*;
import java.io.File;
import java.awt.Color;

public class Pomodoro {

    private int secondsLeft;
    private JLabel timeLeft;
    private int minutes;
    private int seconds;
    private int status; //1: pomodoro, 2: break, 3: long break
    private Timer timer;

    private JButton startButton;
    private JButton resumeButton;
    private JButton pauseButton;
    private JButton resetButton;

    private JPanel panel;
    private JLabel statusLabel;
    private JComboBox <Integer> focusBox;
    private JComboBox <Integer> breakBox;

    private File chime = new File("chime-audio.wav");
    private File notif = new File("notif-sound.wav");

    public Pomodoro(){
        
        //setting up the display
        JFrame frame = new JFrame("Pomodoro Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel();
        statusLabel = new JLabel(); //text shows whether it is pomodoro or break time
        timeLeft = new JLabel();

        //Drop down menus
        Integer [] focusOptions = {1, 15, 20, 25, 30, 45};
        focusBox = new JComboBox<>(focusOptions);
        focusBox.addActionListener(e-> reset());

        Integer [] breakOptions = {2, 5, 8, 10, 15};
        breakBox = new JComboBox<>(breakOptions);
        breakBox.addActionListener(e-> reset());

        secondsLeft = (Integer) focusBox.getSelectedItem() * 60;
        
        //buttons
        startButton = new JButton("Start");
        resumeButton = new JButton("resume");
        pauseButton = new JButton("Pause");
        resetButton = new JButton("Reset");

        startButton.addActionListener(e ->{ 
            panel.setBackground(Color.decode("#883131"));
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

        resetButton.addActionListener(e -> reset());

        panel.add(statusLabel);
        panel.add(timeLeft);
        panel.add(startButton);
        panel.add(resumeButton);
        panel.add(pauseButton);
        panel.add(resetButton);
        panel.add (focusBox);
        panel.add(breakBox);

        resetButton.setVisible(false);
        resumeButton.setVisible(false);

        frame.add(panel);

        //Display the window
        frame.setSize(500, 100);
        panel.setBackground(Color.decode("#BD968E"));
        frame.setVisible(true);

        timer = new Timer(100, event ->{
            if (secondsLeft == 0){
                switch (status){
                    case 1:
                        startBreak();
                        break;
                    case 2:
                        secondsLeft = (Integer) focusBox.getSelectedItem() * 60;
                        startPomodoro();
                        break;
                    case 3:
                        System.out.println("long break");
                        break;
                        //TODO case of long break
                }
                return;
            }
            displayTime();
            secondsLeft--;
        });
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

    public void startPomodoro(){
        status = 1;
        playSound(chime);
        statusLabel.setText("Pomodoro");
        pauseButton.setVisible(true);
        resetButton.setVisible(true);
        secondsLeft = (Integer) focusBox.getSelectedItem() * 60;
    }

    public void startBreak(){
        status = 2;
        statusLabel.setText("Break");
        playSound(notif);
        secondsLeft = (Integer) breakBox.getSelectedItem() * 60;
    }

    public void displayTime(){
        minutes = secondsLeft / 60;
        seconds = secondsLeft % 60;
        timeLeft.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public void reset(){
        timer.stop();
        //reset the focus time
        secondsLeft = (Integer) focusBox.getSelectedItem() * 60;
        displayTime();
        
        startButton.setVisible(true);
        pauseButton.setVisible(false);
        panel.setBackground(Color.decode("#BD968E"));
        statusLabel.setText("");
    }
}
