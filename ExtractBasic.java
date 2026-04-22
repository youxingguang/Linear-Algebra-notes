//提取最终单纯形表
public class ExtractBasic {

	public static void main(String[] args) {
		
		try
		{
			IloCplex model=new IloCplex();
			IloNumVar[] x=model.numVarArray(2, 0,Double.MAX_VALUE);
			
			double[][] A= {{7,-2},{0,1},{2,-2}};
			double[] b= {14,3,3};
			
			IloRange[] cons=new IloRange[A.length];
			for(int i=0;i<A.length;i++)
			{
				cons[i]=model.addLe(model.scalProd(A[i], x), b[i]);
			}
			double[] c= {4,-1};
			IloNumExpr obj=model.scalProd(c, x);
			model.addMaximize(obj);
			
			
			if(model.solve())
			{
				int nVars=x.length+A.length;//总变量数 原变量+松弛变量（对应约束）
				int[] varStatus=new int[nVars];//1-基变量  0-非基变量
				
				int id=0;
				int BVN=0;//基变量个数
				for(int i=0;i<x.length;i++)
				{
					//Basic-基变量  AtLower-处于下界  AtUpper-处于上界 
					if(model.getBasisStatus(x[i])==IloCplex.BasisStatus.Basic)
					{
						varStatus[id]=1;
						BVN++;
					}
					++id;
				}
				for(int i=0;i<A.length;i++)
				{
					if(model.getBasisStatus(cons[i])==IloCplex.BasisStatus.Basic)
					{
						varStatus[id]=1;
						BVN++;
					}
					++id;
				}
				
				//从augA提取最终基变量系数 构成基矩阵B
				
				//补全A  到augA
				double[][] augA=new double[A.length][nVars];
				for(int i=0;i<A.length;i++)
				{
					System.arraycopy(A[i], 0, augA[i], 0, x.length);
					augA[i][x.length+i]=1;
				}
				
				double[][] B=new double[BVN][BVN];
				int k=0;
				for(int j=0;j<nVars;j++)
				{
					if(varStatus[j]==1)
					{
						for(int i=0;i<A.length;i++)
						{
							B[i][k]=augA[i][j];
						}
						k++;
					}
				}
				
				
				
				//矩阵求逆
				double[][] invB=GE(B);
				System.out.println("打印B的逆矩阵：");
				printMatrix(invB);
				
				System.out.println("打印增广A：");
				printMatrix(augA);
				
				double[][] C=matrixMulti(invB,augA);
				System.out.println("打印变量的系数：");
				printMatrix(C);
			
				
	
			}
			
			model.end();
		}catch(IloException e)
		{
			System.err.println("Concert exception "+e);
		}

	}
	
	//高斯消元求逆
	public static double[][]  GE(double [][] A)
	{
		int m=A.length;
		int n=A[0].length;
		if(m!=n) throw new IllegalArgumentException("matrix is not square matrix");//不是方阵
		
		double[][] augA=new double[n][n*2];//构建增广矩阵 A|I
		for(int i=0;i<n;i++)
		{
			System.arraycopy(A[i], 0, augA[i], 0, n);
			augA[i][i+n]=1;
		}
		
		for(int i=0;i<n;i++)
		{
			//在i列 寻找最大值所在行
			double maxV=Math.abs(augA[i][i]);
			int maxRowId=i;
			for(int j=i+1;j<n;j++)
			{
				if(maxV<Math.abs(augA[j][i]))
				{
					maxV=Math.abs(augA[j][i]);
					maxRowId=j;
				}
			}
			
			//因为数值精度 小于1e-5就视为0 
			if(maxV<1e-5) throw new IllegalArgumentException("matrix is ​​not invertible");//不可逆
			
			//交换两行 i<->rowId
			if(i!=maxRowId)
			{
				for(int j=0;j<n*2;j++)
				{
					double tmp=augA[i][j];
					augA[i][j]=augA[maxRowId][j];
					augA[maxRowId][j]=tmp;
				}
			}
			
			//归一化处理
			double div=augA[i][i];
			for(int l=0;l<n*2;l++)
			{
				augA[i][l]/=div;
			}
			
			//消元
			for(int row=0;row<n;row++)
			{
				if(row==i) continue;
				double t=augA[row][i];
				if(t==0) continue;
				for(int j=i;j<n*2;j++)
				{
					augA[row][j]-=t*augA[i][j];
				}
			}
			
		}
		//提取逆矩阵
		double[][] ans=new double[n][n];
		for(int i=0;i<n;i++)
		{
			System.arraycopy(augA[i], n, ans[i], 0, n);
		}
		return ans;	
	}
	public static void printMatrix(double[][] matrix)
	{
		for(int i=0;i<matrix.length;i++)
		{
			for(int j=0;j<matrix[0].length;j++)
			{
				System.out.printf("%.4f\t",matrix[i][j]);
			}
			System.out.println();
		}
	}
	
	public static double[][] matrixMulti(double[][] A,double[][] B)
	{
		//测量长度
		int m1=A.length;
		int n1=A[0].length;
		
		int m2=B.length;
		int n2=B[0].length;
		
		if(n1!=m2) throw new IllegalArgumentException("matrices cannot be multiplied");//矩阵无法相乘
		
		//矩阵相乘
		double[][] ans=new double[m1][n2];
		for(int i=0;i<m1;i++)
		{
			
			for(int col=0;col<n2;col++)
			{
				double v=0;
				for(int j=0;j<n1;j++)
				{
					v+=A[i][j]*B[j][col];
				}
				ans[i][col]=v;
			}
		}
		return ans;
	}

}
