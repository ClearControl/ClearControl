package rtlib.nidevices.tc01.bridj;

import org.bridj.BridJ;
import org.bridj.CRuntime;
import org.bridj.Pointer;
import org.bridj.ann.CLong;
import org.bridj.ann.Library;
import org.bridj.ann.Name;
import org.bridj.ann.Ptr;
import org.bridj.ann.Runtime;

/**
 * Wrapper for library <b>TC01lib</b><br>
 * This file was autogenerated by <a
 * href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a
 * href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few
 * opensource projects.</a>.<br>
 * For help, please visit <a
 * href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a
 * href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("TC01lib")
@Runtime(CRuntime.class)
public class TC01libLibrary
{
	static
	{
		BridJ.register();
	}

	/**
	 * TC01lib<br>
	 * Original signature : <code>double TC01lib(int32_t)</code><br>
	 * <i>native declaration :
	 * C:\Users\myerslab\workspace\RTlibNIDevices\labview\TC01\lib\TC01lib.h:9</i>
	 */
	@Name("TC01lib")
	native public static double tC01lib(int thermocoupleType);

	/**
	 * Original signature : <code>long LVDLLStatus(char*, int, void*)</code><br>
	 * <i>native declaration :
	 * C:\Users\myerslab\workspace\RTlibNIDevices\labview\TC01
	 * \lib\TC01lib.h:11</i>
	 */
	@Name("LVDLLStatus")
	@CLong
	public static long lVDLLStatus(	Pointer<Byte> errStr,
																	int errStrLen,
																	Pointer<?> module)
	{
		return lVDLLStatus(	Pointer.getPeer(errStr),
												errStrLen,
												Pointer.getPeer(module));
	}

	@Name("LVDLLStatus")
	@CLong
	protected native static long lVDLLStatus(	@Ptr long errStr,
																						int errStrLen,
																						@Ptr long module);
}
