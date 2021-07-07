package online.sanen.unabo.core;

import static online.sanen.unabo.api.condition.C.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mhdt.toolkit.Assert;

import online.sanen.unabo.api.PipelineFactory;
import online.sanen.unabo.api.QuerySql;
import online.sanen.unabo.api.component.Manager;
import online.sanen.unabo.api.component.Pipeline;
import online.sanen.unabo.api.component.PipelineDivice;
import online.sanen.unabo.api.condition.Condition;
import online.sanen.unabo.api.condition.Condition.Associated;
import online.sanen.unabo.api.condition.Condition.Cs;
import online.sanen.unabo.api.exception.ConditionException;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.Column;
import online.sanen.unabo.api.structure.StreamConsumer;
import online.sanen.unabo.api.structure.enums.QueryType;
import online.sanen.unabo.api.structure.enums.ResultType;
import online.sanen.unabo.core.factory.HandelFactory;

/**
 * 
 *
 * @author online.sanen <br>
 *         Date:2017年11月30日 <br>
 *         Time:下午8:37:20
 */
public class QuerySqlDevice implements QuerySql {

	ChannelContext context;

	public QuerySqlDevice(Manager manager, String sql, Object... paramers) {
		context = new ChannelContext(manager);
		context.setSql(sql);
		context.setCls(this.getClass());
		addParamers(paramers);
	}

	@Override
	public QuerySql addParamer(Object... paramers) {

		if (paramers == null)
			throw new ConditionException(String.format("Paramer is null"));

		for (Object paramer : paramers) {
			context.addParamer(paramer);
		}

		return this;
	}

	@Override
	public int update() {

		return (int) Assembler.instance().create(QueryType.update, ResultType.Int, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> list() {
		return (List<T>) Assembler.instance().create(QueryType.select, ResultType.List, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
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
						pipeline.addLast(HandelFactory.conditionHandel());
						pipeline.addLast(HandelFactory.resultHandel());
						pipeline.addLast(HandelFactory.debugHandel());
						return pipeline;
					}
				});
	}

	@Override
	public List<Map<String, Object>> maps(HashMap<String, String> alias) {
		Assert.notNullOrEmpty(alias, "Alias is null or empty");

		context.setAlias(alias);
		return this.maps();
	}

	@Override
	public void stream(int bufferSize, Consumer<List<Map<String, Object>>> consumer) {

		Assembler.instance().create(QueryType.select, ResultType.Maps, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.conditionHandel());
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
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.streamHandel(bufferSize, consumer, null));
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
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.streamHandel(bufferSize, consumer, datas, aliases));
				return pipeline;
			}
		});

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> stream(int count) {

		return (List<Map<String, Object>>) Assembler.instance().create(QueryType.select, ResultType.Maps, context,
				new PipelineFactory() {

					@Override
					public Pipeline getPipeline() {
						Pipeline pipeline = new PipelineDivice();
						pipeline.addLast(HandelFactory.conditionHandel());
						pipeline.addLast(HandelFactory.streamHandel(count));
						return pipeline;
					}

				});
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> map() {
		return (Map<String, Object>) Assembler.instance().create(QueryType.select, ResultType.Map, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unique() {

		return (T) Assembler.instance().create(QueryType.select, ResultType.Object, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}
	
	

	private void addParamers(Object[] paramers) {
		if (paramers != null) {
			for (int i = 0; i < paramers.length; i++) {
				addParamer(paramers[i]);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Column> getQueryColumns() {

		return (List<Column>) Assembler.instance().create(QueryType.select, ResultType.DataField, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.resultColumnsHandel());
				return pipeline;
			}
		});

	}

	@Override
	public <T> List<T> list(Class<T> entityClass) {
		context.setEntityClass(entityClass);
		return list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> entities(Class<T> entityClass) {

		context.setEntityClass(entityClass);

		return (List<T>) Assembler.instance().create(QueryType.select, ResultType.Beans, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

	@Override
	public <T> T unique(Class<T> entityClass) {
		context.setEntityClass(entityClass);
		return unique();
	}

	
	@Override
	public <T> T entity(Class<T> entityClass) {
		return unique(entityClass);
	}
	
	@Override
	public QuerySql addCondition(String fieldName, Cs cs) {
		context.addCondition(buid(fieldName, cs));
		return this;
	}

	@Override
	public Object addCondition(String fieldName, Cs cs, Associated associated) {
		context.addCondition(buid(fieldName, cs, associated));
		return this;
	}

	@Override
	public Object addCondition(String fieldName, Cs cs, Object value, Associated associated) {
		context.addCondition(buid(fieldName, cs, value, associated));
		return this;
	}

	@Override
	public QuerySql addCondition(String fieldName, Cs cs, Object value) {
		context.addCondition(buid(fieldName, cs, value));
		return this;
	}

	@Override
	public QuerySql addCondition(Consumer<List<Condition>> consumer) {

		consumer.accept(context.getConditions());
		return this;
	}

	@Override
	public QuerySql addCondition(Condition cond) {

		if (cond == null)
			return this;

		context.addCondition(cond);
		return this;
	}

	

}
