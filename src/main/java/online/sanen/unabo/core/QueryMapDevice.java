package online.sanen.unabo.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import com.mhdt.degist.Validate;

import online.sanen.unabo.api.PipelineFactory;
import online.sanen.unabo.api.QueryMap;
import online.sanen.unabo.api.component.Manager;
import online.sanen.unabo.api.component.Pipeline;
import online.sanen.unabo.api.component.PipelineDivice;
import online.sanen.unabo.api.condition.Condition;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.enums.QueryType;
import online.sanen.unabo.api.structure.enums.ResultType;
import online.sanen.unabo.core.factory.HandelFactory;
import online.sanen.unabo.template.jpa.JPA.Primarykey;

/**
 * 
 * @author LazyToShow Date: 2018/06/12 Time: 09:17
 */
public class QueryMapDevice implements QueryMap {

	ChannelContext context;

	public QueryMapDevice(Manager manager, String tableName, Map<String, Object> entry) {
		context = new ChannelContext(manager);
		context.setTableName(tableName);
		context.setEntityMap(entry);
	}

	public <T extends Map<String, Object>> QueryMapDevice(Manager manager, String tableName, Collection<T> maps) {
		context = new ChannelContext(manager);
		context.setTableName(tableName);
		context.setEntityMaps(maps);
	}

	@Override
	public int insert() {

		if (context.getEntityMaps() != null)
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

		if (context.getPrimaryKey() == null)
			throw new NullPointerException(
					"Primary key is null,Use the setPrimaryKey(String primary) method to set this ");

		if (context.getEntityMaps() != null)
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

	@Override
	public int update() {

		if (context.getPrimaryKey() == null && context.getConditions().isEmpty())
			throw new NullPointerException(
					"Primary key is null,Use the setPrimaryKey(String primary) method to set this ");

		if (context.getEntityMaps() != null)
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
	public int delete(String primary) {
		setPrimary(primary);
		return delete();
	}

	@Override
	public int update(String primary) {
		setPrimary(primary);
		return update();
	}

	@Override
	public QueryMap setFields(String... fields) {

		if (fields == null)
			return this;

		context.setFields(Arrays.asList(fields));
		return this;
	}

	@Override
	public QueryMap setExceptFields(String... fields) {

		if (fields == null)
			return this;

		context.setExceptes(new HashSet<>(Arrays.asList(fields)));
		return this;
	}

	@Override
	public QueryMap setPrimary(String primary) {

		if (Validate.isNullOrEmpty(primary))
			throw new NullPointerException("Primary key is null.");

		context.setPrimaryKey(new Primarykey(primary,
				context.getEntityMap() == null ? context.getEntityMaps().stream().findFirst().get().get(primary)
						: context.getEntityMap().get(primary)));

		return this;
	}

	@Override
	public int update(Condition... condition) {

		context.setConditions(Arrays.asList(condition));
		return update();
	}

	@Override
	public QueryMap setQualifier(boolean isOpen) {
		context.setQualifier(isOpen);
		return this;
	}

}
