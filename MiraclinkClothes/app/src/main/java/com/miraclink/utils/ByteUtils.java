package com.miraclink.utils;

public class ByteUtils {


    public static byte[] getCmdStart(int type, int status, int code) {
        byte[] mData = new byte[]{(byte) 0xAE, (byte) type, (byte) status, (byte) code};
        return mData;
    }

    public static byte[] getCmd() {
        byte[] mData = new byte[]{(byte) 0xAE, 0x01, 0x0A, 0x14, 0x1E, 0x28, 0x32, 0x3C, 0x46, 0x01, 0x1A};
        return mData;
    }

    public static byte[] getTestCmd() {
        byte[] mData = new byte[]{(byte) 0xAE, 0x01, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x05, 0x64};
        return mData;
    }

    public static byte[] getRateCmd(int i,int strong) {
        if (i >= 0 && i<=10){
            int rate = i*10;
            byte[] mData = new byte[]{(byte) 0xAE, 0x01, intToByte(rate), intToByte(rate), intToByte(rate), intToByte(rate), intToByte(rate), intToByte(rate), intToByte(rate), 0x01,
                    (byte) ((intToByte(rate)*7 +0x01+0x01)&0xFF)};
            return mData;
        }else {
            return new byte[]{(byte) 0xAE, 0x01, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x05, 0x64};  // exception rate
        }
    }


    //TODO strong will get
    public static byte[] getRateCmd(int armIo,int chestIo,int stomachIo,int legIo,int neckIo,int backIo,int rearIo,int strong){
        if (armIo >=0 && armIo <=10){
            byte[] data = new byte[]{(byte) 0xAE, 0x01,intToByte(armIo *10),intToByte(chestIo*10),intToByte(stomachIo*10),intToByte(legIo*10),intToByte(neckIo*10),
                    intToByte(backIo*10),intToByte(rearIo*10),0x01,(byte) ((intToByte(armIo *10)+ intToByte(chestIo*10)+intToByte(stomachIo*10)+intToByte(legIo*10)+
                    intToByte(neckIo*10)+intToByte(backIo*10)+intToByte(rearIo*10)+0x01+0x01)&0xFF)};
            return data;
        }else {
            LogUtil.i("ByteUtils","cmd exception");
            return new byte[]{(byte) 0xAE, 0x01, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x32, 0x05, 0x64};  // exception rate
        }

    }

    // leg 大腿    arm 臂     chest 胸膛      stomach 腹部     neck 脖子    back 背部      rear 屁股
    //public static byte[] getFunRateCmd(boolean leg,boolean arm,boolean chest,boolean stomach,boolean neck,boolean back,boolean rear){

    //}


    /**
     * 以字符串表示形式返回字节数组的内容
     */
    public static String toHexString(byte[] bytes) {
        if (bytes == null)
            return "null";
        int iMax = bytes.length - 1;
        if (iMax == -1)
            return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(String.format("%02x", bytes[i] & 0xFF));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    //比较byte[]
    public static boolean isEqual(byte[] dataa, byte[] datab) {
        if (dataa == datab) return true;
        if (dataa == null || datab == null) {
            return false;
        }
        if (dataa.length != datab.length) {
            return false;
        }

        int result = 0;
        // 时间开销为常数
        for (int i = 0; i < dataa.length; i++) {
            result |= dataa[i] ^ datab[i];// 先异或(相同为0,不同为1),再或(有一个1,就不为0)
        }
        return result == 0;
    }

    //xzx add int rate to byte
    public static byte intToByte(int i) {
        byte b = 0;
        switch (i) {
            case 10:
                b = 0x0A;
                break;
            case 20:
                b = 0x14;
                break;
            case 30:
                b = 0x1E;
                break;
            case 40:
                b = 0x28;
                break;
            case 50:
                b = 0x32;
                break;
            case 60:
                b = 0x3C;
                break;
            case 70:
                b = 0x46;
                break;
            case 80:
                b = 0x50;
                break;
            case 90:
                b = 0x5A;
                break;
            case 100:
                b = 0x64;
                break;
            default:
                b = 0;
                break;
        }
        return b;
    }
}
