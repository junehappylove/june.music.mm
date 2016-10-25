/**
 * @author : Paul Taylor
 * <p/>
 * Version @version:$Id: InvalidAudioFrameException.java,v 1.1 2007/08/07 16:09:42 paultaylor Exp $
 * Date :${DATE}
 * <p/>
 * Jaikoz Copyright Copyright (C) 2003 -2005 JThink Ltd
 */
package com.judy.audiotag.audio.exceptions;

/**
 * Thrown if portion of file thought to be an AudioFrame is found to not be.
 */
public class InvalidAudioFrameException extends Exception
{
    /**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = -8451864588420295250L;

	public InvalidAudioFrameException(String message)
    {
        super(message);
    }
}
