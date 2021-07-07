package online.sanen.unabo.core;

import static online.sanen.unabo.api.condition.C.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.mhdt.degist.Validate;
import com.mhdt.toolkit.Assert;
import com.mhdt.toolkit.Reflect;

import online.sanen.unabo.api.Bootstrap;
import online.sanen.unabo.api.condition.C;
import online.sanen.unabo.api.condition.CompositeCondition;
import online.sanen.unabo.api.condition.Condition;
import online.sanen.unabo.api.condition.SimpleCondition;
import online.sanen.unabo.api.exception.QueryException;
import online.sanen.unabo.api.structure.Column;
import online.sanen.unabo.api.structure.enums.Sorts;
import online.sanen.unabo.core.factory.Unabo;
import online.sanen.unabo.core.handle.SimpleHandler;
import online.sanen.unabo.template.jpa.Id;
import online.sanen.unabo.template.jpa.JPA;
import online.sanen.unabo.template.jpa.NoDB;
import online.sanen.unabo.template.jpa.Table;
import online.sanen.unabo.template.jpa.JPA.Primarykey;

/**
 * <p>
 * User behavior interface.
 * <p>
 * By default, it provides users with the basic function of <b>insert</b>,
 * <b>delete</b>, <b>update</b> and <b>select</b> singlecases.
 * <p>
 * To keep coding styles consistent, static methods implement common set
 * operations (although {@link Bootstrap} instances can still be used, we
 * recommend this approach).
 * <p>
 * All result sets correspond to subclass class name fields, class names
 * correspond to table names, object fields correspond to table fields, and
 * object field types correspond to table field types.
 * <p>
 * By default, the query is associated with the class name field name, or
 * aliased using the {@link Table} {@link Column} annotation.
 * <p>
 * Note that this interface cannot execute SQL statements.
 * 
 * @see Bootstrap
 *
 * @author LazyToShow <br>
 *         Date: Dec 7, 2018 <br>
 *         Time: 10:28:34 AM
 */
public interface Behavior<T> extends SimpleHandler{

	/**
	 * 
	 * @param fields
	 * @return
	 */
	default Perfect<T> fields(String... fields) {

		Perfect<T> perfect = perfect(bootstrap(), this, fields, null);
		return perfect;
	}

	/**
	 * 
	 * @param fields
	 * @return
	 */
	default Perfect<T> exceptFields(String... fields) {
		Perfect<T> perfect = perfect(bootstrap(), this, null, fields);
		return perfect;
	}

	/**
	 * Inserts the current instance to the database
	 * 
	 * @return
	 */
	default int insert() {
		return perfect(bootstrap(), this, null, null).insert();
	}

	/**
	 * Delete in the database according to the {@link Id}
	 * 
	 * @return
	 */
	default int delete() throws QueryException {

		checkPrimary();
		return bootstrap().query(this).delete();
	}

	default void checkPrimary() {

		Primarykey primaryKey = this.getPrimaryKey(this.getClass());
		Object primary = primaryKey.getValue(this);
		Assert.notNull(primary, "The primary key is null:" + primaryKey + " " + this.getClass());
	}

	/**
	 * Specify field modification
	 * 
	 * @param fields - Only the specified fields in the entity class are modified
	 * @see #fields(String...)
	 * @see #update(String...)
	 * @return
	 */
	default int update(String... fields) {
		checkPrimary();
		return fields(fields).update();
	}
	
	default int updateBy(String column) {
		return perfect(bootstrap(), this, null, null).updateBy(column);
	}
	

	/**
	 * Gets an instance from the database through the primary key
	 * 
	 * @return
	 */
	default Optional<T> findByPk() {

		return perfect(bootstrap(), this, null, null).findByPk();
	}

	/**
	 * 
	 * @param fields
	 * @return
	 */
	default Optional<T> findByFields(String... fields) throws QueryException {

		return perfect(bootstrap(), this, null, null).findByFields(fields);
	}

	/**
	 * Create tables based on data structures
	 * 
	 * @return
	 */
	default int createTable() {

		return perfect(bootstrap(), this, null, null).createTable();
	}

	/**
	 * Delete table
	 * 
	 * @return
	 */
	default int dropTable() {

		String tableName = Unabo.tableNameByClass(this.getClass());

		if (bootstrap().queryTable(tableName).isExsites())
			return bootstrap().queryTable(tableName).drop();

		return -1;
	}

