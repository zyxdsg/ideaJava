package com.horsman.corejava;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.*;

/**
 * @ClassName:
 * @Description:
 * @author:
 * @date:
 * @Version:
 * @Copyright:
 */
public class LoggingImageViewer {
    public static void main(String[] args) {
        if (System.getProperty("java.util.logging.config.class") == null && System.getProperty("java.util.logging.config.file")==null){
            try {
                Logger.getLogger("com.horsman.corejava").setLevel(Level.ALL);
                final  int LOG_ROTATION_COUNT = 10;
                Handler handler = new FileHandler("%h/LoggingImageViewwe.log",0,LOG_ROTATION_COUNT);
                Logger.getLogger("com.horsman.corejava").addHandler(handler);
            }catch (IOException e) {
                Logger.getLogger("com.horsman.corejava").log(Level.SEVERE,"不能创建日志处理器",0);
            }
        }
        EventQueue.invokeLater(()->{
            Handler windowHandler = new WindowHandler();
            windowHandler.setLevel(Level.ALL);
            Logger.getLogger("com.horsman.corejava").addHandler(windowHandler);

            JFrame frame = new ImageViewerFrame();
            frame.setTitle("LoggingImageViewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Logger.getLogger("com.horsman.corejava").fine("Showing frame");
            frame.setVisible(true);

        });
    }
}
class ImageViewerFrame extends JFrame{
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 400;

    private JLabel label;
    private static Logger logger = Logger.getLogger("com.horsman.corejava");

    public ImageViewerFrame(){
        logger.entering("ImafeViewerFrame","<init>");
        setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menu = new JMenu("File");
        menuBar.add(menu);

        JMenuItem openItem = new JMenuItem("Open");
        menu.add(openItem);
        openItem.addActionListener(new FileOpenListener());

        JMenuItem exitItem = new JMenuItem("Exit");
        menu.add(exitItem);
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                logger.fine("Exiting.");
                System.exit(0);
            }
        });
        label = new JLabel();
        add(label);
        logger.exiting("ImageViewFrame","<init>");
    }
    private class FileOpenListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent event) {
            logger.entering("ImageViewFrame.FileOpenListener","actionPerformed",event);

            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));

            chooser.setFileFilter(new javax.swing.filechooser.FileFilter(){

                @Override
                public boolean accept(File file) {
                    return file.getName().toLowerCase().endsWith(".gif") || file.isDirectory();
                }

                @Override
                public String getDescription() {
                    return "GIF Images";
                }
            });
            int r = chooser.showOpenDialog(ImageViewerFrame.this);

            if(r == JFileChooser.APPROVE_OPTION){
                String name = chooser.getSelectedFile().getPath();
                logger.log(Level.FINE,"Reading file{0}",name);
                label.setIcon(new ImageIcon(name));
            }else {
                logger.fine("File open dialog canceled");
            }
            logger.entering("ImageViewFrame.FileOpenListener","actionPerformed");
        }
    }

}
class WindowHandler extends StreamHandler{
    private  JFrame frame;

    public  WindowHandler(){
        frame = new JFrame();
        final JTextArea output = new JTextArea();
        output.setEditable(false);
        frame.setSize(200,200);
        frame.add(new JScrollPane(output));
        frame.setFocusableWindowState(false);
        frame.setVisible(true);

        setOutputStream(new OutputStream() {
            @Override
            public void write(int i) throws IOException {
            }
            public  void  write(byte[] b,int off,int len){
                output.append(new String(b,off,len));
            }
        });
    }

    @Override
    public synchronized void publish(LogRecord record) {
        if(!frame.isVisible()) return;
        super.publish(record);
        flush();
    }
}
