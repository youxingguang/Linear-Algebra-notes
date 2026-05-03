import ilog.cplex.*;
import ilog.concert.*;


public class CutCallBack {

	
	//IloCplex.CallBack 说明https://www.ibm.com/docs/en/icos/22.1.1?topic=c-ilocplexcallback-2
	
	//facility.dat 数据
	
	private static double[] fixedCost= {480,200,320,340,300};
	private static double[][] cost= {{24, 74, 31, 51, 84},{57, 54, 86, 61, 68},
			{57, 67, 29, 91, 71},{54, 54, 65, 82, 94},
			{98, 81, 16, 61, 27},{13, 92, 34, 94, 87},
			{54, 72, 41, 12, 78},{54, 64, 65, 89, 89}};
	
	private static double eps=1.0e-6;
	/*
	 *  问题 min sum(j) fixedCost[j]*used[j]+ sum(c)sum(j) cost[c][j]*supply[c][j]
	 *   s.t. sum(j) supply[c][j]=1                 (1)
	 *        sum(c) supply[c][j]<=(|C|-1)*used[j]  (2)
	 *        supply, used {0,1}                    (3)
	 */
	

	/*
	 *  在UserCuts 检查 supply[c][j]<=used[j] 一个客户仅能由一个设施服务
	 */
	public static class UserCuts extends IloCplex.UserCutCallback
	{
		private final IloModeler model;
		private final IloNumVar[] used;
		private final IloNumVar[][] supply;
		
		public UserCuts(IloModeler model,IloNumVar[] used,IloNumVar[][] supply)
		{
			this.model=model;
			this.used=used;
			this.supply=supply;
		}
		
		//必须实现IloCplex.UserCutCallback的抽象方法 main
		//判断约束 
		public void main() throws IloException
		{
			int nLocations=used.length;
			int nClients=supply.length;
			for(int i=0;i<nClients;i++)
			{
				for(int j=0;j<nLocations;j++)
				{
					double lhs=getValue(supply[i][j]);
					double rhs=getValue(used[j]);
					if(lhs>rhs+eps)
					{
						IloRange cut=model.le(model.diff(supply[i][j],used[j]),0.0);
						System.out.printf("%s: (%d,%d) 添加切割平面: %s%n", getNodeId(), i, j, cut.toString());
						//添加      UseCutPurge策略 当检查不再有效时，允许清除切割
						add(cut,IloCplex.CutManagement.UseCutPurge);
					}
					
				}
			}
		}
	}
	
	/*
	 *  在 LazyConstraints 检查约束(2)
	 */
	public static class LazyConstraints extends IloCplex.LazyConstraintCallback
	{
		
		private final IloModeler model;
		private final IloNumVar[] used;
		private final IloNumVar[][] supply;
		
		public LazyConstraints (IloModeler model,IloNumVar[] used,IloNumVar[][] supply)
		{
			this.model=model;
			this.used=used;
			this.supply=supply;
		}
		
		public void main() throws IloException
		{
			int nLocations=used.length;
			int nClients=supply.length;
			
			
			for(int j=0;j<nLocations;j++)
			{
				double selected=getValue(used[j]);
				double served=0.0;
				for(int i=0;i<nClients;i++)
				{
					served+=getValue(supply[i][j]);
				}
				if(served>=(nClients-1)*selected+eps)
				{
					IloLinearNumExpr v=model.linearNumExpr();
					for(int i=0;i<nClients;i++)
					{
						v.addTerm(1.0, supply[i][j]);
					}
					
					v.addTerm(-(nClients-1), used[j]);
					
					System.out.println(String.format("添加 懒约束 %s<=0", v.toString()));
					add(model.le(v, 0.0));
				}
			}
			
		}
	}
	public static void main(String[] args) {
		
		try 
		{
			int nLocations=fixedCost.length;//设施数
			int nClients=cost.length;//客户数
			
			IloCplex model=new IloCplex();
			boolean lazyCall=false;
			boolean userCall=true;
			
			IloNumVar[] used=model.boolVarArray(nLocations);
			IloNumVar[][] supply=new IloNumVar[nClients][nLocations];
			
			for(int i=0;i<nClients;i++)
			{
				supply[i]=model.boolVarArray(nLocations);
			}
			//约束1
			for(int i=0;i<nClients;i++)
			{
				model.addEq(model.sum(supply[i],0,supply[i].length), 1);
			}
			
			//约束2的添加
			if(!lazyCall)
			{
				//当不使用 惰性回调 才添加到模型
				for(int j=0;j<nLocations;j++)
				{
					IloLinearNumExpr v=model.linearNumExpr();
					for(int i=0;i<nClients;i++)
					{
						v.addTerm(1.0, supply[i][j]);
					}
					model.addLe(v, model.prod(nClients-1, used[j]));
				}
			}
			
			//目标函数
			IloLinearNumExpr obj=model.scalProd(fixedCost, used);
			for(int i=0;i<nClients;i++)
			{
				obj.add(model.scalProd(cost[i], supply[i]));
			}
			model.addMinimize(obj);
			
			//调整参数 使模型自动内置割的使用，使自实现割回调能更明显
			 model.setParam(IloCplex.Param.Threads, 1);
	         model.setParam(IloCplex.Param.MIP.Strategy.HeuristicFreq, -1);
	         model.setParam(IloCplex.Param.MIP.Cuts.MIRCut, -1);
	         model.setParam(IloCplex.Param.MIP.Cuts.Implied, -1);
	         model.setParam(IloCplex.Param.MIP.Cuts.Gomory, -1);
	         model.setParam(IloCplex.Param.MIP.Cuts.FlowCovers, -1);
	         model.setParam(IloCplex.Param.MIP.Cuts.PathCut, -1);
	         model.setParam(IloCplex.Param.MIP.Cuts.LiftProj, -1);
	         model.setParam(IloCplex.Param.MIP.Cuts.ZeroHalfCut, -1);
	         model.setParam(IloCplex.Param.MIP.Cuts.Cliques, -1);
	         model.setParam(IloCplex.Param.MIP.Cuts.Covers, -1);

			
			
			//使用callback
			if(userCall)
			{
				model.use(new UserCuts(model,used,supply));
			}
			
			if(lazyCall)
			{
				model.use(new LazyConstraints(model,used,supply));
			}
			
			if(model.solve())
			{
				System.out.println("解的状态："+model.getStatus());
				for(int i=0;i<nLocations;i++)
				{
					System.out.println("used["+i+"]="+model.getValue(used[i]));
				}
				for(int i=0;i<nClients;i++)
				{
					for(int j=0;j<nLocations;j++)
					{
						System.out.println("supply["+i+"]["+j+"]="+model.getValue(supply[i][j]));
					}
				}
			}
			
			
		}catch(IloException e)
		{
			System.err.println("Concert exception "+e);
		}
	}

}
