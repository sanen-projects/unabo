package online.sanen.unabo.api.condition;

import java.util.LinkedList;
import java.util.List;

import online.sanen.unabo.api.TypeIdentifier;

/**
 * 
 * @author LazyToShow <br>
 *         Date: 2018/06/12 <br>
 *         Time: 09:17
 */
public class CompositeCondition implements Condition, TypeIdentifier {

	/**
	 * @see Associated
	 */
	private Associated associated = Associated.AND;

	List<Condition> conditions = new LinkedList<>();

	public List<Condition> getConditions() {
		return conditions;
	}

	@SuppressWarnings("unchecked")
	public <T extends Condition> void setConditions(List<T> conditions) {
		this.conditions = (List<Condition>) conditions;
	}

	public void add(Condition condition) {
		conditions.add(condition);
	}

	public CompositeCondition setAssociated(Associated associated) {
		this.associated = associated;
		return this;
	}

	@Override
	public Associated getAssociated() {
		return associated;
	}

	@Override
	public String toString() {
		return "CompositeCondition [associated=" + associated + ", conditions=" + conditions + "]";
	}

}
