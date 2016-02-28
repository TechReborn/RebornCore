package chylex.reborncore.javacheck.report;
import java.io.PrintStream;
import java.io.PrintWriter;

public final class OutdatedJavaException extends RuntimeException{
	public OutdatedJavaException(){
		setStackTrace(new StackTraceElement[0]);
	}
	
	@Override
	public StackTraceElement[] getStackTrace(){
		return new StackTraceElement[0];
	}
	
	@Override
	public void printStackTrace(PrintStream s){}
	
	@Override
	public void printStackTrace(PrintWriter w){}
}
