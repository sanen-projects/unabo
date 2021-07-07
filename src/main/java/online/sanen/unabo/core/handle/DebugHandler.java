package online.sanen.unabo.core.handle;

import com.mhdt.Print;
import com.mhdt.toolkit.DateUtility;

import online.sanen.unabo.api.Handel;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.core.Assembler;


/**
 * 
 *
 * @author LazyToShow <br>
 *         Date: Dec 6, 2018 <br>
 *         Time: 6:16:58 PM
 */
public class DebugHandler implements SimpleHandler,Handel {

	@Override
	public Object handel(ChannelContext context, Object product) {

		context.setLastSql(context.isSqlFormat() ? formatSql(context) : this.getSql(context));

		if (context.isShowSql()) {
			StringBuilder sb = Assembler.instance().getStringBuild(context.getQueryId());

			sb.append("\r\n");
			sb.append(String.format("[%s][%s ：%s][%s][%s]", DateUtility.getNow("HH:mm:ss.ms"),context.productType(), context.getId(), Thread.currentThread().getName(),context.productType()));
			sb.append("\r\n");
			sb.append(context.isSqlFormat() ? formatSql(context) : this.getSql(context));
			sb.append("\r\n");
			sb.append(Print.translate("WHITE","----------------------------------------------------------------------------------------------"));
			sb.append("\r\n");
		}

		if (context.isLog()) {
			// Todo Provide an external log output interface
		}

		return null;
	}

}
