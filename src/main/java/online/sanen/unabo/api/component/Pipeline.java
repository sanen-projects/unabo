package online.sanen.unabo.api.component;

import java.util.List;

import online.sanen.unabo.api.Handel;


/**
 * <pre>
 * The middle unit of the production process
 * @author LazyToShow
 * Date: 2017/10/21
 * Time: 23:19
 * </pre>
 */
public interface  Pipeline {
	
	void addLast(Handel handel);
	
	Handel getLast();
	
	Handel getFirst();
	
	List<Handel> getHandels();

}
