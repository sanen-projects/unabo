package online.sanen.unabo.core;

import static online.sanen.unabo.api.condition.C.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import online.sanen.unabo.api.PipelineFactory;
import online.sanen.unabo.api.QueryUpdate;
import online.sanen.unabo.api.component.Pipeline;
import online.sanen.unabo.api.component.PipelineDivice;
import online.sanen.unabo.api.condition.Condition;
import online.sanen.unabo.api.condition.Condition.Associated;
import online.sanen.unabo.api.condition.Condition.Cs;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.enums.QueryType;
import online.sanen.unabo.api.structure.enums.ResultType;
import online.sanen.unabo.core.factory.HandelFactory;

/**
 * 
 * @author LazyToShow Date: 2018/06/12 Time: 09:17
 */
public class QueryUpdateDevice implements QueryUpdate {

	ChannelContext structure;

	public QueryUpdateDevice(ChannelContext structure) {
		this.structure = structure;
	}

	@Override
	public QueryUpdate setTableName(String tableName) {
		structure.setTableName(tableName);
		return this;
	}

	@Override
	public QueryUpdate setFields(String... fields) {

		if (fields == null)
			return this;

		structure.setFields(Arrays.asList(fields));
		return this;
	}

	@Override
	public QueryUpdate setExceptFields(String... fields) {

		if (fields == null)
			return this;

		structure.setFields(Arrays.asList(fields));
		return this;
	}

	@Override
	public QueryUpdate addCondition(String fieldName, Cs cs) {
		structure.addCondition(buid(fieldName, cs));
		return this;
	}

	@Override
	public Object addCondition(String fieldName, Cs cs, Associated associated) {
		structure.addCondition(buid(fieldName, cs, associated));
		return this;
	}

	@Override
	public Object addCondition(String fieldName, Cs cs, Object value, Associated associated) {
		structure.addCondition(buid(fieldName, cs, value, associated));
		return this;
	}

	@Override
	public QueryUpdate addCondition(String fieldName, Cs cs, Object value) {
		structure.addCondition(buid(fieldName, cs, value));
		return this;
	}

	@Override
	public QueryUpdate addCondition(Consumer<List<Condition>> consumer) {

		consumer.accept(structure.getConditions());
		return this;
	}

	@Override
	public QueryUpdate addCondition(Condition cond) {

		if (cond == null)
			return this;

		structure.addCondition(cond);
		return new QueryUpdateDevice(structure);
	}

	@Override
	public int update() {
		return (int) Assembler.instance().create(QueryType.update, ResultType.Int, structure, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.commonFieldHandel());
				pipeline.addLast(HandelFactory.sqlHandel());
				pipeline.addLast(HandelFactory.paramerHandel());
				pipeline.addLast(HandelFactory.conditionHandel());
				pipeline.addLast(HandelFactory.resultHandel());
				pipeline.addLast(HandelFactory.debugHandel());
				return pipeline;
			}
		});
	}

}
