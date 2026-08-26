import javax.swing.*;
import javax.sound.sampled.*;
import java.io.File;
import java.awt.Color;

public class Pomodoro {

    private int secondsLeft;
    private JLabel timeLeft;
    private int minutes;
    private int seconds;
    //private int status;
    private boolean focusOn;
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

        //**************buttons********************
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

        //******************************************* 

        resetButton.setVisible(false);
        resumeButton.setVisible(false);

        panel.add(statusLabel);
        panel.add(timeLeft);
        panel.add(startButton);
        panel.add(resumeButton);
        panel.add(pauseButton);
        panel.add(resetButton);
        panel.add (focusBox);
        panel.add(breakBox);

        frame.add(panel);

        //Display the window
        frame.setSize(500, 100);
        panel.setBackground(Color.decode("#003a4a"));
        frame.setVisible(true);

        //***************Timer************************ 
        timer = new Timer(10, event ->{
            if (secondsLeft == 0 & focusOn)
                startBreak();
            else if (secondsLeft == 0 & !focusOn)
                startPomodoro();
            else{
                displayTime();
                secondsLeft--;
            }
        });

    }

    /*switch case: if (secondsLeft == 0 & status == 7)
                startLongBreak();
            else if (secondsLeft == 0 & status % 2 == 1)   //if status is even (break)
                startPomodoro();
            else if (secondsLeft == 0 & status % 2 ==0)
                startBreak();
            else{
                displayTime();
                secondsLeft--;
            } */

    public void startPomodoro(){
        focusOn = true;
        playSound(chime);
        statusLabel.setText("Pomodoro");
        pauseButton.setVisible(true);
        resetButton.setVisible(true);
        secondsLeft = (Integer) focusBox.getSelectedItem() * 60;
    }

    public void startBreak(){
        focusOn = false;
        statusLabel.setText("Break");
        playSound(notif);
        secondsLeft = (Integer) breakBox.getSelectedItem() * 60;
    }

    /*public void startLongBreak(){
        status++;
        statusLabel.setText("Long break");
        secondsLeft = 25 * 60;
    }*/

    public void reset(){
        timer.stop();
        secondsLeft = (Integer) focusBox.getSelectedItem() * 60;
        displayTime();
        
        startButton.setVisible(true);
        pauseButton.setVisible(false);
        panel.setBackground(Color.decode("#003a4a"));
        statusLabel.setText("");
    }

    public void displayTime(){
        minutes = secondsLeft / 60;
        seconds = secondsLeft % 60;
        timeLeft.setText(String.format("%02d:%02d", minutes, seconds));
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
