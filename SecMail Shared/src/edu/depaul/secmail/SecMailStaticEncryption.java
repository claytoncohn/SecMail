package edu.depaul.secmail;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SealedObject;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

//debug connection: 127.0.0.1:57890

public class SecMailStaticEncryption {
	
	private static String filePath;

	private static byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }; //AES initialization vector, should not remain 0s
    private static IvParameterSpec ivspec = new IvParameterSpec(iv);
    private static final String ENCRYPTIONSPEC = "AES/CBC/PKCS5Padding"; //
    
    //MARK: - Text Encryption
    public static void encryptText(String text, byte[] key) throws IOException {
    	byte[] encryptedText = SecMailEncryptAES(text, key);
    	File tempFile = File.createTempFile("smTEXT", ".tmp", null);
    	FileOutputStream fileOut = new FileOutputStream(tempFile);
    	fileOut.write(encryptedText);
    	fileOut.close();
    	filePath = tempFile.getAbsolutePath();
    	System.out.println("Text to encrypt: " + text);
    	System.out.println("Text encrypted: " + ConvertByteArrayToString(encryptedText));
    	System.out.println("File path of encrypted text: " + filePath);
    }
    
    public static String readFile(String filename) throws IOException{
        String content = null;
        File file = new File(filename); 
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader !=null){reader.close();}
        }
        return content;
    }
    
    public static String decryptText(String filePathString, byte[] key) throws IOException {
    	String encryptedText = readFile(filePathString);
    	System.out.println("Encrypted text read from temp file: " + encryptedText);
    	byte[] cipherText = ConvertStringToByteArray(encryptedText);
    	String decryptedText = SecMailDecryptAES(cipherText, key);
    	System.out.println("Decrypted Text: " + decryptedText);
    	return decryptedText;
    }
    
//    //MARK: - File Encryption
//    public static void encryptFile(File file, byte[] key) throws IOException {
//    	SealedObject obj = encryptObject(file, key);
//    	File tempFile = File.createTempFile("smFILE", ".tmp", null);
//    	FileOutputStream fileOut = new FileOutputStream(tempFile);
//    	ObjectOutputStream opStream = new ObjectOutputStream(fileOut);
//    	opStream.writeObject(obj);
//    	opStream.close();
//    }
//    
//    public static File decryptFile(File file, byte[] key) throws IOException {
//    	//TODO
//    }
    
    //following java implementation here: http://www.java2s.com/Tutorial/Java/0490__Security/ImplementingtheDiffieHellmankeyexchange.htm
    //									  http://www.java2s.com/Tutorial/Java/0490__Security/DiffieHellmanKeyAgreement.htm
    
    public static class DHKeyServer implements Runnable{
    	private BigInteger Modulo; //also known as p
    	private BigInteger Base; //also known as generator, g
    	private int bitLen;
    	private Socket clientSocket;
    	private KeyAgreement serverKeyAgree;
    	private KeyPair serverPair;
    	private KeyPairGenerator kpg;
    	private byte key[];
    	
    	private InputStream clientIn;
    	private DataOutputStream clientDataOut;
    	private OutputStream clientOut;
    	
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
    	    	
    	    	this.clientDataOut = new DataOutputStream(clientOut);
    	    	
    	    	    	    	
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
    			
    			clientDataOut.writeInt(len);
    			clientDataOut.flush();    			
    			clientDataOut.write(serverPublicKey);
    			clientDataOut.flush();
    			
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
			    byte temp[] = serverKeyAgree.generateSecret();
			    
			    return temp;
			    
			    
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
    		
    		return null;
    		
    	}
    	
    	
    	
    }
    
    	
    //Clayton Newmiller
    public static class DHKeyClient implements Runnable{
    	private Socket serverSocket;
    	private InputStream serverIn;
    	//DataOutputStream readInt()
    	private DataInputStream serverDataIn;
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
    			this.serverDataIn= new DataInputStream(serverIn);
    			//reads the server's key
				int serverPubKeyLen = serverDataIn.readInt();
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
			    serverOut.write(clientPublicKey);
			    serverOut.flush();
			    
			    //does the Diffie-Hellman operations
			    KeyAgreement clientKeyAgree = KeyAgreement.getInstance("DH");
			    clientKeyAgree.init(clientPair.getPrivate());
			    clientKeyAgree.doPhase(serverPubKey, true);
			    byte temp[] = clientKeyAgree.generateSecret();
			    
			    return temp;			    
			    
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
    		
    		return null;
    	}


    	public byte[] getKey() {
			return key;
		}


    		
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
			throw new IOException("wrong size input: "+input.length);
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

	
	
	
	public static void main(String[] args) throws IOException {	
		
		encryptText("this is the encrypted text", iv);
		decryptText(filePath, iv);
//		String s = "hello";
//		byte[] key = ConvertStringToByteArray("12345678");
//		SealedObject enc = encryptObject(s, key);
//		String b = (String) decryptObject(enc, key);
//		System.out.println(b);
		
		

	}

}
