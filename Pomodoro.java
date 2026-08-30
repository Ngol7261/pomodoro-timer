import javax.swing.*;
import java.awt.Font;
import javax.sound.sampled.*;
import java.awt.Color;
import java.awt.Dimension;

public class Pomodoro {

    private int secondsLeft;
    private int minutes;
    private int seconds;
    private int status; // 1, 3, or 5: pomodoro | 2, 4, or 6: break | 7: long break
    private Timer timer;

    private JButton startButton;
    private JButton resumeButton;
    private JButton pauseButton;
    private JButton resetButton;
    private JButton skipButton;

    private JLabel timeLabel;
    private JLabel statusLabel;

    private JPanel mainPanel;
    private JPanel buttonsPanel;
    private JPanel dropDownPanel;

    private JComboBox<Integer> focusBox;
    private JComboBox<Integer> breakBox;
    private JComboBox<Integer> longBox;

    private String pomodoroAudio = "/chime-audio.wav";
    private String breakAudio = "/notif-sound.wav";
    private String longBreakAudio = "/long-break-notif.wav";

    public Pomodoro() {

        // ********* Setting up the display **********
        JFrame frame = new JFrame("Pomodoro Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        // ------- status label --------
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Georgia", Font.BOLD, 20));
        statusLabel.setForeground(Color.decode("#ffffff"));
        statusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        mainPanel.add(statusLabel);

        // --------- time label ---------
        timeLabel = new JLabel("25:00");
        timeLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        timeLabel.setFont(new Font("Georgia", Font.PLAIN, 80));
        timeLabel.setForeground(Color.decode("#ffffff"));
        mainPanel.add(timeLabel);

        // **************** Buttons ********************
        startButton = new JButton("Start");
        startButton.setFont(new Font("Georgia", Font.PLAIN, 15));
        resumeButton = new JButton("Resume");
        resumeButton.setFont(new Font("Georgia", Font.PLAIN, 15));
        pauseButton = new JButton("Pause");
        pauseButton.setFont(new Font("Georgia", Font.PLAIN, 15));
        resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Georgia", Font.PLAIN, 15));

        skipButton = new JButton("⏭");
        skipButton.setPreferredSize(new Dimension(45, 30));
        skipButton.setMaximumSize(skipButton.getPreferredSize());   

        startButton.addActionListener(e -> {
            startPomodoro();
            pauseButton.setVisible(true);
            resetButton.setVisible(true);
            skipButton.setVisible(true);
            startButton.setVisible(false);
            timer.start();
        });

        resumeButton.addActionListener(e -> {
            pauseButton.setVisible(true);
            resumeButton.setVisible(false);
            timer.start();
        });

        pauseButton.addActionListener(e -> { // pause button function
            timer.stop();
            pauseButton.setVisible(false);
            resumeButton.setVisible(true);
        });

        resetButton.addActionListener(e -> reset());

        skipButton.addActionListener(e -> {
            secondsLeft = 0;
            if (status == 7)
                startLong();
            else if (status % 2 == 0)
                startPomodoro();
            else if (status % 2 == 1)
                startBreak();
        });

        // -------- buttons panel ----------
        buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        buttonsPanel.add(startButton);
        buttonsPanel.add(resumeButton);
        buttonsPanel.add(pauseButton);
        buttonsPanel.add(resetButton);
        buttonsPanel.add(skipButton);

        resetButton.setVisible(false);
        resumeButton.setVisible(false);
        pauseButton.setVisible(false);
        skipButton.setVisible(false);

        mainPanel.add(buttonsPanel);

        // ************ Drop down menus ****************
        Integer[] focusOptions = { 10, 15, 20, 25, 30, 45, 60 };
        focusBox = new JComboBox<>(focusOptions);
        focusBox.setSelectedIndex(3);
        focusBox.addActionListener(e -> reset());

        Integer[] breakOptions = { 3, 5, 7, 10, 15, 20 };
        breakBox = new JComboBox<>(breakOptions);
        breakBox.setSelectedIndex(1);
        breakBox.addActionListener(e -> reset());

        Integer[] longOptions = { 10, 15, 20, 25, 30 };
        longBox = new JComboBox<>(longOptions);
        longBox.addActionListener(e -> reset());

        // ---------- drop down panels -----------
        JPanel focusPanel = new JPanel();
        focusPanel.setLayout(new BoxLayout(focusPanel, BoxLayout.Y_AXIS));
        focusPanel.setOpaque(false);

        JLabel focusLabel = new JLabel("Focus");
        focusLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
        focusLabel.setForeground(Color.decode("#ffffff"));
        focusLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        focusPanel.add(focusLabel);
        focusPanel.add(focusBox);

        JPanel breakPanel = new JPanel();
        breakPanel.setLayout(new BoxLayout(breakPanel, BoxLayout.Y_AXIS));
        breakPanel.setOpaque(false);

        JLabel breakLabel = new JLabel("Break");
        breakLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
        breakLabel.setForeground(Color.decode("#ffffff"));
        breakLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        breakPanel.add(breakLabel);
        breakPanel.add(breakBox);

        JPanel longPanel = new JPanel();
        longPanel.setLayout(new BoxLayout(longPanel, BoxLayout.Y_AXIS));
        longPanel.setOpaque(false);

        JLabel longLabel = new JLabel("Long break");
        longLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
        longLabel.setForeground(Color.decode("#ffffff"));
        longLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        longPanel.add(longLabel);
        longPanel.add(longBox);

        dropDownPanel = new JPanel();
        dropDownPanel.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        dropDownPanel.setLayout(new BoxLayout(dropDownPanel, BoxLayout.X_AXIS));
        dropDownPanel.setBackground(Color.decode("#203d42"));

        dropDownPanel.add(focusPanel);
        dropDownPanel.add(breakPanel);
        dropDownPanel.add(longPanel);

        mainPanel.add(dropDownPanel);

        secondsLeft = (Integer) focusBox.getSelectedItem() * 60;
        status = 0;

        frame.setSize(400, 255);
        mainPanel.setBackground(Color.decode("#315c64"));
        frame.add(mainPanel);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // ******************* Timer *******************
        timer = new Timer(1000, event -> {
            displayTime();
            secondsLeft--;
            if (secondsLeft == 0 && status == 7) // if long
                startLong();
            else if (secondsLeft == 0 && status % 2 == 0) // if break
                startPomodoro();
            else if (secondsLeft == 0 && status % 2 == 1) // if pomodoro
                startBreak();
        });

    }

    public void startPomodoro() {
        status++;
        statusLabel.setText("Pomodoro");
        secondsLeft = (Integer) focusBox.getSelectedItem() * 60;
        displayTime();
        playSound(pomodoroAudio);
        mainPanel.setBackground(Color.decode("#7f2612"));
        dropDownPanel.setBackground(Color.decode("#4d170b"));
    }

    public void startBreak() {
        status++;
        statusLabel.setText("Break");
        playSound(breakAudio);
        secondsLeft = (Integer) breakBox.getSelectedItem() * 60;
        mainPanel.setBackground(Color.decode("#3e4d18"));
        dropDownPanel.setBackground(Color.decode("#262f0f"));
        displayTime();
    }

    public void startLong() {
        statusLabel.setText("Long Break");
        secondsLeft = (Integer) longBox.getSelectedItem() * 60;
        mainPanel.setBackground(Color.decode("#41457b"));
        dropDownPanel.setBackground(Color.decode("#222540"));
        status = 0;
        displayTime();
        playSound(longBreakAudio);
    }

    public void reset() {
        timer.stop();
        secondsLeft = (Integer) focusBox.getSelectedItem() * 60;
        status = 0;

        startButton.setVisible(true);
        pauseButton.setVisible(false);
        resumeButton.setVisible(false);
        skipButton.setVisible(false);
        mainPanel.setBackground(Color.decode("#315c64"));
        dropDownPanel.setBackground(Color.decode("#203d42"));
        displayTime();
        statusLabel.setText(" ");
    }

    public void displayTime() {
        minutes = secondsLeft / 60;
        seconds = secondsLeft % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public void playSound(String sound) {
        new Thread(() -> {
            try {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                        Pomodoro.class.getResource(sound));

                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
