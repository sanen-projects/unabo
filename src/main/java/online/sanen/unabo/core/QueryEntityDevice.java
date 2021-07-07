package online.sanen.unabo.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import com.mhdt.toolkit.Reflect;

import online.sanen.unabo.api.PipelineFactory;
import online.sanen.unabo.api.QueryEntity;
import online.sanen.unabo.api.QueryUpdate;
import online.sanen.unabo.api.component.Manager;
import online.sanen.unabo.api.component.Pipeline;
import online.sanen.unabo.api.component.PipelineDivice;
import online.sanen.unabo.api.condition.C;
import online.sanen.unabo.api.condition.Condition;
import online.sanen.unabo.api.condition.Condition.Associated;
import online.sanen.unabo.api.condition.Condition.Cs;
import online.sanen.unabo.api.exception.QueryException;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.enums.QueryType;
import online.sanen.unabo.api.structure.enums.ResultType;
import online.sanen.unabo.core.factory.HandelFactory;
import online.sanen.unabo.template.jpa.Id;
import online.sanen.unabo.template.jpa.JPA.Primarykey;

/**
 * 
 * @author online.sanen <br>
 *         Date: 2017/11/25 <br>
 *         Time： 9：24
 */
public class QueryEntityDevice implements QueryEntity {

	ChannelContext context;

	public QueryEntityDevice(Manager manager, Object entry) {
		context = new ChannelContext(manager);
		context.setEntity(entry);
	}

	public QueryEntityDevice(Manager manager, Collection<?> entrys) {
		context = new ChannelContext(manager);
		context.setEntities(entrys);
	}

	@Override
	public QueryEntity setTableName(String tableName) {
		context.setTableName(tableName);
		return this;
	}

	@Override
	public QueryEntity setFields(String... fields) {

		if (fields == null)
			return this;

		context.setFields(Arrays.asList(fields));
		return this;
	}

	@Override
	public QueryEntity setExceptFields(String... fields) {

		if (fields == null)
			return this;

		context.setExceptes(new HashSet<String>(Arrays.asList(fields)));
		return this;
	}

	@Override
	public int insert() {

		if (Reflect.hasAnnotation(context.getEntityClass(), Id.class))
			throw new QueryException(
					"An entity class must have a field that is a primary key,Decorate the primary key with an <code>@Id</code> annotation");

		if (context.getEntities() != null)
			return batchUpdate(QueryType.insert);

		return (int) Assembler.instance().create(QueryType.insert, ResultType.Int, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {

				Pipeline pipeline = new PipelineDivice();

				pipeline.addLast(HandelFactory.commonFieldHandel());
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());

				return pipeline;

			}
		});
	}

	@Override
	public int delete() {

		if (context.getEntities() != null)
			return batchUpdate(QueryType.delete);

		return (int) Assembler.instance().create(QueryType.delete, ResultType.Int, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.primaryKeyHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

	@Override
	public int updateBy(String column) {
		context.setPrimaryKey(new Primarykey(Reflect.getField(context.getEntity(), column)));
		return this.update();
	}

	@Override
	public int update() {

		if (context.getEntities() != null)
			return batchUpdate(QueryType.update);

		return (int) Assembler.instance().create(QueryType.update, ResultType.Int, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.commonFieldHandel());
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
				pipeline.addLast(HandelFactory.primaryKeyHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

	private int batchUpdate(QueryType type) {
		return (int) Assembler.instance().create(type, ResultType.Int, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.commonFieldHandel());
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.batchUpdate());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

	@Override
	public QueryUpdate addCondition(String fieldName, Cs cs) {
		context.addCondition(C.buid(fieldName, cs));
		return new QueryUpdateDevice(context);
	}

	@Override
	public QueryUpdate addCondition(String fieldName, Cs cs, Object value, Associated associated) {
		context.addCondition(C.buid(fieldName, cs, associated));
		return new QueryUpdateDevice(context);
	}

	@Override
	public QueryUpdate addCondition(String fieldName, Cs cs, Associated associated) {
		context.addCondition(C.buid(fieldName, cs, associated));
		return new QueryUpdateDevice(context);
	}

	@Override
	public QueryUpdate addCondition(String fieldName, Cs cs, Object value) {
		context.addCondition(C.buid(fieldName, cs, value));
		return new QueryUpdateDevice(context);
	}

	@Override
	public QueryUpdate addCondition(Condition cond) {
		context.addCondition(cond);
		return new QueryUpdateDevice(context);
	}

	@Override
	public QueryUpdate addCondition(Consumer<List<Condition>> consumer) {
		consumer.accept(context.getConditions());
		return new QueryUpdateDevice(context);
	}

	@Override
	public int create() {

		return (int) Assembler.instance().create(QueryType.create, ResultType.Int, context, new PipelineFactory() {
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

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unique() {

		context.setResultType(ResultType.Object);

		return (T) Assembler.instance().create(QueryType.select, ResultType.Object, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.commonFieldHandel());
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.entityConditionHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
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
				pipeline.addLast(HandelFactory.commonFieldHandel());
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.entityConditionHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
				pipeline.addLast(HandelFactory.limitHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

}
