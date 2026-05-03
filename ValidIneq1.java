import ilog.concert.*;
import ilog.cplex.*;

public class ValidIneq1 {
	
	/*
	 * Integer programming 8.15例子 计算u
	 */
	public static void main(String[] args) {
		
		//测试有效不等式
		try {
			IloCplex model=new IloCplex();
			IloNumVar[] gamma=model.numVarArray(2, -Double.MAX_VALUE,Double.MAX_VALUE);
			IloNumVar gamma_0=model.numVar(-Double.MAX_VALUE,Double.MAX_VALUE);
			IloNumVar[] u1=model.numVarArray(2, 0,Double.MAX_VALUE);
			IloNumVar[] u2=model.numVarArray(3, 0,Double.MAX_VALUE);
			
			//gamma <=u^i A^i
			double[][] A1= {{-1,1},{1,1}};
			double[] b1= {1,5};
			
			double[][] A2= {{0,-2,1},{1,1,-3}};
			double[] b2= {4,-6,-2};
			
			for(int i=0;i<A1.length;i++)
			{
				model.addLe(gamma[i], model.scalProd(A1[i],u1));
				model.addLe(gamma[i], model.scalProd(A2[i],u2));
			    
			}
			
			model.addGe(gamma_0, model.scalProd(u1, b1));
			model.addGe(gamma_0, model.scalProd(u2, b2));
			double[] n1= {1,1};
			double[] n2= {1,1,1};
			model.addEq(model.sum(model.scalProd(n1, u1),model.scalProd(n2, u2)),1);
			
			//假设 x=(4,3)
			double[] x= {4,3};
			IloNumExpr obj=model.diff(model.scalProd(gamma, x), gamma_0);
			model.addMaximize(obj);
			if(model.solve())
			{
				System.out.println(model.getStatus());
				for(int i=0;i<u1.length;i++)
				{
					System.out.println("u1["+i+"]="+model.getValue(u1[i]));
				}
				for(int i=0;i<u2.length;i++)
				{
					System.out.println("u2["+i+"]="+model.getValue(u2[i]));
				}
			}
			
			
		}catch(IloException e)
		{
			System.out.println("Concert exception caught:"+e);
		}
		

	}

}
