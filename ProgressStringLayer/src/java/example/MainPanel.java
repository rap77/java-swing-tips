package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.LayerUI;

public class MainPanel extends JPanel {
    public MainPanel() {
        super(new BorderLayout());

        final BoundedRangeModel m = new DefaultBoundedRangeModel();
        final JProgressBar progressBar  = new JProgressBar(m);
        progressBar.setOrientation(SwingConstants.VERTICAL);

        JProgressBar progressBar0 = new JProgressBar(m);
        initProgressBar(progressBar0);
        progressBar0.setStringPainted(true);

        JPanel p = new JPanel();
        p.add(progressBar);
        p.add(Box.createHorizontalStrut(5));
        p.add(progressBar0);
        p.add(Box.createHorizontalStrut(5));
        p.add(makeProgressBar1(m));
        p.add(Box.createHorizontalStrut(5));
        p.add(makeProgressBar2(m));
//         p.add(new JButton(new AbstractAction("+10") {
//             private int i = 0;
//             @Override public void actionPerformed(ActionEvent e) {
//                 m.setValue(i = (i>=100) ? 0 : i + 10);
//             }
//         }));

        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(new JButton(new AbstractAction("Test") {
            SwingWorker<String, Void> worker;
            @Override public void actionPerformed(ActionEvent e) {
                if(worker!=null && !worker.isDone()) worker.cancel(true);
                worker = new SwingWorker<String, Void>() {
                    @Override public String doInBackground() {
                        int current = 0;
                        int lengthOfTask = 100;
                        while(current<=lengthOfTask && !isCancelled()) {
                            try { // dummy task
                                Thread.sleep(50);
                            }catch(InterruptedException ie) {
                                return "Interrupted";
                            }
                            setProgress(100 * current / lengthOfTask);
                            current++;
                        }
                        return "Done";
                    }
                    @Override public void done() {
                        String text = null;
                        if(isCancelled()) {
                            text = "Cancelled";
                        }else{
                            try{
                                text = get();
                            }catch(Exception ex) {
                                ex.printStackTrace();
                                text = "Exception";
                            }
                        }
                    }
                };
                worker.addPropertyChangeListener(new ProgressListener(progressBar));
                worker.execute();
            }
        }));
        box.add(Box.createHorizontalStrut(5));
        box.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        add(new JProgressBar(m), BorderLayout.NORTH);
        add(p);
        add(box, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(320, 240));
    }
    private static JProgressBar makeProgressBar1(BoundedRangeModel model) {
        JProgressBar progressBar = new JProgressBar(model) {
            private JLabel label = new JLabel("000/100", SwingConstants.CENTER);
            private ChangeListener changeListener = null;
            @Override public void updateUI() {
                removeAll();
                if(changeListener!=null) removeChangeListener(changeListener);
                super.updateUI();
                EventQueue.invokeLater(new Runnable() {
                    @Override public void run() {
                        setLayout(new BorderLayout());
                        changeListener = new ChangeListener() {
                            @Override public void stateChanged(ChangeEvent e) {
                                int iv = (int)(100 * getPercentComplete());
                                label.setText(String.format("%03d/100", iv));
                                //label.setText(getString());
                            }
                        };
                        addChangeListener(changeListener);
                        add(label);
                        label.setBorder(BorderFactory.createEmptyBorder(0,4,0,4));
                    }
                });
            }
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                Insets i = label.getInsets();
                d.width = label.getPreferredSize().width + i.left + i.right;
                return d;
            }
        };
        initProgressBar(progressBar);
        return progressBar;
    }
    private static JComponent makeProgressBar2(BoundedRangeModel model) {
        final JLabel label = new JLabel("000/100");
        label.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        LayerUI<JProgressBar> layerUI = new LayerUI<JProgressBar>() {
            private final JPanel rubberStamp = new JPanel();
            @Override public void paint(Graphics g, JComponent c) {
                super.paint(g, c);
                Dimension d = label.getPreferredSize();
                int x = (c.getWidth()  - d.width)  / 2;
                int y = (c.getHeight() - d.height) / 2;
                JLayer jlayer = (JLayer)c;
                JProgressBar progress = (JProgressBar)jlayer.getView();
                int iv = (int)(100 * progress.getPercentComplete());
                label.setText(String.format("%03d/100", iv));
                //label.setText(progress.getString());
                SwingUtilities.paintComponent(
                    g, label, rubberStamp, x, y, d.width, d.height);
            }
        };
        JProgressBar progressBar = new JProgressBar(model) {
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                Insets i = label.getInsets();
                d.width = label.getPreferredSize().width + i.left + i.right;
                return d;
            }
        };
        initProgressBar(progressBar);
        return new JLayer<JProgressBar>(progressBar, layerUI);
    }
    private static void initProgressBar(JProgressBar progressBar) {
        progressBar.setOrientation(SwingConstants.VERTICAL);
        progressBar.setStringPainted(false);
        //progressBar.setForeground(Color.GREEN);
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
class ProgressListener implements PropertyChangeListener {
    private final JProgressBar progressBar;
    ProgressListener(JProgressBar progressBar) {
        this.progressBar = progressBar;
        this.progressBar.setValue(0);
    }
    @Override public void propertyChange(PropertyChangeEvent evt) {
        String strPropertyName = evt.getPropertyName();
        if("progress".equals(strPropertyName)) {
            progressBar.setIndeterminate(false);
            int progress = (Integer)evt.getNewValue();
            progressBar.setValue(progress);
        }
    }
}