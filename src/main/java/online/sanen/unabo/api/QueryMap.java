package online.sanen.unabo.api;

import online.sanen.unabo.api.condition.Condition;

/**
 * 
 * @author LazyToShow Date: 2018/06/12 Time: 09:17
 */
public interface QueryMap {

	int insert();

	/**
	 * Delete by primary key,The key values must be contained in the map container
	 * 
	 * @param primaryKey
	 * @return
	 */
	int delete(String primaryKey);

	int delete();

	/**
	 * The primary key to modify,The key values must be contained in the map
	 * container
	 * 
	 * @param primaryKey
	 * @return
	 */
	int update(String primaryKey);

	/**
	 * Custom condition modification，Priority is greater than {@link QueryMap#setPrimary(String)}
	 * modification, and even if primary key is set, the data will be modified in a
	 * conditional manner.
	 * 
	 * @param condition
	 * @return
	 */
	int update(Condition... condition);

	int update();

	QueryMap setFields(String... fields);

	QueryMap setExceptFields(String... fields);

	int create();

	/**
	 * The primary key to modify
	 * 
	 * @param primaryKey
	 * @return
	 */
	QueryMap setPrimary(String primaryKey);

	QueryMap setQualifier(boolean b);

}
