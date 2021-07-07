package online.sanen.unabo.api.component;

import java.util.LinkedList;
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
public class PipelineDivice implements Pipeline{
	
	LinkedList<Handel> list = new LinkedList<>();

	@Override
	public void addLast(Handel handel) {
		list.addLast(handel);
	}

	@Override
	public Handel getLast() {
		return list.getLast();
	}

	@Override
	public Handel getFirst() {
		return list.getFirst();
	}

	@Override
	public List<Handel> getHandels() {
		return list;
	}

}
