package gui;

import App.App;
import App.NoZipException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;

public class FileChooser extends JFrame implements ActionListener {

    private JTextField filePath;
    private String selectedFilePath;

    public FileChooser() {
        super("Unzip & Rename");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        JButton selectButton = new JButton("Select Directory");
        selectButton.addActionListener(this);
        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(this);
        confirmButton.setEnabled(true);

        filePath = new JTextField(20);
        panel.add(selectButton);
        panel.add(filePath);
        panel.add(confirmButton);
        add(panel);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals("Select Directory")) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setFileHidingEnabled(false);

            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                selectedFilePath = selectedFile.getAbsolutePath();
                filePath.setText(selectedFilePath);
            }
        } else if (evt.getActionCommand().equals("Confirm")) {
            String path = getSelectedFilePath().replaceAll("\"", "/");
            try {
                App.execute(path);
            } catch (Exception e) {
            } finally {
                dispose();
            }
        }
    }

    public String getSelectedFilePath() {
        return selectedFilePath;
    }

    public static void main(String[] args) {
        FileChooser fileChooser = new FileChooser();
        //fileChooser.pack();
        fileChooser.setVisible(true);
        fileChooser.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fileChooser.setLocationRelativeTo(null);
        fileChooser.setResizable(false);
    }

}