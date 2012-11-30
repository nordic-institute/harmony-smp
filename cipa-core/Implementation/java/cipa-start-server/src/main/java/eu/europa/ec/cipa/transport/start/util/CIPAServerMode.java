package eu.europa.ec.cipa.transport.start.util;

public enum CIPAServerMode {
	/**
	 * full production mode with SMP and SML usage
	 */
	PRODUCTION,
	/**
	 * Standalone mode the AP accepts the message with no recipent check (url and certificate)
	 */
	DEVELOPMENT_STANDALONE,
	/**
	 * In this mode the ap will call the smp directly withot sml dns lookup
	 */
	DEVELOPMENT_DIRECT_SMP;
}
