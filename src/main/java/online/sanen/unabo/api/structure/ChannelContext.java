package online.sanen.unabo.api.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mhdt.degist.Validate;

import online.sanen.unabo.api.component.Manager;
import online.sanen.unabo.api.component.ManagerBridge;
import online.sanen.unabo.api.condition.Condition;
import online.sanen.unabo.api.condition.SimpleCondition;
import online.sanen.unabo.api.structure.enums.QueryType;
import online.sanen.unabo.api.structure.enums.ResultType;
import online.sanen.unabo.core.factory.Unabo;
import online.sanen.unabo.core.handle.SimpleHandler;
import online.sanen.unabo.template.jpa.JPA.Primarykey;

/**
 * The intermediate products of the construction process are stored on the
 * entity object.
 * 
 * @author LazyToShow <br>
 *         Date: 2017/10/21 <br>
 *         Time: 23:19
 */
public class ChannelContext extends ManagerBridge implements SimpleHandler {

	public ChannelContext(Manager manager) {
		super(manager);
	}

	Class<?> cls;

	String tableName;

	String schema;

	StringBuilder sql = new StringBuilder();

	List<Condition> conditions;

	List<String> fields;

	Set<String> exceptes;

	Object entity;

	Map<String, Object> entityMap;

	Class<?> entityClass;

	LinkedList<Object> paramers = new LinkedList<Object>();

	boolean qualifier = true;

	Map<String, String> alias;

	private String queryId;

	/**
	 * Whether the current version supports limit operations in the current database
	 * .
	 */
	boolean isSupportLimit;

	String countField;

	Collection<String> commonFilds;

	ResultType resultType;

	QueryType queryType;

	Primarykey primaryKey;

	Integer[] limit;

	Collection<?> entities;

	/**
	 * The limit operation is set
	 */
	boolean hasLimit;

	List<SortSupport> sortSupports = new LinkedList<>();

	private Collection<Map<String, Object>> entityMaps;

	public String getTableName() {

		if (!Validate.isNullOrEmpty(tableName))
			return tableName;

		Class<?> entryClass = getEntityClass();
		tableName = Unabo.tableNameByClass(entryClass);

		return tableName;
	}

	public void setTableName(String tableName) {

		if (tableName.contains(".")) {
			String[] arr = tableName.split("\\.");
			this.tableName = arr[1];
			setSchema(arr[0]);
		} else {
			this.tableName = tableName;

		}

	}

	public StringBuilder getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql.setLength(0);
		this.sql.append(sql);
	}

	public List<Condition> getConditions() {
		if (conditions == null)
			conditions = new ArrayList<>();
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public Set<String> getExceptes() {
		return exceptes;
	}

	public void setExceptes(Set<String> exceptes) {
		this.exceptes = exceptes;
	}

	public Class<?> getEntityClass() {
		if (entityClass != null)
			return entityClass;

		if (entity != null)
			return entityClass = entity.getClass();

		
		if (entities != null && entities.size() > 0) {
			Class<?> cls = entityClass = entities.stream().findFirst().get().getClass();
			return cls;
		}
			

		return null;
	}

	public void setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	public Object getEntity() {

		return entity;
	}

	public void setEntity(Object entry) {
		this.entity = entry;
	}

	public ResultType getResultType() {
		return resultType;
	}

	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}

	public QueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(QueryType queryType) {
		this.queryType = queryType;
	}

	public Collection<String> getCommonFields() {
		return commonFilds;
	}

	public void setCommonFields(Collection<String> tableFields) {
		this.commonFilds = tableFields;
	}

	/**
	 * 
	 * If the type of value is an <b>Array</b> or {@link Collection}, it will be
	 * broken down and added one by one.<br>
	 * <br>
	 * 
	 * An <b>Array</b>/{@link Collection} is valid when using a function like
	 * <b>BETWEEN</b> because it requires multiple arguments, and then an
	 * <b>Array</b>/{@link Collection} class is required . to wrap it
	 * 
	 * @param value
	 */
	public void addParamer(Object value) {

		if (value != null && value.getClass().isArray() && !(value instanceof byte[])) {

			Object[] array = (Object[]) value;

			for (Object it : array)
				this.addParamer(it);

		} else if (value != null && value instanceof Collection) {

			@SuppressWarnings("unchecked")
			Collection<Object> cc = (Collection<Object>) value;
			cc.forEach(this::addParamer);

		} else {
			paramers.add(value);
		}

	}

	public void addParamer(SimpleCondition condition) {

		Object value = condition.getValue();

		if (value == null) {
			paramers.add(value);
			return;
		}

		if (value.getClass().isArray()) {
			Object[] array = (Object[]) value;
			for (Object it : array)
				this.addParamer(it);
		} else if (value instanceof Collection) {
			@SuppressWarnings("unchecked")
			Collection<Object> cc = (Collection<Object>) value;
			cc.forEach(this::addParamer);
		} else {
			paramers.add(value);
		}

	}

	public void addCondition(Condition condition) {
		if (conditions == null)
			conditions = new ArrayList<Condition>();

		conditions.add(condition);
	}

	public Primarykey getPrimaryKey() {

		if (this.primaryKey != null)
			return this.primaryKey;

		// Get From Entry

		Class<?> cls = getEntityClass();

		if (cls != null)
			this.primaryKey = this.getPrimaryKey(cls);

		return this.primaryKey;
	}

	public void setPrimaryKey(Primarykey primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setLimit(Integer[] args) {
		this.limit = args;
	}

	public Integer[] getLimit() {
		return limit;
	}

	public void setEntities(Collection<?> entrys) {
		this.entities = entrys;
	}

	public Collection<?> getEntities() {
		return entities;
	}

	public boolean isSupportLimit(boolean flag) {
		return isSupportLimit = flag;
	}

	public boolean isSupportLimitAble() {
		return isSupportLimit;
	}

	public boolean hasLimitAble() {
		return hasLimit;
	}

	public void setHasLimitAble(boolean flag) {
		hasLimit = flag;
	}

	public Map<String, Object> getEntityMap() {
		return entityMap;
	}

	public void setEntityMap(Map<String, Object> entityMap) {
		this.entityMap = entityMap;
	}

	public interface SortSupport {

		public String toString();

	}

	public List<SortSupport> getSortSupports() {
		return sortSupports;
	}

	@SuppressWarnings("unchecked")
	public <T extends Map<String, Object>> void setEntityMaps(Collection<T> maps) {
		this.entityMaps = (Collection<Map<String, Object>>) maps;
	}

	public Collection<Map<String, Object>> getEntityMaps() {
		return entityMaps;
	}

	public Class<?> getCls() {
		return cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public List<Object> getParamers() {
		return paramers;
	}

	public void setLastSql(Object object) {

	}

	public void addParamersToHeader(List<Object> paramerVals) {
		this.paramers.addAll(0, paramerVals);
	}

	public void addParamersToIndex(Integer index, List<Object> paramerVals) {
		this.paramers.addAll(index, paramerVals);
	}

	public void addParamersToFooter(List<Object> paramerVals) {
		this.paramers.addAll(paramerVals);
	}

	public void setAlias(Map<String, String> alias) {
		this.alias = alias;
	}

	public Map<String, String> getAlias() {
		return this.alias;
	}

	public boolean isQualifier() {
		return qualifier;
	}

	public void setQualifier(boolean qualifier) {
		this.qualifier = qualifier;
	}

	public void setQueryId(String queryId) {

		this.queryId = queryId;
	}

	public String getQueryId() {
		return queryId;
	}

	public String getCountField() {
		return countField;
	}

	public void setCountField(String countField) {
		this.countField = countField;
	}

}
