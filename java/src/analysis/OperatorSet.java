package analysis;

import java.util.HashSet;

import tree.Operator;

public class OperatorSet extends HashSet<Operator> {
	
	public final EnumPrivacyProperty privacyProperty;
	public final boolean isDisjunctive;
	
	public OperatorSet(EnumPrivacyProperty privacyProperty,boolean isDisjunctive) {
		super();
		this.privacyProperty = privacyProperty;
		this.isDisjunctive = isDisjunctive;
	}
	
	

}