	/**
	 * Get the {@link Bootstrap} instance corresponding to the current model. Note
	 * that new {@link Bootstrap} should not be constructed here, but should be
	 * obtained from the created object pool or cache container.
	 * 
	 * @return
	 */
	default Bootstrap bootstrap() {

		String bootstrapId = JPA.getBootStrapId(this.getClass());

		if (Validate.isNullOrEmpty(bootstrapId) && Unabo.isUniqueness())
			return Unabo.getFirst();

		Assert.notNull(bootstrapId, "The bootstrap instance is not specified id " + this.getClass());

		if (Unabo.contains(bootstrapId))
			return Unabo.get(bootstrapId);
		else
			throw new NullPointerException("BootstrapId: '" + bootstrapId
					+ "' index instance cannot be passed by class:" + this.getClass().getName());
	}

	/**
	 * Tell {@link Behavior} the type of instance the interface is about to operate
	 * on, which is a constructed start function
	 * 
	 * @param cls
	 * @return
	 */
	public static <T> PerfectList<T> specify(Class<T> cls) {

		return perfectList(staticBootstrap(cls), cls);
	}

	/**
	 * 
	 * @param cls
	 * @return
	 */
	static Bootstrap staticBootstrap(Class<?> cls) {

		String bootStrapId = JPA.getBootStrapId(cls);

		if (!Validate.isNullOrEmpty(bootStrapId)) {
			Bootstrap bootstrap = Unabo.get(JPA.getBootStrapId(cls));

			if (bootstrap == null)
				throw new BootstrapObtainException("Invalid bootstrap id '" + bootStrapId + "' for class " + cls);

			return bootstrap;

		} else if (Validate.isNullOrEmpty(bootStrapId) && Unabo.isUniqueness()) {
			return Unabo.getFirst();
		} else {
			throw new BootstrapObtainException("There is no available bootstrap instance");
		}

	}

	/**
	 * 
	 * @param $bootstrap
	 * @param $class
	 * @return
	 */
	static <T> PerfectList<T> perfectList(Bootstrap $bootstrap, Class<T> $class) {

		return new PerfectList<T>() {

			List<Condition> conditions = new ArrayList<>();

			Integer[] limit;

			private String[] fields;

			private String[] excepts;

			Sorts sorts;

			private String[] sortFields;

			@Override
			public PerfectList<T> addCondition(Condition cond) {

				if (cond != null)
					this.conditions.add(cond);
				return this;
			}

			@Override
			public PerfectList<T> addCondition(Consumer<List<Condition>> conds) {

				if (conds != null)
					conds.accept(conditions);
				return this;
			}

			@Override
			public PerfectList<T> sort(Sorts sorts, String... fields) {
				this.sorts = sorts;
				this.sortFields = fields;
				return this;
			}

			@Override
			public PerfectList<T> limit(Integer... limit) {
				this.limit = limit;
				return this;
			}

			@Override
			public PerfectList<T> setFields(String... fields) {
				this.fields = fields;
				return this;
			}

			@Override
			public PerfectList<T> setExcepts(String... excepts) {
				this.excepts = excepts;
				return this;
			}

			@Override
			public void batchInsert(List<T> list) {

				Assert.notNull(list, "List is null");

				$bootstrap.query(list).setFields(fields).setExceptFields(excepts).insert();
			}

			@Override
			public void batchUpdate(List<T> list) {

				Assert.notNull(list, "List is null");

				$bootstrap.query(list).setFields(fields).setExceptFields(excepts).update();

			}

			@Override
			public List<T> list() {

				return $bootstrap.queryTable(Unabo.tableNameByClass($class)).setFields(fields).setExceptFields(excepts)
						.limit(limit).sort(sorts, sortFields).addCondition(conds -> conds.addAll(conditions))
						.entities($class);
			}

			@Override
			public int delete() {
				return $bootstrap.queryTable(Unabo.tableNameByClass($class)).addCondition(conds -> conds.addAll(conditions))
						.delete();
			}

			@Override
			public List<Map<String, Object>> maps() {
				return $bootstrap.queryTable(Unabo.tableNameByClass($class)).setFields(fields).setExceptFields(excepts)
						.limit(limit).sort(sorts, sortFields).addCondition(conds -> conds.addAll(conditions)).maps();
			}

			@Override
			public Integer count() {
				return $bootstrap.queryTable(Unabo.tableNameByClass($class)).setFields(fields).setExceptFields(excepts)
						.limit(limit).sort(sorts, sortFields).addCondition(conds -> conds.addAll(conditions)).count();
			}

		};
	}

	/**
	 * 
	 *
	 * @author LazyToShow <br>
	 *         Date: Dec 7, 2018 <br>
	 *         Time: 4:36:11 PM
	 */
	interface PerfectList<T> {

		/**
		 * Add the condition
		 * 
		 * @param cond - {@link C} {@link SimpleCondition} {@link CompositeCondition}
		 * @return
		 */
		public PerfectList<T> addCondition(Condition cond);

