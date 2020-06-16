package dh;

import RSA.RSATest;
import RSA.RSAUtils;
import org.apache.commons.codec.binary.Base64;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 *xiaoqianke
 *2020年6月16日17点02分
 */
public class DH{
    /*
     * 生成随机的大素数
     */
    public static long createRadomPrimeNumber(int   n){
        long recLong = 0;
        List list = listAllPrimeNumber(n);
        Random rd = new Random();
        int randomIndex = Math.abs( rd.nextInt()%list.size());
        recLong = ((Long)list.get(randomIndex)).longValue();
        return recLong;
    }
    public static List listAllPrimeNumber(int n){
        List list = new ArrayList();
        long low = (long)Math.pow(10,n-1);
        long high = (long)Math.pow(10,n) - 1;
        for(long i= low; i < high; i++){
            if(isPrimeNumber(i)){
                list.add(new Long(i));
            }
        }
        return   list;
    }
    /*
     *判断一个数是不是素数的函数
     */
    public static boolean isPrimeNumber(long x){
        boolean flag = true;
        if (x<2) {//素数不小于2
            return false;
        } else {
            for(int i = 2;i<=Math.sqrt(x);i++) {
                if (x % i == 0) {//若果能被整除则说明不是素数，返回false
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }
    private static boolean isProbablePrime(long x) {
        return true;
    }
    /*
     * 产生大的随机数
     */
    public static long createRandomNumber(int n){
        long recLong = 0;
        List list = listAllPrimeNumber(n);
        Random rd = new Random();
        int randomIndex = Math.abs( rd.nextInt()%list.size());
        recLong = ((Long)list.get(randomIndex)).longValue();
        return recLong;
    }
    public static List listAllNumber(int n){
        List list2 = new ArrayList();
        long low = (long)Math.pow(10,n-1);
        long high = (long)Math.pow(10,n) - 1;
        for(long  i= low; i < high; i++){
            list2.add(new Long(i));
        }
        return list2;
    }
    public static double largeMOD(long x,long y,long z) {
        if(y==1)
            return x%z;
        else {
            --y;
            return ((x%z) * largeMOD(x,y,z)) % z ;
        }
    }
    public static void main(String[] args){

        RSATest A_RSA = new RSATest();
        KeyPair AKeyPair = null;
        try {
            AKeyPair = A_RSA.getKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String A_pubKey = new String(Base64.encodeBase64(AKeyPair.getPublic().getEncoded()));
        String A_priKey = new String(Base64.encodeBase64(AKeyPair.getPrivate().getEncoded()));
        //A的非对称密钥对

        RSATest B_RSA = new RSATest();
        KeyPair BKeyPair = null;
        try {
            BKeyPair = B_RSA.getKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String B_pubKey = new String(Base64.encodeBase64(BKeyPair.getPublic().getEncoded()));
        String B_priKey = new String(Base64.encodeBase64(BKeyPair.getPrivate().getEncoded()));
        //B的非对称密钥对

        System.out.println("A和B约定一个4位的随机大素数p, 5位的随机大素数g");
        long longPrimeVar4_n = createRadomPrimeNumber(4);
        System.out.println("p="+longPrimeVar4_n);
        //共享的g
        long longPrimeVar5_g = createRadomPrimeNumber(5);
        System.out.println("g="+longPrimeVar5_g);
        //得到一个4位的随机大数
        long longVar4_x = createRandomNumber(3);
        System.out.println("A选择一个随机数a="+longVar4_x);
        //得到一个3位的随机大数
        long  longVar5_y = createRandomNumber(3);
        System.out.println("B选择一个随机数b="+longVar5_y);
        //计算A,B
        long A = (long)largeMOD(longPrimeVar5_g,longVar4_x,longPrimeVar4_n);
        System.out.println("A根据a计算出Ya="+A+"  发送给B");

        String BSign = null;//B的签名
        String encryptB = null;

        long B = (long)largeMOD(longPrimeVar5_g,longVar5_y,longPrimeVar4_n);
        long K2 = (long)largeMOD(A,longVar5_y,longPrimeVar4_n);
        System.out.println("B根据A计算出密匙K2="+K2);
        try {
            encryptB = B_RSA.encrypt(String.valueOf(B),B_RSA.getPublicKey(B_pubKey));
            BSign = B_RSA.sign(encryptB,B_RSA.getPrivateKey(B_priKey));//B的签名
            System.out.println("B进行签名：\n"+BSign+"\n并将签名发送给A");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("A对收到的签名进行验证：");
        try {
            Boolean Aresult = B_RSA.verify(encryptB,B_RSA.getPublicKey(B_pubKey),BSign);
            if(Aresult) {
                System.out.println("验证成功！");
            }else{
                System.out.println("验证失败！密钥协商不安全！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //计算K1，K2
        long K1 = (long)largeMOD(B,longVar4_x,longPrimeVar4_n);
        System.out.println("A根据B计算出密匙K1="+K1);

        String ASign = null;//A的签名
        String encryptA = null;
        try {
            encryptA  = A_RSA.encrypt(String.valueOf(A),A_RSA.getPublicKey(A_pubKey));
            ASign = A_RSA.sign(encryptA,A_RSA.getPrivateKey(A_priKey));
            System.out.println("A进行签名：\n"+ASign+"\n并将签名发送给B");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("B对A发来的签名进行验证：");
        try {
            Boolean Bresult = A_RSA.verify(encryptA,A_RSA.getPublicKey(A_pubKey),ASign);
            if(Bresult){
                System.out.println("验证成功！");
            }else {
                System.out.println("验证失败！密钥协商不安全！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //判断K1是否等于k2
        if(K1==K2) {
            System.out.println("K1=K2,密钥协商过程结束。");
        }
        else {
            System.out.println("error，密钥协商不安全！");
        }
    }
}