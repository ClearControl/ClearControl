package rtlib.nifpga.bridj;

import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;

/**
 * <i>native declaration :
 * C:\Users\myerslab\workspace\Direttore\labview\lib\Direttore.h</i><br>
 * This file was autogenerated by <a
 * href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a
 * href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few
 * opensource projects.</a>.<br>
 * For help, please visit <a
 * href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a
 * href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("Direttore")
public class TD1 extends StructObject
{
	public TD1()
	{
		super();
	}

	@Field(0)
	public byte status()
	{
		return this.io.getByteField(this, 0);
	}

	@Field(0)
	public TD1 status(byte status)
	{
		this.io.setByteField(this, 0, status);
		return this;
	}

	@Field(1)
	public int code()
	{
		return this.io.getIntField(this, 1);
	}

	@Field(1)
	public TD1 code(int code)
	{
		this.io.setIntField(this, 1, code);
		return this;
	}

	// / C type : LStrHandle
	@Field(2)
	public Pointer<Pointer<LStrHandleStruct>> source()
	{
		return this.io.getPointerField(this, 2);
	}

	// / C type : LStrHandle
	@Field(2)
	public TD1 source(Pointer<Pointer<LStrHandleStruct>> source)
	{
		this.io.setPointerField(this, 2, source);
		return this;
	}

	public TD1(Pointer pointer)
	{
		super(pointer);
	}
}
