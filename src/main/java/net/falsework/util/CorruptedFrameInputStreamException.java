package net.falsework.util;

/**
 * 破坏的Frame结构输入流异常类。
 * 
 * @author sea.bao
 */
public class CorruptedFrameInputStreamException extends RuntimeException {
	private static final long serialVersionUID = -9201967765301075546L;

	public CorruptedFrameInputStreamException() {
	}
	
	public CorruptedFrameInputStreamException(String msg) {
		super(msg);
	}
}
