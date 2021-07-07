package online.sanen.unabo.api.condition;

/**
 * Query conditions Instance , In the form of objects embedded in the query
 * method .
 * 
 * @author LazyToshow <br>
 *         Date: 2016/07/25 <br>
 *         Time: 14:56
 */
public interface Condition {

	/**
	 * <p>
	 * Conditions enumerated
	 * <p>
	 * Describes conditional state values
	 */
	public static enum Cs {

		GT(">?"), 
		LT("<?"), 
		LT_EQUALS("<=?"), 
		GT_EQUALS(">=?"), 
		IN(" in (?)"), 
		NOT_IN(" not in (?)"), 
		EQUALS("=?"),
		NO_EQUALS("<>?"), 
		CONTAINS(" like '%?%'"), 
		NO_CONTAINS(" NOT LIKE '%?%'"),
		MATCH("  against (? in boolean mode) "), 
		START_WITH(" like '?%'"), 
		NO_START_WITH(" not like '?%'"), 
		END_WITH(" like '%?'"), 
		NO_END_WITH(" not like '%?'"), 
		IS_NULL(" is null"),
		IS_NOT_NULL(" is not null"), 
		IS_EMPTY("=''"), 
		IS_NOT_EMPTY(" <>''"), 
		BETWEEN(" between ? AND ?"),
		BETWEEN_TIME(" between ? AND ?");

		public String annotation;

		private Cs(String annotation) {
			this.annotation = annotation;
		}

	}

	/**
	 * Connection mode, which determines whether multiple conditions are connected
	 * with <code>'AND'</code> or with <code>'OR'</code>
	 */
	public static enum Associated {
		AND, 
		OR;
	}

	public Associated getAssociated();
	
	

}
