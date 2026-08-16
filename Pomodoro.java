import javax.swing.*;

public class Pomodoro {

    private int secondsLeft;
    private Timer timer;
    private JButton startButton;
    private JButton pauseButton;

    public Pomodoro(){
        JFrame frame = new JFrame("Pomodoro Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        JLabel statusLabel = new JLabel();
        JLabel timeLeft = new JLabel();

        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        JButton resetButton = new JButton("Reset");

        startButton.addActionListener(e ->{ //on start click, text "pomodoro"
            statusLabel.setText("Pomodoro");
            startPomodoro();
        });

        pauseButton.addActionListener(e ->{ //pause button function
            timer.stop();
        });

        resetButton.addActionListener(e ->{ 
            System.out.println("Yo the reset button works");
        });

        panel.add(statusLabel);
        panel.add(timeLeft);
        panel.add(startButton);
        panel.add(pauseButton);
        panel.add(resetButton);

        frame.add(panel);

        //Display the window
        frame.pack();
        frame.setVisible(true);

        timer = new Timer(1000, event ->{
            if (secondsLeft<0)
                return;

            int minutes = secondsLeft / 60;
            int seconds = secondsLeft % 60;
            timeLeft.setText(String.format("%02d:%02d", minutes, seconds));

            System.out.println(secondsLeft);
            secondsLeft--;
        });
    }

    public void startPomodoro(){
        pauseButton.setVisible(true);
        secondsLeft = 10;
        timer.start();
    }

    /*private void showGUI() {
        JFrame frame = new JFrame("Pomodoro Timer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        JLabel statusLabel = new JLabel();
        JLabel timeLeft = new JLabel();

        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        JButton resetButton = new JButton("Reset");

        startButton.addActionListener(e ->{ //on start click, text "pomodoro"
            statusLabel.setText("Pomodoro");
            startPomodoro();
        });

        pauseButton.addActionListener(e ->{ //pause button function
            timer.stop();
        });

        resetButton.addActionListener(e ->{ 
            System.out.println("Yo the reset button works");
        });

        panel.add(statusLabel);
        panel.add(timeLeft);
        panel.add(startButton);
        panel.add(pauseButton);
        panel.add(resetButton);

        frame.add(panel);

        //Display the window
        frame.pack();
        frame.setVisible(true);

        timer = new Timer(1000, event ->{
            if (secondsLeft<0)
                return;

            int minutes = secondsLeft / 60;
            int seconds = secondsLeft % 60;
            timeLeft.setText(String.format("%02d:%02d", minutes, seconds));

            System.out.println(secondsLeft);
            secondsLeft--;
        });
    }
    
    public void startPomodoro(){
        pauseButton.setVisible(true);
        secondsLeft = 10;
        timer.start();
    }*/

}
