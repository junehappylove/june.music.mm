package com.judy.audiotag.tag;

/**
 * Thrown if the key cannot be found
 *
 * <p>Shoudl not happen with well written code, hence RuntimeException.
 */
public class KeyNotFoundException extends RuntimeException
{
    /**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = -7009346494263662256L;

	/**
     * Creates a new KeyNotFoundException datatype.
     */
    public KeyNotFoundException()
    {
    }

    /**
     * Creates a new KeyNotFoundException datatype.
     *
     * @param ex the cause.
     */
    public KeyNotFoundException(Throwable ex)
    {
        super(ex);
    }

    /**
     * Creates a new KeyNotFoundException datatype.
     *
     * @param msg the detail message.
     */
    public KeyNotFoundException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new KeyNotFoundException datatype.
     *
     * @param msg the detail message.
     * @param ex the cause.
     */
    public KeyNotFoundException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
