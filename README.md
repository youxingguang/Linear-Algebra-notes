# Linear-Algebra-notes

线性代数笔记

1. 高斯消元求逆矩阵
```
  高斯消元求逆
	   狭义求逆 仅适用n阶方阵
	   1.构建增广矩阵 M=[A|I]  I是n阶单位矩阵 
	   2. 寻找主元  对于第i列  找到[i,n-1]行中绝对值最大元素所在行 pivot_row
	   	若M[pivot_row][i]==0 i列 中i行及以下全为0  不可逆
	   	若M[pivot_row][i]!=0 交换第i行和第pivot_row行
	   3.归一化 将i行所有元素除以M[i][i], 使主元变为1
	   4.消元  对除i外的每一行k的[0:n-1]  row(k)=row(k)-M[k][i]*row[i] (k!=i)
	   5.提取 A_inv  
 ```

注：寻找主元，实际过程是在对角线在放置一个不为0的元素； 上述流程使用行主元，不会改变变量的位置，后续便于求解线性方程。若使用列主元，交换列影响变量位置。

代码实现-GaussianElimination.java

2. 求解线性规划后提取 最终单纯形表

   代码实现-ExtractBasic.java

   测试例子 见Integer programming 8.11,打印的变量系数对应 最终单纯形表达的系数矩阵。

3. 利用CGLP 构造析取不等式(disjunctive inequality)

   代码实现见-ValidIneq1.java

4.  1:1翻译 cplex AddMIPex5.java
   
    内容:使用callback 实现user cuts 和 lazy constraints
      
    对应CutCallBack.java 
   


   

