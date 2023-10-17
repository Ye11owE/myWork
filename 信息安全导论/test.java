import java.util.Scanner;
import java.text.DecimalFormat;

public class test {
    public static void main(String[] args) {
        System.out.println("已知明密文对的数量");
        Scanner scan = new Scanner(System.in);
        int number = scan.nextInt();//记录明密文对数量
        String[] PlaintextArr = new String[number];//记录明文
        String[] CiphertextArr = new String[number];//记录密文
        int[][] keyarray = new int[10][10];//记录可能的密钥
        int keynum = 0;//记录密钥数量

        int inputcount = 0;
        while (inputcount<number){
            System.out.print("请输入第"+(inputcount+1)+"条明文");
            PlaintextArr[inputcount] = scan.next();
            System.out.print("请输入第"+(inputcount+1)+"条密文");
            CiphertextArr[inputcount] = scan.next();
            inputcount++;
        }
        // 获取当前时间的毫秒数，作为起始时间
        long startTime = System.currentTimeMillis();

        int [] key = new int [10];
        for(int n =0;n<number;n++){
            if(keynum==0){
                for(int i=0;i<1024;i++){
                    String binary = Integer.toBinaryString(i);
                    int length = binary.length();

                    while(length<10){
                        binary = "0" + binary;
                        length++;
                    }
                    for(int j=0;j<10;j++){
                        key[j] = binary.charAt(j) - '0';
                    }
                    String test = SDES.encrypt(PlaintextArr[n],key);
                    if(test.equals(CiphertextArr[n])){//如果生成的密文和存储的密文相同
                        for(int m = 0;m<10;m++){
                            keyarray[keynum][m] = key[m];
                        }
                        keynum++;
                    }
                }
            }

            else{
                int update = 0;
                int [][]temp = new int[keynum][10];
                for(int i=0;i<keynum;i++){
                    for(int j=0;j<10;j++){
                         key[j] =keyarray[i][j];
                    }
                    String test = SDES.encrypt(PlaintextArr[n],key);
                    if(test.equals(CiphertextArr[n])){
                        for(int m=0;m<10;m++){
                            temp[update][m] = key[m];
                        }
                        update++;
                    }
                }
                keyarray = temp;
                keynum = update;
            }
        }

        // 获取当前时间的毫秒数，作为结束时间
        long endTime = System.currentTimeMillis();
        // 计算代码块的运行时间（毫秒）
        long executionTime = endTime - startTime;
        System.out.println("可能的密钥如下：");
        for(int i=0;i<keynum;i++){
            for(int j=0;j<10;j++){
                System.out.print(keyarray[i][j]);
            }
            System.out.println();
        }
        System.out.println("起始时间为：");
        System.out.println(startTime);
        System.out.println("终止时间为：");
        System.out.println(endTime);
        System.out.println("运行时间为：");
        System.out.println( executionTime+ " 毫秒");

        scan.close();
    }
}
