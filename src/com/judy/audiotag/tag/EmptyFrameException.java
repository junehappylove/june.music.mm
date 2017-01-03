/*
 *  @author : Paul Taylor
 *  @author : Eric Farng
 *
 *  Version @version:$Id: EmptyFrameException.java,v 1.3 2006/08/25 15:35:13 paultaylor Exp $
 *
 *  MusicTag Copyright (C)2003,2004
 *
 *  This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 *  General Public  License as published by the Free Software Foundation; either version 2.1 of the License,
 *  or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 *  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 *  you can get a copy from http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.judy.audiotag.tag;

/**
 * Thrown when find a Frame but it contains no data.
 * 
 * @version $Revision: 1.3 $
 */
public class EmptyFrameException extends InvalidFrameException {
	/**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = -3830439162327200522L;

	/**
	 * Creates a new EmptyFrameException datatype.
	 */
	public EmptyFrameException() {
	}

	/**
	 * Creates a new EmptyFrameException datatype.
	 * 
	 * @param ex
	 *            the cause.
	 */
	public EmptyFrameException(Throwable ex) {
		super(ex);
	}

	/**
	 * Creates a new EmptyFrameException datatype.
	 *
	 * @param msg
	 *            the detail message.
	 */
	public EmptyFrameException(String msg) {
		super(msg);
	}

	/**
	 * Creates a new EmptyFrameException datatype.
	 * 
	 * @param msg
	 *            the detail message.
	 * @param ex
	 *            the cause.
	 */
	public EmptyFrameException(String msg, Throwable ex) {
		super(msg, ex);
	}
}
