package seckill;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*
 * 配置spring和junit整合，junit启动时加载springIOC容器
 * spring-test, junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml" })
public class SeckillServiceImplTest
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;

	@Test
	public void testGetSeckillList() throws Exception
	{
		List<Seckill> list = seckillService.getSeckillList();
		//占位符{}
		logger.info("list={}", list);
	}

	@Test
	public void testGetById() throws Exception
	{
		long seckillId = 1000;
		Seckill seckill = seckillService.getById(seckillId);
		logger.info("seckill={}", seckill);
		/*
		 * Seckill [
		 * seckillId=1000, 
		 * name=1000元秒杀iphone6, 
		 * number=100, 
		 * startTime=Fri Feb 03 00:00:00 CST 2017, 
		 * endTime=Sat Feb 04 00:00:00 CST 2017, 
		 * createTime=Mon Apr 10 19:56:03 CST 2017]
		 */
	}

	//测试代码完整逻辑，注意可重复执行。
	@Test
	public void testSeckillLogic() throws Exception
	{
		long seckillId = 1001;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if (exposer.isExposed())
		{
			//秒杀已开启
			logger.info("exposer={}", exposer);
			long phone = 13974526398L;
			String md5 = exposer.getMd5();
			try
			{
				SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phone,
						md5);
				logger.info("result={}", seckillExecution.toString());
			}
			catch (RepeatKillException e)
			{
				logger.error(e.getMessage());
				;
			}
			catch (SeckillCloseException e)
			{
				logger.error(e.getMessage());
			}
		}
		else
		{
			//未开启
			logger.warn("exposer={}", exposer);
		}

	}

	@Test
	public void executeSeckillProcedure()
	{
		long seckillId = 1003;
		long userPhone = 13761565415L;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if (exposer.isExposed())
		{
			String md5 = exposer.getMd5();
			SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId,
					userPhone, md5);
			logger.info(execution.getStateInfo());

		}
	}

}
