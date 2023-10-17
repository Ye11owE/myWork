public class SDES {
    // 定义S-DES算法所需要的置换表
    private static final int[] P10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
    private static final int[] P8 = {6, 3, 7, 4, 8, 5, 10, 9};
    private static final int[] IP = {2, 6, 3, 1, 4, 8, 5, 7};
    private static final int[] IPInverse = {4, 1, 3, 5, 7, 2, 8, 6};
    private static final int[] EP = {4, 1, 2, 3, 2, 3, 4, 1};
    private static final int[] P4 = {2, 4, 3, 1};

    //替换盒S-Box函数
    private static int[] sBoxSubstitution(int[] data){
        int[][] sBox1 = {
            {1, 0, 3, 2},
            {3, 2, 1, 0},
            {0, 2, 1, 3},
            {3, 1, 0, 2}
    };
        int[][] sBox2 = {
            {0, 1, 2, 3},
            {2, 3, 1, 0},
            {3, 0, 1, 2},
            {2, 1, 0, 3}
        };
        int[] output = new int[4];
        int row1 = data[0]*2+data[3];
        int col1 = data[1]*2+data[2];
        int output1 = sBox1[row1][col1];

        int row2 = data[4]*2+data[7];
        int col2 = data[5]*2+data[6];
        int output2 = sBox2[row2][col2];
        output[0]=output1/2;
        output[1]=output1%2;
        output[2]=output2/2;
        output[3]=output2%2;
        
        return output;
    }

    //S-DES的密钥生成函数
    private static int[] generateKey(int[] key, boolean isFirst){
        // 使用P10表对密钥进行置换
        int[] temp = new int[10];
        for(int i=0;i<10;i++){
            temp[i]=key[P10[i]-1];
        }

        int shiftAmount = isFirst ? 1:2;   //子密钥1左移一位，子密钥2左移两位
        // 循环左移
        int[] shiftedKey = new int[10];
        for(int i=0;i<10;i++){
            if(i<5){
                shiftedKey[i] = temp[(i+shiftAmount)%5];
            }
            else{
                shiftedKey[i] = temp[(i+shiftAmount)%5+5];
            }
        }

        //使用P8表生成子密钥
        int[] subKey = new int[8];
        for(int i=0;i<8;i++){
            subKey[i]=shiftedKey[P8[i]-1];
        }

        return subKey;
    }

    // S-DES加密函数
    public static String encrypt(String plainText, int[] key){
        int[] data = new int[8];
        for(int i=0;i<8;i++){
            data[i] = Character.getNumericValue(plainText.charAt(i));  //将数据从char转为int类型
        }

        // 生成子密钥
        int[] subKey1 = generateKey(key, true);
        int[] subKey2 = generateKey(key, false);

        //初始置换IP
        int [] permutedData = new int[8];
        for(int i=0;i<8;i++){
            permutedData[i]=data[IP[i]-1];
        }

        //将IP置换后的8bit分为左右两组
        int[] permutedDataLeft = new int[4];
        int[] permutedDataRight = new int[4];
        for(int i=0;i<4;i++){
            permutedDataLeft[i]=permutedData[i];
        }
        for(int i=4;i<8;i++){
            permutedDataRight[i-4]=permutedData[i];
        }
        
        //第一次执行轮函数F
        //(1)扩展置换
        int[] expandedData=new int[8];
        for(int i=0;i<8;i++){
            expandedData[i]=permutedDataRight[EP[i]-1];
        }
        
        //(2)轮密钥异或
        for(int i=0;i<8;i++){
            expandedData[i]=expandedData[i] ^ subKey1[i];
        }

        //(3)使用替换盒S-Box
        int[] substitutedData = sBoxSubstitution(expandedData);

        //(4)直接置换
        int[] permutedData4 = new int[4];
        for(int i=0;i<4;i++){
            permutedData4[i] = substitutedData[P4[i]-1];
        }

        //左部分原始数据和轮函数结果进行异或
        int[] temp1 = new int[4];
        for(int i=0;i<4;i++){
            temp1[i] = permutedDataLeft[i]^permutedData4[i];
        }

        //第二次执行轮函数F
        //(1)扩展置换
        expandedData = new int[8];
        for(int i=0;i<8;i++){
            expandedData[i]=temp1[EP[i]-1];
        }

        //(2)轮密钥异或
        for(int i=0;i<8;i++){
            expandedData[i]=expandedData[i] ^ subKey2[i];
        }

        //(3)使用替换盒S-Box
        substitutedData = sBoxSubstitution(expandedData);

        //(4)直接置换
        permutedData4 = new int[4];
        for(int i=0;i<4;i++){
            permutedData4[i] = substitutedData[P4[i]-1];
        }

        //右部分原始数据和轮函数结果进行异或
        int[] temp2 = new int[4];
        for(int i=0;i<4;i++){
            temp2[i]=permutedDataRight[i]^permutedData4[i];      //此时的密文为temp2+temp1(未进行最终置换)
        }

        //进行最终置换
        int[] tempData = new int[8];
        int[] cipherText = new int[8];
        for(int i=0;i<8;i++){
            if(i<4){
                tempData[i]=temp2[i];
            }
            else{
                tempData[i]=temp1[i-4];
            }
        }
        for(int i=0;i<8;i++){
            cipherText[i]=tempData[IPInverse[i]-1];
        }

        // 转换为 ASCII 字符串
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            result.append(cipherText[i]);
        }

        return result.toString();
    }

    //S-DES解密函数
    public static String decrypt(String ciphperText, int[] key){
        int[] data = new int[8];
        for(int i=0;i<8;i++){
            data[i] = Character.getNumericValue(ciphperText.charAt(i));  //将数据从char转为int类型
        }

        // 生成子密钥
        int[] subKey1 = generateKey(key, true);
        int[] subKey2 = generateKey(key, false);

        //初始置换IP
        int [] permutedData = new int[8];
        for(int i=0;i<8;i++){
            permutedData[i]=data[IP[i]-1];
        }

        //将IP置换后的8bit分为左右两组
        int[] permutedDataLeft = new int[4];
        int[] permutedDataRight = new int[4];
        for(int i=0;i<4;i++){
            permutedDataLeft[i]=permutedData[i];
        }
        for(int i=4;i<8;i++){
            permutedDataRight[i-4]=permutedData[i];
        }

        //第一次执行轮函数F
        //(1)扩展置换
        int[] expandedData=new int[8];
        for(int i=0;i<8;i++){
            expandedData[i]=permutedDataRight[EP[i]-1];
        }
        
        //(2)轮密钥异或
        for(int i=0;i<8;i++){
            expandedData[i]=expandedData[i] ^ subKey2[i];
        }

        //(3)使用替换盒S-Box
        int[] substitutedData = sBoxSubstitution(expandedData);

        //(4)直接置换
        int[] permutedData4 = new int[4];
        for(int i=0;i<4;i++){
            permutedData4[i] = substitutedData[P4[i]-1];
        }

        //左部分原始数据和轮函数结果进行异或
        int[] temp1 = new int[4];
        for(int i=0;i<4;i++){
            temp1[i] = permutedDataLeft[i]^permutedData4[i];
        }

        //第二次执行轮函数F
        //(1)扩展置换
        expandedData = new int[8];
        for(int i=0;i<8;i++){
            expandedData[i]=temp1[EP[i]-1];
        }

        //(2)轮密钥异或
        for(int i=0;i<8;i++){
            expandedData[i]=expandedData[i] ^ subKey1[i];
        }

        //(3)使用替换盒S-Box
        substitutedData = sBoxSubstitution(expandedData);

        //(4)直接置换
        permutedData4 = new int[4];
        for(int i=0;i<4;i++){
            permutedData4[i] = substitutedData[P4[i]-1];
        }

        //右部分原始数据和轮函数结果进行异或
        int[] temp2 = new int[4];
        for(int i=0;i<4;i++){
            temp2[i]=permutedDataRight[i]^permutedData4[i];      //此时的密文为temp2+temp1(未进行最终置换)
        }

        //进行最终置换
        int[] tempData = new int[8];
        int[] plainText = new int[8];
        for(int i=0;i<8;i++){
            if(i<4){
                tempData[i]=temp2[i];
            }
            else{
                tempData[i]=temp1[i-4];
            }
        }
        for(int i=0;i<8;i++){
            plainText[i]=tempData[IPInverse[i]-1];
        }

        // 转换为 ASCII 字符串
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            result.append(plainText[i]);
        }

        return result.toString();
    }

    
}
