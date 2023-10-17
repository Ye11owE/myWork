import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.Font;
public class UI extends JFrame{
    Encrypt_GUI encrypt_GUI;
    Decrypt_GUI decrypt_GUI;
    JLabel welcomeLabel;
    JButton decryptButton,encryptButton;

    UI(){
        JFrame frame = new JFrame();
        frame.setTitle("S-DES加密系统");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);

        //设置基础进入界面
        welcomeLabel = new JLabel("欢迎使用S-DES加密系统！");
        welcomeLabel.setFont(new Font("宋体", Font.TYPE1_FONT, 30));
        welcomeLabel.setBounds(230, 120, 400, 130);
        encryptButton = new JButton("加密明文");
        encryptButton.setFont(new Font("黑体", Font.PLAIN, 18));
        encryptButton.setBounds(220, 315, 130, 50);
        encryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                frame.setVisible(false);
                encrypt_GUI = new Encrypt_GUI();
            }
        });
        decryptButton = new JButton("解密密文");
        decryptButton.setBounds(450, 315, 130, 50);
        decryptButton.setFont(new Font("黑体", Font.PLAIN, 18));
        decryptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                frame.setVisible(false);
                decrypt_GUI = new Decrypt_GUI();
            }
        });
        frame.add(welcomeLabel);
        frame.add(encryptButton);
        frame.add(decryptButton);
        frame.setVisible(true);

    }
}
