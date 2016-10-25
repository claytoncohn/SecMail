package edu.depaul.secmail;

import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



@SuppressWarnings("unused")
public class Client {
	int port_number;
	InetAddress loopback;
	Socket client_socket;
	PrintWriter output;
	BufferedReader input;
	
	static byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }; //initialization vector, should not remain 0s
    static IvParameterSpec ivspec = new IvParameterSpec(iv);
	
	
	
	public Client(){
		
		
		
	}
	
	//Clayton Newmiller
	//This method currently requires a key length of 8 chars and a message length of 16 chars... will figure out padding later
	private static byte[] SecMailEncryptAES(String message, String key){
				
		byte cipherText[] = null;
		byte keyBytes[] = ConvertStringToByteArray(key);
		byte messageBytes[] = ConvertStringToByteArray(message);
		
		try {
			SecretKeySpec keyspec = new SecretKeySpec(keyBytes, "AES");
			Cipher c = Cipher.getInstance("AES/CBC/NoPadding");
			c.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			cipherText = c.doFinal(messageBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return cipherText;
	}
	
	//Clayton Newmiller
	private static String SecMailDecryptAES(byte[] cipherText, String key){ //returns true if successful
		
		String message = null;
		byte keyBytes[] = ConvertStringToByteArray(key);
		
		
		try {
			SecretKeySpec keyspec = new SecretKeySpec(keyBytes, "AES");
			Cipher c = Cipher.getInstance("AES/CBC/NoPadding");
			c.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			message = ConvertByteArrayToString(c.doFinal(cipherText));
			
			
		} catch (NoSuchAlgorithmException e) {
			System.out.println("No such algorithm exception: " + e.getLocalizedMessage());
			System.out.println("Stack trace: " + e.printStackTrace());
		} catch (NoSuchPaddingException e) {
			System.out.println("No such padding exception: " + e.getLocalizedMessage());
			System.out.println("Stack trace: " + e.printStackTrace());
		} catch (InvalidKeyException e) {
			System.out.println("Invalid key exception : " + e.getLocalizedMessage());
			System.out.println("Stack trace: " + e.printStackTrace());
		} catch (IllegalBlockSizeException e) {
			System.out.println("Illegal block size exception: " + e.getLocalizedMessage());
			System.out.println("Stack trace: " + e.printStackTrace());
		} catch (BadPaddingException e) {
			System.out.println("Bad padding exception: " + e.getLocalizedMessage());
			System.out.println("Stack trace: " + e.printStackTrace());
		} catch (InvalidAlgorithmParameterException e) {
			System.out.println("Invalid algorithm parameter exception: " + e.getLocalizedMessage());
			System.out.println("Stack trace: " + e.printStackTrace());
		} catch (IOException e) {
			System.out.println("IO exception: " + e.getLocalizedMessage());
			System.out.println("Stack trace: " + e.printStackTrace());
		}
		return message;
	}
	
	
	//Clayton Newmiller
	//Note: Java chars are unicode, 2 bytes long
	private static byte[] ConvertStringToByteArray(String input){
		byte ret[] = new byte[input.length()*2];
		for (int i=0; i<ret.length;i+=2){
			char c = input.charAt(i/2);
			byte l= (byte) (c>>>8);
			byte r =(byte) (c);
			ret[i]=l;
			ret[i+1]=r;
		}
		return ret;
	}
	
	//Clayton Newmiller
	private static String ConvertByteArrayToString(byte input[]) throws IOException { //returns 4 unicode chars in a string
		if (input == null || input.length%8!=0){
			throw new IOException("wrong size input");
		}
		StringBuilder s=new StringBuilder();
		byte split [][] = new byte[input.length/8][8];
		for (int i=0; i<input.length/8 ;i++){
			for (int j=0; j<8;j++){
				split[i][j] = input[i*8+j];
			}
		}
		
		for (int i=0;i<input.length/8;i++){
			int xL = ((split[i][0]<<24) | (split[i][1]<<16 & 0x00ff0000) | (split[i][2]<<8& 0x0000ff00) | (split[i][3]& 0x000000ff) );
			int xR = ((split[i][4]<<24) | (split[i][5]<<16 & 0x00ff0000) | (split[i][6]<<8& 0x0000ff00) | (split[i][7]& 0x000000ff) );
			char[] ret = new char[4];
			ret[0] = (char) (xL>>>16);
			ret[1] = (char) (xL);
			ret[2] = (char) (xR>>>16);
			ret[3] = (char) (xR);
			for (int j=0;j<4;j++){
				s.append(ret[j]);
			}
		}
		return s.toString();
	}
	
	
	
	
	
	public static void main(String[] args) {
		
		Client test = new Client();
		
		
		byte ciphertext[] = test.SecMailEncryptAES("FAT CAT NOT CATS", "12345678");
		String changed = test.SecMailDecryptAES(ciphertext, "12345678");
		
		System.out.println(changed);
		
		
		

	}

}
