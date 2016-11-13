package edu.depaul.secmail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Log {
	//member vars here
	private final static boolean DEBUG = true;
	private static PrintWriter logFile;
	
	//constructor
	Log() {
		
	}
	
	public static void Init(File file)
	{
		//create the FileWriter based on file
		PrintWriter w = null;
		try {
			w = new PrintWriter(new FileWriter(file));
		} catch (IOException e)	{
			System.err.println(e);
		} finally {
			logFile = w;
		}
		
		
	}
	
	//public methods
	public static void Out(String message)
	{
		String toOut = null;
		
		
		if (DEBUG)
			toOut = GetStackInfo();
		
		toOut = "Log: " + toOut + message;
		File_Output(toOut);
	}
	
	public static void Error(String message)
	{
		if (DEBUG)
			System.err.println(message);
		
		String toOut = "Error: " + GetStackInfo() + " " + message;
		File_Output(toOut);
	}
	
	public static void Debug(String message)
	{
		String toOut = "DEBUG: " + GetStackInfo() + " " + message;
		File_Output(toOut);
	}
	
	//private methods
	private static void File_Output(String out)
	{
		if (logFile == null)
		{
			System.err.println("Error: unable to write to logFile because logFile uninitialized.");
			System.err.println(out);
		}
		else
		{
			logFile.println(out);
			logFile.flush();
		}
		
	}
	
	// print debugging magic.
	private static String GetStackInfo()
	{
		String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
	    String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
	    String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
	    int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();
	    
	    return className + "." + methodName + "():" + lineNumber + ": ";
	}
}
