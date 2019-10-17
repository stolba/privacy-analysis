package analysis;

import java.util.HashSet;

import tree.Operator;

public class OperatorSet extends HashSet<Operator> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4903380541587073974L;
	
	public final EnumPrivacyProperty privacyProperty;
	public final boolean isDisjunctive;
	
	public OperatorSet(EnumPrivacyProperty privacyProperty,boolean isDisjunctive) {
		super();
		this.privacyProperty = privacyProperty;
		this.isDisjunctive = isDisjunctive;
	}
	
	

}
