import javax.swing.*;
import javax.sound.sampled.*;
import java.io.File;
import java.awt.Color;

public class Pomodoro {

    private int secondsLeft;
    private int focusMinutes;
    //private int breakMinutes;
    private JLabel timeLeft;
    private int minutes;
    private int seconds;
    //private boolean breakOn;
    private Timer timer;

    private JButton startButton;
    private JButton pauseButton;
    private JButton resetButton;

    private JPanel panel;
    private JLabel statusLabel;
    //private int breakDuration;
    //private int pomodoroDuration;
    private JComboBox <Integer> focusBox;
    //private JComboBox <Integer> breakBox;

    private File chime = new File("chime-audio.wav");
    private File notif = new File("notif-sound.wav");

    public Pomodoro(){
        //setting up the display
        JFrame frame = new JFrame("Pomodoro Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel();

        //Drop down menus
        Integer [] focusOptions = {1, 15, 20, 25, 30, 45};
        focusBox = new JComboBox<>(focusOptions);

        focusMinutes = (Integer) focusBox.getSelectedItem();
        secondsLeft = focusMinutes * 60;

        //Integer [] breakOptions = {2, 5, 8, 10, 15};
        //breakBox = new JComboBox<>(breakOptions);
        //TODO create a starting mode

        focusBox.addActionListener(e->{
            reset();
        });

        statusLabel = new JLabel(); //text shows whether it is pomodoro or break time
        timeLeft = new JLabel();

        //buttons
        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        resetButton = new JButton("Reset");

        startButton.addActionListener(e ->{ //on start click, text "pomodoro"
            panel.setBackground(Color.decode("#883131"));
            if (secondsLeft == focusMinutes * 60)
                playSound(chime);
            startPomodoro();
            //pomodoroLoop();
            startButton.setVisible(false);
        });

        pauseButton.addActionListener(e ->{ //pause button function
            timer.stop();
            pauseButton.setVisible(false);
            startButton.setVisible(true);
        });

        resetButton.addActionListener(e -> reset());

        panel.add(statusLabel);
        panel.add(timeLeft);
        panel.add(startButton);
        panel.add(pauseButton);
        panel.add(resetButton);
        panel.add (focusBox);
        //panel.add(breakBox);

        resetButton.setVisible(false);

        frame.add(panel);

        //Display the window
        frame.setSize(500, 100);
        panel.setBackground(Color.decode("#BD968E"));
        frame.setVisible(true);

        timer = new Timer(1000, event ->{
            if (secondsLeft<0)
                return;

            displayTime();

            System.out.println(secondsLeft);
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
        statusLabel.setText("Pomodoro");
        pauseButton.setVisible(true);
        resetButton.setVisible(true);
        if (secondsLeft == focusMinutes)
            playSound(chime);
        timer.start();
    }

    public void startBreak(int secondsLeft){

    }

    public void displayTime(){
        minutes = secondsLeft / 60;
        seconds = secondsLeft % 60;
        timeLeft.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public void reset(){
        timer.stop();
        focusMinutes = (Integer) focusBox.getSelectedItem();
        secondsLeft = focusMinutes * 60;
        displayTime();
        startButton.setVisible(true);
        pauseButton.setVisible(false);
        panel.setBackground(Color.decode("#BD968E"));
        statusLabel.setText("");
    }
}
