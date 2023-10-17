import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

public class Encrypt_GUI {
    JTextField dataField;
    JTextArea resulTextArea;
    JPasswordField keyJPasswordField;
    JLabel dataJLabel, keyJLabel, resultJLabel, welcomeLabel;
    JCheckBox checkBox;
    JButton button1, button2;
    JOptionPane warning;
    String dataString, cipherText;
    char[] keyChar;
    int[] key;
    Box box_H_01, box_H_02, box_H_03, box_V;
    

    Encrypt_GUI(){
        init();
    }
    void init(){
        JFrame frame = new JFrame("Encrypt_GUI");

        //初始化UI布置组件
        box_H_01=Box.createHorizontalBox();
        box_H_02=Box.createHorizontalBox();
        box_H_03=Box.createHorizontalBox();
        box_V=Box.createVerticalBox();

        //设置窗体对象的属性值
        frame.setTitle("加密界面");
        frame.setSize(900, 650);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(null);

        //设置欢迎标签
        welcomeLabel = new JLabel("欢迎使用S-DES加密系统！");
        welcomeLabel.setFont(new Font("宋体", Font.TYPE1_FONT, 30));
        welcomeLabel.setBounds(265, 40 , 400, 80);

        // 明文输入模式选择按钮
        JRadioButton textModeRadioButton = new JRadioButton("二进制(8-bit)模式");
        JRadioButton asciiModeRadioButton = new JRadioButton("ASCII模式");
        ButtonGroup modeButtonGroup = new ButtonGroup();
        modeButtonGroup.add(textModeRadioButton);
        modeButtonGroup.add(asciiModeRadioButton);
        textModeRadioButton.setSelected(true);
        textModeRadioButton.setFont(new Font("黑体", Font.PLAIN, 18));
        asciiModeRadioButton.setFont(new Font("黑体", Font.PLAIN, 18));
        textModeRadioButton.setBounds(250, 115, 200, 30);
        asciiModeRadioButton.setBounds(500, 115, 120, 30);

        //明文输入
        dataJLabel=new JLabel("明文：");
        dataJLabel.setFont(new Font("黑体", Font.PLAIN, 18));
        dataField=new JTextField();
        dataField.setPreferredSize(new Dimension(150, 25));
        dataField.setFont(new Font("宋体", Font.PLAIN, 18));

        //密钥输入
        keyJLabel=new JLabel("密钥：");
        keyJLabel.setFont(new Font("黑体", Font.PLAIN, 18));
        keyJPasswordField = new JPasswordField();
        keyJPasswordField.setEchoChar('*');
        keyJPasswordField.setPreferredSize(new Dimension(150,25));
        keyJPasswordField.setFont(new Font("宋体", Font.PLAIN, 18));

        //密钥可视按钮
        checkBox= new JCheckBox("显示密钥");
        checkBox.setSize(new Dimension(70, 35));
        checkBox.setFont(new Font("黑体",Font.TYPE1_FONT,21));
        checkBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e){
                if(e.getStateChange()==ItemEvent.SELECTED){
                    keyJPasswordField.setEchoChar((char)0);
                }
                else{
                    keyJPasswordField.setEchoChar('*');
                }
            }
        });
        checkBox.setBounds(650, 255, 130, 70);

        //密文显示
        resultJLabel=new JLabel("密文：");
        resultJLabel.setFont(new Font("黑体", Font.PLAIN, 18));
        resulTextArea=new JTextArea();
        resulTextArea.setPreferredSize(new Dimension(180, 80));
        resulTextArea.setFont(new Font("宋体", Font.PLAIN, 18));
        resulTextArea.setEditable(false);

        //加密按钮
        button1=new JButton();
        button1.setText("加密");
        button1.setFont(new Font("黑体", Font.PLAIN, 19));
        button1.setSize(new Dimension(100,50));
        button1 = new JButton();
        button1.setText("加密");
        button1.setFont(new Font("黑体", Font.PLAIN, 19));
        button1.setSize(new Dimension(100, 50));
        // 加密按钮
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (textModeRadioButton.isSelected()) {
                    // 文本模式
                    String dataInput = dataField.getText().trim();
                    String keyInput = new String(keyJPasswordField.getPassword());
                    if (!isValidBinary(dataInput)) {
                        warning.showMessageDialog(frame, "请输入8位的二进制明文！", "警告", JOptionPane.WARNING_MESSAGE);
                    } else if (!isValidBinaryKey(keyInput)) {
                        warning.showMessageDialog(frame, "请输入10位的二进制密钥！", "警告", JOptionPane.WARNING_MESSAGE);
                    } else {
                        // 获取明文
                        dataString = dataInput;
                        // 获取密钥
                        keyChar = keyJPasswordField.getPassword();
                        key = new int[keyChar.length];
                        for (int i = 0; i < keyChar.length; i++) {
                            key[i] = Character.getNumericValue(keyChar[i]);
                        }
                        cipherText = SDES.encrypt(dataString, key);
                        resulTextArea.setText(cipherText.toString());
                    }
                } else if (asciiModeRadioButton.isSelected()) {
                    // ASCII模式
                    if (dataField.getText().trim().equals("")) {
                        warning.showMessageDialog(frame, "明文不能为空！", "警告", JOptionPane.WARNING_MESSAGE);
                    } else if (keyJPasswordField.getPassword().length != 10) {
                        warning.showMessageDialog(frame, "密钥长度必须为10bit！", "警告", JOptionPane.WARNING_MESSAGE);
                    } else {
                        // 获取明文
                        String asciiText = dataField.getText();
                        // 获取密钥
                        keyChar = keyJPasswordField.getPassword();
                        key = new int[keyChar.length];
                        for (int i = 0; i < keyChar.length; i++) {
                            key[i] = Character.getNumericValue(keyChar[i]);
                        }
                        // 将ASCII码转换为二进制字符串
                        StringBuilder binaryText = new StringBuilder();
                        for (char c : asciiText.toCharArray()) {
                            String binaryChar = Integer.toBinaryString(c);
                            while (binaryChar.length() < 8) {
                                binaryChar = "0" + binaryChar;
                            }
                            binaryText.append(binaryChar);
                        }

                        // 每8位进行切割，并放入字符串数组
                        int blockCount = binaryText.length() / 8;
                        String[] binaryBlocks = new String[blockCount];
                        for (int i = 0; i < blockCount; i++) {
                            binaryBlocks[i] = binaryText.substring(i * 8, (i + 1) * 8);
                        }
                        // 初始化一个StringBuilder来存储加密后的二进制块
                        StringBuilder encryptedText = new StringBuilder();

                        // 对每个8位的二进制块进行SDES加密
                        for (String block : binaryBlocks) {
                            cipherText = SDES.encrypt(block, key); // 假设SDES.encrypt接受8位的二进制输入
                            encryptedText.append(cipherText);
                        }

                        // 将加密后的二进制块转换为ASCII码形式
                        StringBuilder asciiCipherText = new StringBuilder();
                        for (int i = 0; i < encryptedText.length(); i += 8) {
                            String binaryChar = encryptedText.substring(i, i + 8);
                            int asciiValue = Integer.parseInt(binaryChar, 2);
                            char asciiChar = (char) asciiValue;
                            asciiCipherText.append(asciiChar);
                        }

                        resulTextArea.setText(asciiCipherText.toString());
                    }
                }
            }
            // 定义一个辅助方法来验证二进制输入是否有效
            private boolean isValidBinary(String input) {
                // 使用正则表达式验证是否是8位二进制输入
                return input.matches("[01]{8}");
            }
            // 定义一个辅助方法来验证二进制密钥是否有效
            private boolean isValidBinaryKey(String input) {
                // 使用正则表达式验证是否是10位二进制输入
                return input.matches("[01]{10}");
            }
        });


        button1.setBounds(265, 500, 160, 40);

        //返回按钮
        button2 = new JButton("返回");
        button2.setFont(new Font("黑体", Font.PLAIN, 19));
        button2.setSize(new Dimension(100,50));
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                UI uI = new UI();
                frame.dispose();
            }
        });
        button2.setBounds(465, 500, 160, 40);

        //布置各个组件
        box_H_01.add(dataJLabel);
        box_H_01.add(Box.createHorizontalStrut(5));
        box_H_01.add(dataField);

        box_H_02.add(keyJLabel);
        box_H_02.add(Box.createHorizontalStrut(5));
        box_H_02.add(keyJPasswordField);

        box_H_03.add(resultJLabel);
        box_H_03.add(Box.createHorizontalStrut(5));
        box_H_03.add(resulTextArea);

        box_V.add(box_H_01);
        box_V.add(Box.createVerticalStrut(40));
        box_V.add(box_H_02);
        box_V.add(Box.createVerticalStrut(30));
        box_V.add(box_H_03);
        box_V.setBounds(200, 170, 450, 300);

        frame.add(welcomeLabel);
        frame.add(box_V);
        frame.add(checkBox);
        frame.add(button1);
        frame.add(button2);
        frame.add(textModeRadioButton);
        frame.add(asciiModeRadioButton);
        frame.setVisible(true);
    }
}
