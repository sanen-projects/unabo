package online.sanen.unabo.api;

import online.sanen.unabo.api.structure.ChannelContext;


/**
 * Pipeline processor
 * 
 * @author LazyToShow <br>
 *         Date: 2017/10/21 <br>
 *         Time: 23:19
 */
public interface Handel {

	/**
	 * 
	 * @param context
	 * @param message
	 * @return
	 */
	Object handel(ChannelContext context, Object message);
	

}
