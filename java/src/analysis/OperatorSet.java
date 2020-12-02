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
	private boolean isValid = true;
	
	public OperatorSet(EnumPrivacyProperty privacyProperty,boolean isDisjunctive) {
		super();
		this.privacyProperty = privacyProperty;
		this.isDisjunctive = isDisjunctive;
	}
	
	public void setValid(){
		isValid = true;
	}
	
	public void setInvalid(){
		isValid = false;
	}
	
	public boolean isValid(){
		return isValid;
	}

	@Override
	public String toString() {
		return "OperatorSet privacyProperty=" + privacyProperty
				+ " [" + super.toString() + "]";
	}
	
	
	
	

}
