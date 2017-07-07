package org.seckill.dto;

import org.seckill.entity.Successkilled;
import org.seckill.enums.SeckillStateEnum;

/**
 * 封装秒杀后执行结果
 * @author Administrator
 *
 */
public class SeckillExecution
{

	private long SeckillId;

	@Override
	public String toString()
	{
		return "SeckillExecution [SeckillId=" + SeckillId + ", state=" + state + ", stateInfo="
				+ stateInfo + ", successkilled=" + successkilled + "]";
	}

	//执行秒杀结果状态
	private int state;

	//状态表示
	private String stateInfo;

	//秒杀成功对象
	private Successkilled successkilled;

	public long getSeckillId()
	{
		return SeckillId;
	}

	public void setSeckillId(long seckillId)
	{
		SeckillId = seckillId;
	}

	public int getState()
	{
		return state;
	}

	public void setState(int state)
	{
		this.state = state;
	}

	public String getStateInfo()
	{
		return stateInfo;
	}

	public void setStateInfo(String stateInfo)
	{
		this.stateInfo = stateInfo;
	}

	public Successkilled getSuccesskilled()
	{
		return successkilled;
	}

	public void setSuccesskilled(Successkilled successkilled)
	{
		this.successkilled = successkilled;
	}

	//成功
	public SeckillExecution(long seckillId, SeckillStateEnum stateEnum, Successkilled successkilled)
	{
		super();
		SeckillId = seckillId;
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.successkilled = successkilled;
	}

	//失败
	public SeckillExecution(long seckillId, SeckillStateEnum stateEnum)
	{
		super();
		SeckillId = seckillId;
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

}
