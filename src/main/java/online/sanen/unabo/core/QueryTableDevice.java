package online.sanen.unabo.core;

import static online.sanen.unabo.api.condition.C.buid;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mhdt.toolkit.Assert;

import online.sanen.unabo.api.PipelineFactory;
import online.sanen.unabo.api.QueryTable;
import online.sanen.unabo.api.component.Manager;
import online.sanen.unabo.api.component.Pipeline;
import online.sanen.unabo.api.component.PipelineDivice;
import online.sanen.unabo.api.condition.Condition;
import online.sanen.unabo.api.condition.Condition.Associated;
import online.sanen.unabo.api.condition.Condition.Cs;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.Column;
import online.sanen.unabo.api.structure.StreamConsumer;
import online.sanen.unabo.api.structure.ChannelContext.SortSupport;
import online.sanen.unabo.api.structure.enums.ProductType;
import online.sanen.unabo.api.structure.enums.QueryType;
import online.sanen.unabo.api.structure.enums.ResultType;
import online.sanen.unabo.api.structure.enums.Sorts;
import online.sanen.unabo.core.factory.HandelFactory;
import online.sanen.unabo.core.handle.SimpleHandler;
import online.sanen.unabo.template.SqlTemplate;

/**
 * 
 * @author LazyToShow <br>
 *         Date: 2017/11/23 <br>
 *         Time: 22:22
 */
public class QueryTableDevice implements QueryTable,SimpleHandler {

	ChannelContext context;

	public QueryTableDevice(Manager manager, String tableName) {

		Assert.notNullOrEmpty(tableName, "Table name is null or empty");

		context = new ChannelContext(manager);
		context.setTableName(tableName);
	}

	@Override
	public QueryTable addCondition(String fieldName, Cs cs) {
		context.addCondition(buid(fieldName, cs));
		return this;
	}

	@Override
	public QueryTable addCondition(String fieldName, Cs cs, Associated associated) {
		context.addCondition(buid(fieldName, cs, associated));
		return this;
	}

	@Override
	public QueryTable addCondition(String fieldName, Cs cs, Object value, Associated associated) {
		context.addCondition(buid(fieldName, cs, value, associated));
		return this;
	}

	@Override
	public QueryTable addCondition(String fieldName, Cs cs, Object value) {
		context.addCondition(buid(fieldName, cs, value));
		return this;
	}

	@Override
	public QueryTable addCondition(Condition cond) {

		if (cond == null)
			return this;

		context.addCondition(cond);
		return this;
	}

	@Override
	public QueryTable addCondition(Consumer<List<Condition>> consumer) {

		if (consumer == null)
			return this;

		consumer.accept(context.getConditions());
		return this;
	}

	@Override
	public QueryTable sort(final Sorts sorts, final String... fields) {

		if (sorts == null)
			return this;

		String modifier = ProductType.applyTableModifier(context.productType());

		context.getSortSupports().add(new SortSupport() {

			@Override
			public String toString() {

				StringBuilder sb = new StringBuilder();

				for (String field : fields) {
					if (!field.contains("*") && !field.contains("/"))
						sb.append(String.format("%s%s%s,", modifier, field, modifier));
					else
						sb.append(String.format("%s,", field));
				}

				sb.setLength(sb.length() - 1);
				sb.append(" " + sorts);

				return sb.toString();
			}
		});

		return this;
	}

	@Override
	public QueryTable setFields(String... fields) {

		if (fields == null)
			return this;

		context.setFields(Arrays.asList(fields));

		return this;
	}

