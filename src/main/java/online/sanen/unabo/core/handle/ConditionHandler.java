package online.sanen.unabo.core.handle;

import static online.sanen.unabo.api.condition.Condition.Associated.AND;
import static online.sanen.unabo.api.condition.Condition.Cs.CONTAINS;
import static online.sanen.unabo.api.condition.Condition.Cs.END_WITH;
import static online.sanen.unabo.api.condition.Condition.Cs.IN;
import static online.sanen.unabo.api.condition.Condition.Cs.IS_EMPTY;
import static online.sanen.unabo.api.condition.Condition.Cs.IS_NOT_EMPTY;
import static online.sanen.unabo.api.condition.Condition.Cs.IS_NOT_NULL;
import static online.sanen.unabo.api.condition.Condition.Cs.IS_NULL;
import static online.sanen.unabo.api.condition.Condition.Cs.MATCH;
import static online.sanen.unabo.api.condition.Condition.Cs.NOT_IN;
import static online.sanen.unabo.api.condition.Condition.Cs.NO_CONTAINS;
import static online.sanen.unabo.api.condition.Condition.Cs.START_WITH;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.mhdt.degist.Statistics;
import com.mhdt.degist.Validate;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.condition.CompositeCondition;
import online.sanen.unabo.api.condition.Condition;
import online.sanen.unabo.api.condition.SimpleCondition;
import online.sanen.unabo.api.exception.ConditionException;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.enums.ProductType;

/**
 * 
 * @author LazyToShow <br>
 *         Date: 2017/10/21 <br>
 *         Time: 23:19
 */
public class ConditionHandler implements Handel {

	@Override
	public Object handel(ChannelContext structure, Object product) {
		processCondition(structure);
		return null;
	}

	private void processCondition(ChannelContext context) {

		if (context.getConditions() == null || context.getConditions().isEmpty())
			return;

		StringBuilder sql = context.getSql();
		int index = 0;

		if ((index = isNeedSplitkeywords(sql)) < 0)
			index = sql.length();

		int question_mark_number = Statistics.countChar('?', sql.substring(0, index));

		// is append where
		boolean needAppendWhere = false;
		int lastWhereIndex = sql.toString().toLowerCase().lastIndexOf("where");
		
		if (lastWhereIndex == -1 || (sql.toString().indexOf("(", lastWhereIndex)==-1  && sql.toString().indexOf(")", lastWhereIndex) != -1))
			needAppendWhere = true;

		StringBuilder sb = new StringBuilder(needAppendWhere ? " WHERE " : " ");

		List<Object> paramerVals = new LinkedList<Object>();

		process(context.productType(), sb, context.getConditions(), obj -> {
			if (Validate.isArray(obj)) {
				Object[] array = (Object[]) obj;
				for (Object item : array)
					paramerVals.add(item);
			} else if (Validate.isCollection(obj)) {
				Collection<?> coleCollection = (Collection<?>) obj;
				for (Iterator<?> iterator = coleCollection.iterator(); iterator.hasNext();) {
					Object object = (Object) iterator.next();
					paramerVals.add(object);
				}

			} else {
				paramerVals.add(obj);
			}

		});

		context.addParamersToIndex(question_mark_number, paramerVals);
		sql.insert(index, sb.toString());
	}

	static final String[] SPLITE_KEYWORDS = new String[] { " group ", " order by ", " limit " };

	private int isNeedSplitkeywords(StringBuilder sql) {

		int index = -1;

		for (String item : SPLITE_KEYWORDS)
			if ((index = sql.lastIndexOf(item)) > -1 || (index = sql.lastIndexOf(item.toUpperCase())) > -1)
				return index;

		return index;

	}

	public static String process(ProductType productType, StringBuilder sb, List<? extends Condition> conditions,
			Consumer<Object> paramerValConsumer) {

		String modifier = ProductType.applyTableModifier(productType);

		for (Condition condition : conditions) {
			processPefix(sb, condition);

			sb.append("(");

			if (condition instanceof CompositeCondition) {
				process(productType, sb, ((CompositeCondition) condition).getConditions(), paramerValConsumer);
				sb.append(")");
				continue;
			}

			SimpleCondition cond = (SimpleCondition) condition;

			if (cond.getCs() == CONTAINS || cond.getCs() == NO_CONTAINS || cond.getCs() == START_WITH
					|| cond.getCs() == END_WITH) {

				try {
					sb.append(analyseConditionKey(cond.getFieldName(), modifier)
							+ cond.getCs().annotation.replace("?", cond.getValue().toString()));
				} catch (NullPointerException e) {
					throw new ConditionException("Condition value is null ," + condition.toString(), e);
				}

			} else if (cond.getCs() == IN || cond.getCs() == NOT_IN) {

				sb.append(analyseConditionKey(cond.getFieldName(), modifier)
						+ cond.getCs().annotation.replace("?", processParamersOfIn(cond.getValue())));
				paramerValConsumer.accept(cond.getValue());

			} else if (cond.getCs() == MATCH) {

				sb.append(
						"match(" + analyseConditionKey(cond.getFieldName(), modifier) + ")" + cond.getCs().annotation);
				paramerValConsumer.accept(cond.getValue());

			} else if (cond.getCs() == IS_EMPTY || cond.getCs() == IS_NULL || cond.getCs() == IS_NOT_EMPTY
					|| cond.getCs() == IS_NOT_NULL) {

				sb.append(String.format("%s%s", analyseConditionKey(cond.getFieldName(), modifier),
						cond.getCs().annotation));

			} else {
				String analyseConditionKey = analyseConditionKey(cond.getFieldName(), modifier);
				sb.append(String.format("%s%s", analyseConditionKey, cond.getCs().annotation));
				paramerValConsumer.accept(cond.getValue());
			}

			sb.append(")");
		}

		sb.append(" ");
		return sb.toString();
	}

	private static String analyseConditionKey(String filedName, String modifier) {

		if (!filedName.contains(".")) {
			return String.format("%s%s%s", modifier, filedName, modifier);
		} else if (filedName.contains("$") || Statistics.countChar('.', filedName) > 1) {
			return filedName;
		} else {
			String[] split = filedName.split("\\.");
			return String.format("%s.%s%s%s", split[0], modifier, split[1], modifier);
		}

	}

	/**
	 * (?,?,?)
	 * 
	 * @param value
	 * @return
	 */
	private static CharSequence processParamersOfIn(Object value) {

		if (value.getClass().isArray()) {
			Object[] array = (Object[]) value;
			StringBuilder sql = new StringBuilder();

			for (int i = 0; i < array.length; i++)
				sql.append("?,");

			sql.setLength(sql.length() - 1);

			return sql.toString();

		} else if (value instanceof Collection) {

			@SuppressWarnings("unchecked")
			Collection<Object> array = (Collection<Object>) value;
			StringBuilder sql = new StringBuilder();

			for (int i = 0; i < array.size(); i++)
				sql.append("?,");

			sql.setLength(sql.length() - 1);

			return sql.toString();

		} else {
			return "?";
		}
	}

	private static void processPefix(StringBuilder sql, Condition condition) {

		if (sql.toString().endsWith(" WHERE ") || sql.toString().endsWith("("))
			return;

		if (!(condition.getAssociated() == AND)) {
			sql.append(" OR ");
		} else {
			sql.append(" AND ");
		}

	}

}
