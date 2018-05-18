package com.llj.framework.log;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author lu
 *
 */
public final class LogContext {
	private final String requestId;
	private AtomicInteger sequence;
	private String uid;
	private String sequence_prefix;

	public LogContext() {
		this.requestId = IDGenerator.generateWithSeq();
		this.sequence = new AtomicInteger(1);
	}

	public LogContext(String requestId, String sequence_prefix) {
		this.requestId = requestId;
		this.sequence_prefix = sequence_prefix;
		this.sequence = new AtomicInteger(1);
	}

	public String getUid() {
		return uid;
	}

	private static ThreadLocal<LogContext> REQUEST_CONTEXT_HOLDER = new ThreadLocal<LogContext>() {
		@Override
		protected LogContext initialValue() {
			return null;
		}
	};

	/**
	 * set request context to threadlocal holder.
	 *
	 * @param logCtx
	 */
	public static void set(LogContext logCtx) {
		/* add into managed attribute set */
		REQUEST_CONTEXT_HOLDER.set(logCtx);
	}

	/**
	 * remove request context from threadlocal holder.
	 */
	public static void remove() {
		/* remove from managed attribute set */
		REQUEST_CONTEXT_HOLDER.remove();
	}

	/**
	 * get request context from threadlocal holder.
	 *
	 * @return RequestContext
	 */
	public static LogContext get() {
		return REQUEST_CONTEXT_HOLDER.get();
	}

	public String getRequestId() {
		return requestId;
	}

	public AtomicInteger getSequence() {
		return sequence;
	}

	public int addSequence() {
		if (sequence.get() < Integer.MAX_VALUE) {
			return sequence.getAndIncrement();
		} else {
			return Integer.MAX_VALUE;
		}
	}
	public void setSequence(AtomicInteger sequence) {
		this.sequence = sequence;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSequence_prefix() {
		return sequence_prefix;
	}

	public void setSequence_prefix(String sequence_prefix) {
		this.sequence_prefix = sequence_prefix;
	}
}
