package com.gifisan.nio.component.protocol.future;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import com.gifisan.nio.component.BufferedOutputStream;
import com.gifisan.nio.component.IOEventHandle;

public interface ReadFuture extends Future {
	
	public abstract boolean flushed();
	
	public abstract IOEventHandle getIOEventHandle() ;

	public abstract OutputStream getOutputStream();

	public abstract String getServiceName();

	public abstract BufferedOutputStream getWriteBuffer();

	public abstract boolean hasOutputStream();

	public abstract void setInputStream(InputStream inputStream);
	
	public abstract void setIOEventHandle(IOEventHandle ioEventHandle) ;
	
	public abstract void setOutputStream(OutputStream outputStream);

	public abstract void write(byte b);

	public abstract void write(byte[] bytes);
	
	public abstract void write(byte[] bytes, int offset, int length);
	
	public abstract void write(String content);
	
	public abstract void write(String content, Charset encoding);
}
