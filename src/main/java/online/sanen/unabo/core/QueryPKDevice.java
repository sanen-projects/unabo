package online.sanen.unabo.core;

import online.sanen.unabo.api.PipelineFactory;
import online.sanen.unabo.api.QueryPK;
import online.sanen.unabo.api.component.Manager;
import online.sanen.unabo.api.component.Pipeline;
import online.sanen.unabo.api.component.PipelineDivice;
import online.sanen.unabo.api.structure.ChannelContext;
import online.sanen.unabo.api.structure.enums.QueryType;
import online.sanen.unabo.api.structure.enums.ResultType;
import online.sanen.unabo.core.factory.HandelFactory;

/**
 * @author online.sanen Date: 2017/11/23 Time： 9:39
 */
public class QueryPKDevice<T> implements QueryPK<T> {
	ChannelContext context;

	public QueryPKDevice(Manager manager, Class<T> entryCls, Object primarykey) {
		context = new ChannelContext(manager);
		context.setEntityClass(entryCls);
		context.getPrimaryKey().setValue(primarykey);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T unique() {

		return (T) Assembler.instance().create(QueryType.select, ResultType.Bean, context, new PipelineFactory() {
			@Override
			public Pipeline getPipeline() {
				Pipeline pipeline = new PipelineDivice();
				pipeline.addLast(HandelFactory.commonFieldHandel());
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
	public int delete() {

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

}
