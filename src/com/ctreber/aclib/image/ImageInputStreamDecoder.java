package com.ctreber.aclib.image;

import java.io.IOException;

import javax.imageio.stream.ImageInputStream;

import com.ctreber.aclib.codec.AbstractDecoder;

/**
 * <p>
 * File decoder based on ImageInputStream (Chris' implementation).
 * </p>
 * &copy; 2001 Christian Treber, ct@ctreber.com
 * 
 * @author Christian Treber, ct@ctreber.com
 */
public class ImageInputStreamDecoder extends AbstractDecoder {
	private final ImageInputStream _stream;

	/**
	 * Create a BIG_ENDIAN file decoder. See
	 * {@link AbstractDecoder#setEndianess}to change the default behavior.
	 * 
	 * @param pStream
	 *            The image input stream to read from.
	 */
	public ImageInputStreamDecoder(final ImageInputStream pStream) {
		super();
		_stream = pStream;
	}

	public void seek(final long pPos) throws IOException {
		_stream.seek(pPos);
	}

	public byte[] readBytes(final long pBytes, final byte[] pBuffer) throws IOException {
		byte[] lBuffer = pBuffer;
		if (lBuffer == null) {
			lBuffer = new byte[(int) pBytes];
		} else {
			if (lBuffer.length < pBytes) {
				throw new IllegalArgumentException("Insufficient space in buffer");
			}
		}

		final int lBytesRead = _stream.read(pBuffer, 0, (int) pBytes);
		if (lBytesRead != pBytes) {
			throw new IOException("Tried to read " + pBytes + " bytes, but obtained " + lBytesRead);
		}

		_pos += pBytes;

		return lBuffer;
	}

	public void close() throws IOException {
		_stream.close();
	}
}
