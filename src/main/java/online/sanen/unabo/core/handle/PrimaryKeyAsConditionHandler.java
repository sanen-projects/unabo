package online.sanen.unabo.core.handle;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.condition.C;
import online.sanen.unabo.api.exception.QueryException;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.template.jpa.JPA.Primarykey;

/**
 * @author LazyToShow <br>
 *         Date: 2017/11/23<br>
 *         Time: 16:15<br>
 */
public class PrimaryKeyAsConditionHandler implements Handel {

	@Override
	public Object handel(ChannelContext context, Object product) {

		if (!context.getConditions().isEmpty())
			return null;

		Primarykey primaryKey = context.getPrimaryKey();
		Object value = (context.getEntity() != null) ? primaryKey.getValue(context.getEntity()) : primaryKey.getValue();

		if (value == null)
			throw new QueryException("Primary key related operations cannot be performed because the value is null");

		context.addCondition(C.buid(primaryKey.getName()).eq(value));

		return null;
	}

}
