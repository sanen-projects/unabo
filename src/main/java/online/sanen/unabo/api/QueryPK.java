package online.sanen.unabo.api;


/**
 * The primary key operation
 * 
 * @author LazyToShow <br>
 * Date: 2017/11/23 <br>
 * Time: 9:33
 */
public interface QueryPK<T> {

	/** The primary key to find */
	T unique();

	/** The primary key to remove */
	int delete();

}
