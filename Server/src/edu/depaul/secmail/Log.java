package edu.depaul.secmail;

public class Log {
	//member vars here
	private final static boolean DEBUG = false;
	
	//constructor
	Log() {
		
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
		//TODO implement this function.
		System.out.println(out);
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
