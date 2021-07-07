package online.sanen.unabo.core.handle;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.enums.ProductType;

/**
 * 
 * @author LazyToShow  <br>
 * Date: 2017/10/21  <br>
 * Time: 23:19
 */
public class QueryResultLimitHandler implements SimpleHandler,Handel {

	@Override
	public Object handel(ChannelContext context, Object product) {
		// The tag needs to be treated with limit.
		if (!context.hasLimitAble())
			return null;

		
		boolean isSupport = ProductType.processLimit(context, context.getSql(),context.getLimit());
		
		// Marked as a temporary unsupported SQL limit, which is handled by the
		// resultHandel general method.
		context.isSupportLimit(isSupport);

		return null;
	}

}
