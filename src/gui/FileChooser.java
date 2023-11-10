package gui;

import App.SubSearch;
import App.UnzipAndRename;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;


public class FileChooser extends JFrame implements ActionListener {

    private JTextField filePath;
    private String selectedFilePath;


    private String title;

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
            File f = new File(path);
            String str = SubSearch.parseTitle(f.getName()).trim();
            try {
                System.out.println(SubSearch.getURL(str));
            } catch (Exception e) {

            }
            List<String> l = SubSearch.findSubLinks(titleCheck(str));
            if (l.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No subtitles found for following title: " + title,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            try {
                SubSearch.downloadFile("https://subsunacs.net" + SubSearch.mostAccurateEntry(l, title), path, title);
                UnzipAndRename.apply(path);
                JOptionPane.showMessageDialog(null,"Executed successfully", "Result"
                        , JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    public String titleCheck(String text) {
        JDialog jd = new JDialog(this, "", Dialog.ModalityType.APPLICATION_MODAL);
        jd.setSize(200, 200);
        jd.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.add(new JLabel("Is the following title correct?"));

        jd.add(panel);
        JButton confirmButton = new JButton("Confirm");
        JTextField field = new JTextField(15);
        field.setEditable(true);
        field.setText(text);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTitle(field.getText());
                System.out.println(field.getText());
                jd.dispose();
            }
        });
        confirmButton.setEnabled(true);

        panel.add(field);
        panel.add(confirmButton);
        jd.setVisible(true);
        jd.setLocationRelativeTo(this);

        return field.getText();

    }

    public String getSelectedFilePath() {
        return selectedFilePath;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }


    public static void main(String[] args) {
        FileChooser f = new FileChooser();
        f.setVisible(true);

    }

}