		/**
		 * Add the condition
		 * 
		 * @param conds - {@link C} {@link SimpleCondition} {@link CompositeCondition}
		 * @return
		 */
		public PerfectList<T> addCondition(Consumer<List<Condition>> conds);

		/**
		 * Specify collation
		 * 
		 * @param sorts
		 * @param fields
		 * @return
		 */
		public PerfectList<T> sort(Sorts sorts, String... fields);

		/**
		 * Paging function, the specific implementation by the database vendor
		 * 
		 * @param limit
		 * @return
		 */
		public PerfectList<T> limit(Integer... limit);

		/**
		 * Specifies that entity class fields participate in the operation
		 * 
		 * @param fields
		 * @return
		 */
		public PerfectList<T> setFields(String... fields);

		/**
		 * Exclude entity class fields from the operation
		 * 
		 * @param excepts
		 * @return
		 */
		public PerfectList<T> setExcepts(String... excepts);

		/**
		 * 
		 * @param list
		 */
		void batchInsert(List<T> list);

		/**
		 * 
		 * @param list
		 */
		void batchUpdate(List<T> list);

		/**
		 * 
		 * @return
		 */
		List<T> list();

		/**
		 * 
		 */
		int delete();

		public List<Map<String, Object>> maps();

		public Integer count();
	}

	/**
	 * 
	 * @param $bootstrap
	 * @param $bean
	 * @param $fields
	 * @param $excepts
	 * @return
	 */
	default Perfect<T> perfect(Bootstrap $bootstrap, Behavior<T> $bean, String[] $fields, String[] $excepts) {

		return new Perfect<T>() {

			@Override
			public int insert() {

				return $bootstrap.query($bean).setFields($fields).setExceptFields($excepts).insert();
			}

			@Override
			public int update() {
				return $bootstrap.query($bean).setFields($fields).setExceptFields($excepts).update();
			}
			
			@Override
			public int updateBy(String field) {
				
				return $bootstrap.query($bean).setFields($fields).setExceptFields($excepts).updateBy(field);
			}

			@SuppressWarnings("unchecked")
			@Override
			public Optional<T> findByPk() {

				Primarykey primaryKey = Behavior.this.getPrimaryKey($bean.getClass());
				Object value = primaryKey.getValue($bean);

				return (Optional<T>) Optional.ofNullable($bootstrap.queryTable(Unabo.tableNameByClass($bean.getClass()))
						.setFields($fields).setExceptFields($excepts)
						.addCondition(conditions -> conditions.add(buid(primaryKey.getName()).eq(value)))
						.setFields($fields).setExceptFields($excepts).unique($bean.getClass()));
			}

			@SuppressWarnings("unchecked")
			@Override
			public Optional<T> findByFields(String... fields) {

				return (Optional<T>) Optional.ofNullable($bootstrap.queryTable(Unabo.tableNameByClass($bean.getClass()))
						.setFields($fields).setExceptFields($excepts).addCondition(conditions -> {

							for (String fieldName : fields) {

								if (Validate.hasField($bean.getClass(), fieldName))

									try {
										conditions.add(eq(fieldName, Reflect.getValue($bean, fieldName)));
									} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
											| SecurityException e) {
										throw new QueryException(e);
									}
							}

						}).unique($bean.getClass()));
			}

			@Override
			public int createTable() {

				if (!$bootstrap.queryTable(Unabo.tableNameByClass($bean.getClass())).isExsites()) {
					return $bootstrap.query($bean).setFields($fields).setExceptFields($excepts).create();

				}

				return -1;

			}

			

		};

	}

	/**
	 * 
	 *
	 * @author LazyToShow <br>
	 *         Date: Dec 7, 2018 <br>
	 *         Time: 4:35:55 PM
	 */
	interface Perfect<T> {

		int insert();

		/**
		 * <p>
		 * Modify the current object persistence layer based on the instance primary key
		 * {@link Id} value
		 * <p>
		 * For fields that you don't want to modify, try {@link NoDB} or
		 * {@link #fields(String...)}
		 * <p>
		 * note that before performing the modification operation, please ensure that
		 * the instance data is up-to-date and complete. It is recommended that a query
		 * be executed first.
		 * 
		 * @return
		 */
		int update();

		/**
		 * Primary key query
		 * 
		 * @return
		 */
		Optional<T> findByPk();

		/**
		 * Query by the specified field. The specified fields are taken into the query
		 * as equivalent conditions
		 * 
		 * @param fields - Must be the fields that the entity class contains
		 * @return
		 */
		Optional<T> findByFields(String... fields);
		
		int updateBy(String field);
		

		/**
		 * Create tables based on entity class data structures
		 * 
		 * @return
		 */
		int createTable();
	}

}
