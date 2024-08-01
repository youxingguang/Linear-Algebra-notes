 public  void solve(double[][] matrix) 
	    {
	        int rowNum= matrix.length;
	        int colNum=matrix[0].length;
	        //如果colNum>rowNum+1，则需要补全矩阵
	        if(colNum>rowNum+1)
	        {
		        double[][] completeMat=new double[colNum-1][colNum];
		        for(int row=0;row<rowNum;row++)
		        {
		        	for(int col=0;col<colNum;col++)
		        	{
		        		completeMat[row][col]=matrix[row][col];
		        	}
		        }
		       matrix=new double[colNum-1][colNum];
		       matrix=completeMat;
		       rowNum= matrix.length;
		       colNum=matrix[0].length;
	        }
	        // 转换为上三角矩阵
	        for (int i = 0; i <rowNum; i++) 
	        {
	            // 找到第i列中绝对值最大的元素
	            int max = i;
	            for (int j = i + 1; j < rowNum; j++)
	            {
	                if (Math.abs(matrix[j][i]) > Math.abs(matrix[max][i])) 
	                {
	                    max = j;
	                }
	            }
	            //交换当前行和绝对值最大元素所在的行
	            double[] temp = matrix[i];
	            matrix[i] = matrix[max];
	            matrix[max] = temp;
	            /*
	             * 	如果主对角线元素为0，分类讨论
			     * 	当前行全为0，常数项也为0，无穷多解；
			     *	当前行全为0，常数项不为0，无解；
			     * 	当前行不全为0，将Matrix[i][i]的后右一位视作变元，向下消0
	             */
	            if (Math.abs(matrix[i][i]) <= 1e-10) 
	            {
	                boolean allZeroes = true;
	                for (int k = i; k < colNum - 1; k++) 
	                {
	                    if (Math.abs(matrix[i][k]) > 1e-10) 
	                    {
	                        allZeroes = false;
	                        break;
	                    }
	                }
	                //若系数矩阵当前行全为0
	                if (allZeroes)
	                {
	                    if (Math.abs(matrix[i][colNum- 1]) > 1e-10) 
	                    {
	                    	printMatrix(matrix);
	                        System.out.println("No solution exists");
	                    } else 
	                    {
	                    	printMatrix(matrix);
	                        System.out.println("Infinite solutions exist");
	                    }
	                    return;
	                }
	               
	              //将Matrix[i][i]的后右一位视作变元，向下消0
	                for(int row=i+1;row<rowNum;row++)
	                {
	                	double factor = matrix[row][row] / matrix[i][row];
		                for (int k = i+1; k < colNum; k++) 
		                {
		                    matrix[row][k] -= factor * matrix[i][k];
		                }
	                }
	                continue;
	                
	            } 
	            // 消去第i列下面的所有元素
	            for (int j = i + 1; j < rowNum; j++) 
	            {
	            	
	                double factor = matrix[j][i] / matrix[i][i];
	                for (int k = i; k < colNum; k++) 
	                {
	                    matrix[j][k] -= factor * matrix[i][k];
	                }
	            }
	        }
	        //对于rowNum>colNum,判断是否存在系数矩阵某一行为0，但常数项不为0
	        if(!judge(matrix))
	        {
	        	printMatrix(matrix);
                System.out.println("No solution exists");
                return;
	        }
	      

	        // 回代求解
	        double[] solution = new double[colNum-1];
	        for (int i = rowNum - 1; i >= 0; i--) 
	        {
	            double sum = 0.0;
	            for (int j = i + 1; j < colNum-1; j++)
	            {
	                sum += matrix[i][j] * solution[j];
	            }
	            solution[i] = (matrix[i][colNum-1] - sum) / matrix[i][i];
	        }
	        // 输出解
	        System.out.println("Solution:");
	        for (int i = 0; i < rowNum; i++) 
	        {
	            System.out.printf("x%d = %.4f\n", i + 1, solution[i]);
	        }
	  
	}
     public void printMatrix(double[][] matrix)
     {
    	 int rowNum= matrix.length;
	     int colNum=matrix[0].length;
	     for(int row=0;row<rowNum;row++)
	        {
	        	for(int col=0;col<colNum;col++)
	        	{
	        		System.out.printf("%.4f\t",matrix[row][col]);
	        	}
	        	System.out.println();
	        }
     }
     public boolean judge(double[][] matrix)
     {
    	 
    	 int rowNum= matrix.length;
	     int colNum=matrix[0].length;
	     for(int row=rowNum-1;row>=0;row--)
	        {
	    	 	boolean allZeroes = true;
	        	for(int col=0;col<colNum-1;col++)
	        	{
	        		if(Math.abs(matrix[row][col])>1e-10)
	        		{
	        			 allZeroes=false;
	        			 break;
	        		}
	        	}
	        	if(allZeroes&&Math.abs(matrix[row][colNum-1])>1e-10)
	        	{
	        		return false;//无解
	        	}
	        	
	        }
	    return true;//有解
    	 
     }