	@Override
	public QueryTable setExceptFields(String... fields) {

		if (fields == null)
			return this;

		context.setExceptes(new HashSet<>(Arrays.asList(fields)));
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unique() {

		return (T) Assembler.instance().create(QueryType.select, ResultType.Object, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.commonFieldHandel());
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
				pipeline.addLast(HandelFactory.limitHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

	@Override
	public <T> Optional<T> uniqueOptional() {
		return Optional.of(this.unique());
	}

	@Override
	public <T> Optional<T> uniqueOptional(Class<T> cls) {
		return Optional.of(this.unique(cls));
	}

	@Override
	public int count() {

		return (int) Assembler.instance().create(QueryType.count, ResultType.Int, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
				pipeline.addLast(HandelFactory.limitHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

	@Override
	public int count(String field) {

		Assert.notNull(field, "Field is null");
		context.setCountField(field);

		return (int) Assembler.instance().create(QueryType.count, ResultType.Int, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
				pipeline.addLast(HandelFactory.limitHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> map() {
		return (Map<String, Object>) Assembler.instance().create(QueryType.select, ResultType.Object, context,
				new PipelineFactory() {
					@Override
					public Pipeline getPipeline() {
						Pipeline pipeline = new PipelineDivice();
						pipeline.addLast(HandelFactory.commonFieldHandel());
						pipeline.addLast(HandelFactory.sqlHandel());
						pipeline.addLast(HandelFactory.conditionHandel());
						pipeline.addLast(HandelFactory.paramerHandel());
						pipeline.addLast(HandelFactory.limitHandel());
						pipeline.addLast(HandelFactory.resultHandel());
						pipeline.addLast(HandelFactory.debugHandel());
						return pipeline;
					}
				});
	}
	
	@Override
	public Optional<Map<String, Object>> mapOptional() {
		return Optional.of(this.map());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> list() {

		return (List<T>) Assembler.instance().create(QueryType.select, ResultType.List, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.commonFieldHandel());
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
				pipeline.addLast(HandelFactory.limitHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

	@Override
	public void stream(int bufferSize, Consumer<List<Map<String, Object>>> consumer) {

		Assembler.instance().create(QueryType.select, ResultType.Maps, context, new PipelineFactory() {

			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.commonFieldHandel());
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
				pipeline.addLast(HandelFactory.limitHandel());
				pipeline.addLast(HandelFactory.streamHandel(bufferSize, consumer, null));
				return pipeline;
			}
		});

	}

	@Override
	public void stream(int bufferSize, Consumer<List<Map<String, Object>>> consumer, Map<String, String> aliases) {

		Assembler.instance().create(QueryType.select, ResultType.Maps, context, new PipelineFactory() {

			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.commonFieldHandel());
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
				pipeline.addLast(HandelFactory.streamHandel(bufferSize, consumer, aliases));
				return pipeline;
			}
		});

	}

	@Override
	public void stream(int bufferSize, Function<List<Column>, Object> consumer, StreamConsumer datas,
			Map<String, String> aliases) {

		Assembler.instance().create(QueryType.select, ResultType.Maps, context, new PipelineFactory() {

			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.commonFieldHandel());
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
				pipeline.addLast(HandelFactory.streamHandel(bufferSize, consumer, datas, aliases));
				return pipeline;
			}
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> maps() {

		return (List<Map<String, Object>>) Assembler.instance().create(QueryType.select, ResultType.Maps, context,
				new PipelineFactory() {
					@Override
					public Pipeline getPipeline() {
						Pipeline pipeline = new PipelineDivice();
						pipeline.addLast(HandelFactory.commonFieldHandel());
						pipeline.addLast(HandelFactory.sqlHandel());
						pipeline.addLast(HandelFactory.conditionHandel());
						pipeline.addLast(HandelFactory.paramerHandel());
						pipeline.addLast(HandelFactory.limitHandel());
						pipeline.addLast(HandelFactory.resultHandel());
						pipeline.addLast(HandelFactory.debugHandel());
						return pipeline;
					}
				});
	}

	@Override
	public List<Map<String, Object>> maps(Map<String, String> alias) {
		Assert.notNull(alias, "Alias is null");

		context.setAlias(alias);
		return this.maps();
	}

	@Override
	public int delete() {

		return (int) Assembler.instance().create(QueryType.delete, ResultType.Int, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

	@Override
	public QueryTable limit(Integer... args) {

		if (args == null)
			return this;

		context.setLimit(args);
		context.setHasLimitAble(true);
		return this;
	}

	@Override
	public boolean isExsites() {

		try {

			SqlTemplate template = (SqlTemplate) context.getTemplate();
			String tableName = context.getTableName();
			String schema = context.getSchema();
			ProductType productType = context.productType();
			template.queryForRowSet("SELECT 1 FROM " + this.modifiedTableName(tableName, schema, productType));
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	@Override
	public int updateName(String newName) {

		context.setSql(ProductType.updateTableNameSQL(context.productType(), context.getTableName(),
				context.getSchema(), newName));

		return (int) Assembler.instance().create(QueryType.update, ResultType.Int, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});

	}

	@Override
	public int clear() {
		return delete();
	}

	@Override
	public int drop() {

		return (int) Assembler.instance().create(QueryType.drop, ResultType.Int, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

	@Override
	public int addColumn(String columnName, String type) {

		String tableName = context.getTableName();
		String schema = context.getSchema();
		ProductType productType = context.productType();
		String modifier = ProductType.applyTableModifier(productType);

		context.setSql(String.format("ALTER TABLE %s ADD COLUMN %s%s%s %s",
				this.modifiedTableName(tableName, schema, productType), modifier, columnName, modifier, type));

		return (int) Assembler.instance().create(QueryType.update, ResultType.Int, context, new PipelineFactory() {

			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});

	}

	@Override
	public String copyTo(String newTableName) {

		return (String) Assembler.instance().create(QueryType.select, ResultType.String, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.commonFieldHandel());
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
				pipeline.addLast(HandelFactory.limitHandel());
				pipeline.addLast(HandelFactory.createAndInsert(newTableName));
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

	@Override
	public <T> T unique(Class<T> cls) {
		context.setEntityClass(cls);
		return unique();
	}

	@Override
	public <T> List<T> list(Class<T> cls) {
		context.setEntityClass(cls);
		return list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> entities(Class<T> cls) {
		context.setEntityClass(cls);

		return (List<T>) Assembler.instance().create(QueryType.select, ResultType.Beans, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.commonFieldHandel());
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
				pipeline.addLast(HandelFactory.limitHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

	@Override
	public QueryTable setQualifier(boolean flag) {
		context.setQualifier(flag);
		return this;
	}

	@Override
	public QueryTable alias(Map<String, String> alias) {
		context.setAlias(alias);
		return this;
	}

	

}
