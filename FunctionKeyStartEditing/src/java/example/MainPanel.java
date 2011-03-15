package example;
//-*- mode:java; encoding:utf8n; coding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class MainPanel extends JPanel {
    private final JCheckBox check = new JCheckBox("ignore: F1,F4-F7,F9-");
    private final JTextArea textarea = new JTextArea("F2: startEditing\nF8: focusHeader\nF3: beep");
    public MainPanel() {
        super(new BorderLayout());
        String[] columnNames = {"String", "Integer", "Boolean"};
        Object[][] data = {
            {"aaa", 12, true}, {"bbb", 5, false},
            {"CCC", 92, true}, {"DDD", 0, false}
        };
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };
        JTable table = new JTable(model) {
            //Swing - JTable starts editing when F3 is pressed - howto disable?
            //http://forums.oracle.com/forums/thread.jspa?threadID=1348192
            @Override public boolean editCellAt(int row, int column, EventObject e) {
                if(!check.isSelected()) return super.editCellAt(row, column, e);
                if(e instanceof KeyEvent) {
                    int c = ((KeyEvent)e).getKeyCode();
                    if(KeyEvent.VK_F1<=c && c<=KeyEvent.VK_F21) {
                        return false;
                    }
                }
                return super.editCellAt(row, column, e);
            }
//             private java.util.List ignoreKeyList = java.util.Arrays.asList(
//                 KeyEvent.VK_F1, KeyEvent.VK_F4, KeyEvent.VK_F5, KeyEvent.VK_F6, KeyEvent.VK_F7,
//                 KeyEvent.VK_F9, KeyEvent.VK_F10, KeyEvent.VK_F11,KeyEvent.VK_F12);
//             @Override protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
//                 if(!check.isSelected()) return super.processKeyBinding(ks, e, condition, pressed);
//                 if(e instanceof KeyEvent) {
//                     int c = ((KeyEvent)e).getKeyCode();
//                     if(KeyEvent.VK_F1<=c && c<=KeyEvent.VK_F21) {
//                         return false;
//                     }
//                 }
//                 return super.processKeyBinding(ks, e, condition, pressed);
//
// //                 if(ignoreKeyList.contains(ks.getKeyCode())) {
// //                     return false;
// //                 }else{
// //                     return super.processKeyBinding(ks, e, condition, pressed);
// //                 }
//             }
        };
        table.setAutoCreateRowSorter(true);

        table.getActionMap().put("beep", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                System.out.println("F3");
                Toolkit.getDefaultToolkit().beep();
            }
        });
        InputMap im = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "beep");

//         for(int ks: java.util.Arrays.asList(KeyEvent.VK_F1, KeyEvent.VK_F2, KeyEvent.VK_F3, KeyEvent.VK_F4,
//                                             KeyEvent.VK_F5, KeyEvent.VK_F6, KeyEvent.VK_F7, KeyEvent.VK_F8,
//                                             KeyEvent.VK_F9, KeyEvent.VK_F10, KeyEvent.VK_F11,KeyEvent.VK_F12)) {
//             InputMap im = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//             if(im.get(KeyStroke.getKeyStroke(ks, 0))==null) {
//                 im.put(KeyStroke.getKeyStroke(ks, 0),            "none");
//             }
//             im.put(KeyStroke.getKeyStroke(ks, Event.CTRL_MASK),  "none");
//             im.put(KeyStroke.getKeyStroke(ks, Event.SHIFT_MASK), "none");
//         }

        check.setSelected(true);
        textarea.setEditable(false);

        JPanel p = new JPanel(new BorderLayout());
        p.add(check, BorderLayout.NORTH);
        p.add(new JScrollPane(textarea));
        add(p, BorderLayout.NORTH);
        add(new JScrollPane(table));
        setPreferredSize(new Dimension(320, 240));
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