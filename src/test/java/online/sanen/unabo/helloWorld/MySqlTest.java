package online.sanen.unabo.helloWorld;

import static online.sanen.unabo.api.condition.C.composite;
import static online.sanen.unabo.api.condition.C.eq;

import java.util.Map;

import online.sanen.unabo.api.Bootstrap;
import online.sanen.unabo.api.condition.Condition.Associated;
import online.sanen.unabo.api.structure.Configuration.DataSouseType;
import online.sanen.unabo.api.structure.Configuration.TransactionFactoryEnum;
import online.sanen.unabo.api.structure.enums.DriverOption;
import online.sanen.unabo.core.factory.Unabo;

public class MySqlTest {

	public static void main(String[] args) {

		Bootstrap bootstrap = Unabo.load("default", configuration -> {
			configuration.setUrl(
					"jdbc:mysql://127.0.0.1:3306/test?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
			configuration.setDriverOption(DriverOption.MYSQL_CJ);
			configuration.setDataSouseType(DataSouseType.HikariCP);
			configuration.setUsername("zs");
			configuration.setPassword("123456");
			configuration.setShowSql(true);
			configuration.setTransactionFactory(TransactionFactoryEnum.JdbcTransactionFactory);
		});


//		bootstrap.createSQL("select * from user").addCondition(eq("id", 1)).unique();
//		bootstrap.createSQL("select * from user").addCondition(eq("id", 1)).addCondition(eq("name", "张三", Associated.OR)).unique();
//		bootstrap.createSQL("select * from user").addCondition(conds->{
//			conds.add(eq("id", 1));
//			conds.add(eq("name", "张三", Associated.OR));
//		}).unique();
//		
//		bootstrap.createSQL("select * from user").addCondition(conds->{
//			conds.add(composite(composite->{
//				composite.add(eq("id", 1));
//				composite.add(eq("name", "张三", Associated.OR));
//			}));
//		}).unique();
//		
		
//		bootstrap.createSQL("update user set name=? where id=?","张三",1).update();
//		bootstrap.createSQL("update user set name=?","张三").addCondition(eq("id", 1)).update();
		
		Map<String, Object> map = bootstrap.createSQL("select * from user where id=?",1).map();
		map.put("name", "张三");
		
		//3条语句都是等效的，第二条是第一条的简写通过指定主键的方式修改；
		bootstrap.queryMap("user", map).update("id");
		bootstrap.queryMap("user", map).setPrimary("id").update();
		//自定义条件修改
		bootstrap.queryMap("user", map).setFields(args).update(eq("id", 1));
	}

}
