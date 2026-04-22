public class GaussianElimination {
  public double[][]  GE(double [][] A)
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
  
  public void printMatrix(double[][] matrix)
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
  
}
