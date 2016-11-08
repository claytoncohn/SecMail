package edu.depaul.secmail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

//debug connection: 127.0.0.1:57890


public class SecMailStaticEncryption {

	private static byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }; //AES initialization vector, should not remain 0s
    private static IvParameterSpec ivspec = new IvParameterSpec(iv);
    private static final String ENCRYPTIONSPEC = "Rijndael/CBC/PKCS5Padding"; //

    
    public static void encryptText(String text, byte[] key) {
    	byte[] strEncrypt = SecMailEncryptAES(text, key);
    	try {
    		PrintWriter out = new PrintWriter("secmail.txt");
    	    out.println(strEncrypt);
    	    out.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static void encryptFile(File file, byte[] key) throws IOException {
    	//TODO
    	
    }
    
    static String fileToString(String path, Charset encoding) throws IOException {  
    	byte[] encoded = Files.readAllBytes(Paths.get(path));
    	System.out.println(new String(encoded, encoding));
    	return new String(encoded, encoding);
    }
    
    //following java implementation here: http://www.java2s.com/Tutorial/Java/0490__Security/ImplementingtheDiffieHellmankeyexchange.htm
    //									  http://www.java2s.com/Tutorial/Java/0490__Security/DiffieHellmanKeyAgreement.htm
    
    public static class DHKeyServer implements Runnable{
    	private BigInteger Modulo; //also known as p
    	private BigInteger Base; //also known as generator, g
    	private int port;
    	private int bitLen;
    	private Socket clientSocket;
    	private KeyAgreement serverKeyAgree;
    	private KeyPair serverPair;
    	private KeyPairGenerator kpg;
    	private byte key[];
    	
    	private InputStream clientIn;
    	private OutputStream clientOut;
    	private PrintWriter out;
    	private BufferedReader in;
    	
    	public byte[] getKey() {
			return key;
		}
    	
    	public void run(){
    		this.serverInit();
    		this.key = this.doExchange();
    	}
    	
    	public DHKeyServer(Socket client, int BitLen){
    	    this.clientSocket = client;
    	    this.bitLen = BitLen;
    	    this.key = null;
    	    
    	}
    	
    	
    	public void serverInit(){
    		SecureRandom rnd = new SecureRandom();
    	    this.Modulo = BigInteger.probablePrime(bitLen, rnd);
    	    this.Base = BigInteger.probablePrime(bitLen, rnd);
    	    //send these over the socket
    	    
    	    try {
    	    	
    	    	this.kpg = KeyPairGenerator.getInstance("DiffieHellman");
    	    	kpg.initialize(bitLen);
    	    	this.serverKeyAgree = KeyAgreement.getInstance("DH");
    	    	this.serverPair = kpg.generateKeyPair();
    	    	
    	    	
    	    	this.clientIn = clientSocket.getInputStream();
    	    	this.clientOut = clientSocket.getOutputStream();
    	    	this.out = new PrintWriter(clientSocket.getOutputStream());
    	    	this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    	    	    	    	
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
    		
	    }
    	
    	public byte[] doExchange(){
    		
    		try{
    			
    			//sends the server's generated key to the client
    			
    			byte serverPublicKey[] = serverPair.getPublic().getEncoded();    			
    			int len = serverPublicKey.length;
    			clientOut.write(len);
    			clientOut.flush();    			
    			clientOut.write(serverPublicKey);
    			clientOut.flush();
				
    			
    			//receives the key the client generated
    			
			    int clientPubKeyLen = clientIn.read();
			    byte clientPubKeyBytes[] = new byte[clientPubKeyLen];
			    clientIn.read(clientPubKeyBytes, 0, clientPubKeyLen);

			    
			    //decodes the client's key
			    
			    KeyFactory kf = KeyFactory.getInstance("DiffieHellman");
			    X509EncodedKeySpec ks = new X509EncodedKeySpec(clientPubKeyBytes);
			    PublicKey clientPubKey = kf.generatePublic(ks);
			    
			    
			    //does the Diffie-Hellman operations
			    serverKeyAgree.init(serverPair.getPrivate());
			    serverKeyAgree.doPhase(clientPubKey, true);
			    return serverKeyAgree.generateSecret();
			    
			    
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
    		
    		return null;
    		
    	}
    	
    	
    	
    }
    
    	
    //Clayton Newmiller
    public static class DHKeyClient implements Runnable{
    	private int port;
    	private Socket serverSocket;
    	private InputStream serverIn;
    	private OutputStream serverOut;
    	private KeyPairGenerator kpg;
    	private byte key[];
    	
    	
    	
    	public void run(){
    		this.clientInit();
    		this.key = this.doExchange();
    	}
    	
    	public DHKeyClient(Socket s){
    	    this.serverSocket = s;
    	    
    	}
    	
    	public void clientInit(){
    	    try {
    	    	this.serverIn = serverSocket.getInputStream();
    	    	this.serverOut = serverSocket.getOutputStream();
				this.kpg = KeyPairGenerator.getInstance("DiffieHellman");
				this.key = null;
			    
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
    		
    		
	    }
    	public byte[] doExchange(){
    		try{
    			
    			//reads the server's key
				int serverPubKeyLen = serverIn.read();
				byte serverPubKeyBytes[] = new byte[serverPubKeyLen];
				this.serverIn.read(serverPubKeyBytes, 0, serverPubKeyLen);
				
				//decodes the server key and finds its parameters
				X509EncodedKeySpec ks = new X509EncodedKeySpec(serverPubKeyBytes);
				KeyFactory kf = KeyFactory.getInstance("DH");
				PublicKey serverPubKey = kf.generatePublic(ks);
				DHParameterSpec serverParameters = ((DHPublicKey)serverPubKey).getParams();
				kpg.initialize(serverParameters);
				
				//generates client key
				KeyAgreement clientKeyAgreement = KeyAgreement.getInstance("DH");
			    KeyPair clientPair = kpg.generateKeyPair();
			    clientKeyAgreement.init(clientPair.getPrivate());
			    //sends client key back to server
			    byte clientPublicKey[] = clientPair.getPublic().getEncoded();
			    serverOut.write(clientPublicKey.length);
			    serverOut.flush();
			    serverOut.write(clientPublicKey, 0, clientPublicKey.length);
			    serverOut.flush();
			    
			    //does the Diffie-Hellman operations
			    KeyAgreement clientKeyAgree = KeyAgreement.getInstance("DH");
			    clientKeyAgree.init(clientPair.getPrivate());
			    clientKeyAgree.doPhase(serverPubKey, true);
			    
			    return clientKeyAgree.generateSecret();
			    
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
    		
    		return null;
    	}


    	public byte[] getKey() { return key; }    		
    }
    
    
    
	//Clayton Newmiller
	//This method currently requires a key length of 8 chars and a message length of 16 chars... will figure out padding later
	public static byte[] SecMailEncryptAES(String message, byte keyBytes[]){
				
		byte cipherText[] = null;
		byte messageBytes[] = ConvertStringToByteArray(message);
		
		try {
			SecretKeySpec keyspec = new SecretKeySpec(keyBytes, "AES");
			Cipher c = Cipher.getInstance(ENCRYPTIONSPEC);
			c.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			cipherText = c.doFinal(messageBytes);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return cipherText;
	}
	
	//Clayton Newmiller
	public static String SecMailDecryptAES(byte[] cipherText, byte keyBytes[]){ //returns true if successful
		
		String message = null;
		
		try {
			SecretKeySpec keyspec = new SecretKeySpec(keyBytes, "AES");
			Cipher c = Cipher.getInstance(ENCRYPTIONSPEC);
			c.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			message = ConvertByteArrayToString(c.doFinal(cipherText));
			
			
		} catch (Exception e) {
			e.printStackTrace(); //this already prints, no need to call System.out.println
		}
		return message;
	}
	
	//Clayton Newmiller
	//Note: Java chars are unicode, 2 bytes long
	public static byte[] ConvertStringToByteArray(String input){
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
	public static String ConvertByteArrayToString(byte input[]) throws IOException { //returns 4 unicode chars in a string
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
	
	public static SealedObject encryptObject(Serializable object, byte keyBytes[]){
		SecretKeySpec keyspec = new SecretKeySpec(keyBytes, "AES");
		Cipher c=null;
		SealedObject encryptedPacket=null;
		try {
			c = Cipher.getInstance(ENCRYPTIONSPEC);
			c.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			encryptedPacket = new SealedObject(object, c);
			
			return encryptedPacket;
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Serializable decryptObject(SealedObject object, byte keyBytes[]){ //needs to be cast to what it actually is
		SecretKeySpec keyspec = new SecretKeySpec(keyBytes, "AES");
		Cipher c=null;
		Serializable decryptedObject=null;
		try {
			c = Cipher.getInstance(ENCRYPTIONSPEC);
			c.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			decryptedObject = (Serializable) object.getObject(c); //problem here: improperly padded - WRONG KEYS
			
			/*
			 * key exchange not functioning
			 * encrypt and decrypt functioning PERFECTLY
			 * 
			 * need to: somehow get information on key exchanges (use Log.out? ask jacob)
			 * 			verify hashing algorithm is reliable
			 * 
			 * */
			
			
			return decryptedObject;
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
	
	
	public static void main(String[] args) {	
		
		
		
		String s = "hello";
		byte[] key = ConvertStringToByteArray("12345678");
		SealedObject enc = encryptObject(s, key);
		String b = (String) decryptObject(enc, key);
		System.out.println(b);
		
		encryptText("hello world", key);

	}

}
