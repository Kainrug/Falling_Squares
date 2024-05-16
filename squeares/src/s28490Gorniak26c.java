import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class s28490Gorniak26c {

    JFrame frame = new JFrame("Square Game");
    JLabel label = new JLabel("Score: " + Board.score + '%');
    JMenu modeMenu;
    JMenuItem easyItem, normalItem, hardItem;

    public s28490Gorniak26c() {
        frame.setMinimumSize(new Dimension(800, 600));

        createMenu();

        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        modeMenu = new JMenu("Mode");
        easyItem = new JMenuItem("Easy");
        normalItem = new JMenuItem("Normal");
        hardItem = new JMenuItem("Hard");

        Font buttonFont = new Font("Arial", Font.BOLD, 16); // Nowa czcionka dla przycisków

        easyItem.addActionListener(new ModeListener(850, 5));
        easyItem.setFont(buttonFont);
        easyItem.setBackground(Color.GREEN);

        normalItem.addActionListener(new ModeListener(650, 4));
        normalItem.setFont(buttonFont);
        normalItem.setBackground(Color.YELLOW);

        hardItem.addActionListener(new ModeListener(450, 2));
        hardItem.setFont(buttonFont);
        hardItem.setBackground(Color.RED);

        modeMenu.add(easyItem);
        modeMenu.add(normalItem);
        modeMenu.add(hardItem);

        menuBar.add(modeMenu);
        frame.setJMenuBar(menuBar);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            s28490Gorniak26c game = new s28490Gorniak26c();
            game.showMenu();
        });
    }

    private void showMenu() {
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BorderLayout());
        menuPanel.setPreferredSize(new Dimension(400, 150));
        menuPanel.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("Choose a game mode");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonsPanel = new JPanel(); // Dodajemy nowy panel dla przycisków
        buttonsPanel.setBackground(Color.BLACK);
        buttonsPanel.setLayout(new FlowLayout()); // Ustawiamy FlowLayout dla przycisków

        Font buttonFont = new Font("Arial", Font.BOLD, 24);
        Color borderColor = Color.WHITE;

        JButton easyButton = new JButton("Easy");
        easyButton.addActionListener(new ModeListener(850, 5));
        easyButton.setFont(buttonFont);
        easyButton.setBackground(Color.GREEN);
        easyButton.setBorder(BorderFactory.createLineBorder(borderColor, 3));

        JButton normalButton = new JButton("Normal");
        normalButton.addActionListener(new ModeListener(650, 4));
        normalButton.setFont(buttonFont);
        normalButton.setBackground(Color.YELLOW);
        normalButton.setBorder(BorderFactory.createLineBorder(borderColor, 3));

        JButton hardButton = new JButton("Hard");
        hardButton.addActionListener(new ModeListener(450, 2));
        hardButton.setFont(buttonFont);
        hardButton.setBackground(Color.RED);
        hardButton.setBorder(BorderFactory.createLineBorder(borderColor, 3));

        buttonsPanel.add(easyButton);
        buttonsPanel.add(normalButton);
        buttonsPanel.add(hardButton);



        menuPanel.add(titleLabel, BorderLayout.NORTH);
        menuPanel.add(buttonsPanel, BorderLayout.CENTER); // Dodajemy panel przycisków do panelu głównego

        frame.add(menuPanel);
        frame.pack();
    }

    class ModeListener implements ActionListener {
        private int addSleep;
        private int updateSleep;

        public ModeListener(int addSleep, int updateSleep) {
            this.addSleep = addSleep;
            this.updateSleep = updateSleep;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            frame.getContentPane().removeAll();
            frame.revalidate();
            frame.repaint();

            Board board = new Board(addSleep, updateSleep);
            label.setBorder(new TitledBorder("Score"));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            frame.add(board);
            frame.add(label, BorderLayout.SOUTH);
            frame.pack();
        }
    }

    class Board extends JPanel {
        double points = 0;
        double max = 1;

        static double score;
        private int addSleep;
        private int updateSleep;

        List<Rectangle2D> list = new ArrayList<>();
        Thread add = new Thread() {
            @Override
            public void run() {
                while (true) {
                    Random random = new Random();
                    int sizeLength = frame.getHeight() / 10;
                    int xPosition = random.nextInt(frame.getWidth() - sizeLength);
                    synchronized (list) {
                        list.add(new Rectangle2D.Double(xPosition, 0, sizeLength, sizeLength));
                    }
                    max++;
                    score = points / max;
                    try {
                        Thread.sleep(addSleep);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };

        Thread update = new Thread(() -> {
            while (true) {
                synchronized (list) {
                    for (Iterator<Rectangle2D> iterator = list.iterator(); iterator.hasNext(); ) {
                        Rectangle2D square = iterator.next();
                        if (square.getY() >= this.getHeight() - square.getHeight())
                            iterator.remove();
                        else
                            square.setRect(square.getX(), square.getY() + 1, square.getWidth(), square.getHeight());
                    }
                }
                try {
                    Thread.sleep(updateSleep);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread timer = new Thread(() -> {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            add.interrupt();
            update.interrupt();

            if (score > 0.8) {
                JOptionPane.showMessageDialog(this, "You win");
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(this, "You lose");
                System.exit(0);
            }
        });

        public Board(int addSleep, int updateSleep) {
            this.addSleep = addSleep;
            this.updateSleep = updateSleep;

            this.setBackground(Color.black);
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    for (Iterator<Rectangle2D> iterator = list.iterator(); iterator.hasNext(); ) {
                        Rectangle2D square = iterator.next();
                        if (square.contains(e.getPoint())) {
                            iterator.remove();
                            points++;
                            score = (points / max);
                        }
                    }
                }
            });
            add.start();
            update.start();
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            this.repaint();
            label.setText("Score: " + (int) (score * 100) + "%");
            super.paintComponent(g);
            Graphics2D graphics2D = (Graphics2D) g;

            Stroke borderStroke = new BasicStroke(3.0f); // Grubość linii obramowania

            synchronized (list) {
                for (Rectangle2D square : list) {
                    square.setRect(square.getX(), square.getY(), frame.getHeight() / 10, frame.getHeight() / 10);

                    graphics2D.setStroke(borderStroke); // Ustawienie pogrubionego obramowania
                    graphics2D.setColor(Color.BLUE);
                    graphics2D.fill(square);

                    graphics2D.setColor(Color.GREEN);
                    graphics2D.draw(square); // Narysowanie obramowania
                }
            }
        }
    }
}
