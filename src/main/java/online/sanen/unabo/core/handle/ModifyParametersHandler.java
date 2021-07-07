package online.sanen.unabo.core.handle;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.template.jpa.JPA;

/**
 * Modify modify operation parameters.
 * 
 * @author LazyToShow <br>
 *         Date: 2017/10/21 <br>
 *         Time: 23:19
 */
public class ModifyParametersHandler implements Handel {

	@Override
	public Object handel(ChannelContext context, Object product) {

		// Add the sort statement after the condition is processed
		if (!context.getSortSupports().isEmpty()) {
			StringBuilder sb = new StringBuilder(" ORDER BY ");
			context.getSortSupports().forEach(action -> {
				sb.append(action.toString());
				sb.append(",");
			});

			sb.setLength(sb.length() - 1);
			context.getSql().append(sb.toString());
		}

		// If you customize the parameters
		if (context.getParamers() != null && context.getParamers().size() > 0)
			return null;

		switch (context.getQueryType()) {

		case insert:
			processParamers(context);
			break;

		case update:
			processParamers(context);
			break;

		default:
			break;
		}

		return null;
	}

	private void processParamers(ChannelContext context) {
		
		for (String column : context.getCommonFields()) {
			
			if (context.getEntity() != null) {
				context.addParamer(JPA.getInject(context.getEntity(), column));
			} else {
				context.addParamer(context.getEntityMap().get(column));
			}

		}

		

	}

}
